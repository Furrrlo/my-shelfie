import org.codehaus.plexus.archiver.zip.ZipArchiver
import org.codehaus.plexus.util.xml.Xpp3Dom
import org.codehaus.plexus.util.xml.Xpp3DomBuilder

import java.nio.file.Files
import java.nio.file.Path

import static org.twdata.maven.mojoexecutor.MojoExecutor.*

def applicationName = "My Shelfie"
def optsEnvironmentVar = "MY_SHELFIE_OPTS"
def defaultJvmOpts = ""
def distributions = [
        [name: "temurin19Linux", distribution: "temurin", platform: "linux-x86_64", version: "19.0.2", archiveType: "tar.gz"],
        [name: "temurin19Windows", distribution: "temurin", platform: "windows-x86_64", version: "19.0.2", archiveType: "zip"],
        [name: "temurin19OsxArm64", distribution: "temurin", platform: "osx-arm64", version: "19.0.2", archiveType: "tar.gz"],
]
def launchers = [
        [name: "my-shelfie", mainClass: "it.polimi.ingsw.client.Main", winNoConsole: true],
        [name: "my-shelfie-console", mainClass: "it.polimi.ingsw.client.Main", winNoConsole: false],
        [name: "my-shelfie-server", mainClass: "it.polimi.ingsw.server.Main", winNoConsole: false],
]

def jdksFolder = "${project.build.directory}/jdks"
def pluginManager = container.lookup("org.apache.maven.plugin.BuildPluginManager")
def projectHelper = container.lookup("org.apache.maven.project.MavenProjectHelper")

distributions.forEach {
    it.isNix = !it.platform.startsWith("win")
    it.dir = jdksFolder + "/" + it.name
    it.extractedDir = it.dir + "/extracted-jdk"
    it.jlinkedHome = "${project.build.directory}/maven-jlink/classifiers/${it.name}"
    // Clean the extracted dir
    new File(it.extractedDir).deleteDir()
}

println "Downloading jdks..."
executeMojo(
        plugin(
                groupId("org.jreleaser"),
                artifactId("jdks-maven-plugin"),
                version("1.8.0")),
        goal("setup-disco"),
        configuration(
                element(name("outputDirectory"), jdksFolder),
                // Unpacking is handled by a different plugin, see below
                element(name("unpack"), "false"),
                element("pkgs", distributions.collect {
                    element("pkg",
                            element("name", it.name),
                            element("distribution", it.distribution),
                            element("platform", it.platform),
                            element("version", it.version),
                            element("archiveType", it.archiveType))
                } as Element[])),
        executionEnvironment(project,
                session,
                pluginManager))

// Look for the downloaded files
distributions.forEach { distr ->
    distr.compressedFile = Files.list(Path.of(distr.dir))
            .filter { it.getFileName().toString().endsWith(".${distr.archiveType}") }
            .findFirst()
            .map { it.toAbsolutePath().toString() }
            .orElseThrow { new IOException("Couldn't find downloaded archive for ${distr.name}") }
    println "Found downloaded file for ${distr.name} -> ${distr.compressedFile}"
}

distributions.forEach { distr ->
    // Need to use a different plugin to extract .tar.gz files because of symlink handling
    // see https://github.com/maven-download-plugin/maven-download-plugin/issues/165
    println "Extracting jdk ${distr.name}..."
    executeMojo(
            plugin(
                    groupId("org.codehaus.mojo"),
                    artifactId("truezip-maven-plugin"),
                    version("1.2"),
                    // Bump the truezip version, as with the shipped one it does not extract all files from tar.gz files
                    // See https://github.com/mojohaus/truezip/pull/5
                    [dependency("de.schlichtherle.truezip", "truezip-driver-tar", "7.7.10")]),
            goal("copy"),
            configuration(
                    element("verbose", "true"),
                    element("fileset",
                            element("directory", distr.compressedFile),
                            element("outputDirectory", distr.extractedDir))),
            executionEnvironment(project,
                    session,
                    pluginManager))

    // Detect jdk home
    distr.extractedJdkHome = Files.walk(Path.of(distr.extractedDir))
            .filter {
                Files.isRegularFile(it.resolve("bin/java")) ||
                        Files.isRegularFile(it.resolve("bin/java.exe"))
            }
            .findFirst()
            .map { it.toAbsolutePath().toString() }
            .orElseThrow { new IOException("Couldn't find jdk home for ${distr.name}") }
    println "Found home path for ${distr.name} -> ${distr.extractedJdkHome}"

    def pluginWithPomVersion = { String groupId, String artifactId ->
        return plugin(
                groupId,
                artifactId,
                project.build.plugins.stream()
                        .filter { artifactId == it.artifactId && groupId == it.groupId }
                        .findFirst()
                        .map { it.version }
                        .orElseThrow { new UnsupportedOperationException("Couldn't find plugin version for ${groupId}:${artifactId}") })
    }

    def getPluginConfig = { plugin ->
        return project.build.plugins.stream()
                .filter { plugin.artifactId == it.artifactId && plugin.groupId == it.groupId }
                .map { it.configuration }
                .filter { it != null }
                .map {
                    // I think this is a way of cloning the dom, I'm not 100% sure, I copied it off of the Internet
                    Xpp3DomBuilder.build(new ByteArrayInputStream(it.toString().getBytes("UTF-8")), "UTF-8")
                }
                .filter { it != null }
                .findFirst()
                .orElse(null)
    }

    def mergeConfigs = { Object[] doms ->
        if (doms.length == 0)
            return null

        Xpp3Dom res = new Xpp3Dom("configuration")
        Arrays.stream(doms).filter { it != null }.flatMap { Arrays.stream(it.children) }.forEach { res.addChild(it) }
        return res
    }

    // Run jlink and generate jres
    println "Running jlink for ${distr.name}..."
    def artifacts = project.artifacts // Clear artifacts cause we don't want to include dependencies as jmods
    try {
        project.artifacts = new HashSet<>()

        def jlinkPlugin = pluginWithPomVersion(groupId("org.apache.maven.plugins"), artifactId("maven-jlink-plugin"))
        executeMojo(
                jlinkPlugin,
                goal("jlink"),
                mergeConfigs(
                        getPluginConfig(jlinkPlugin),
                        configuration(
                                element("classifier", distr.name),
                                element("modulePaths", element("modulePath", "${distr.extractedJdkHome}/jmods")))),
                executionEnvironment(project,
                        session,
                        pluginManager))
    } finally {
        project.artifacts = artifacts
    }

    // Copy jars in libs
    println "Copying dependencies jars in jre lib dir..."
    ant.copy(todir: "${distr.jlinkedHome}/lib/") {
        project.artifacts.forEach { artifact ->
            if (artifact.scope != "compile" && artifact.scope != "runtime")
                return

            fileset(dir: artifact.file.getParentFile().getAbsolutePath()) {
                include(name: artifact.file.getName())
            }
        }
    }

    def unixShellScriptTemplate = "${project.basedir}/unixScriptTemplate.txt"
    def windowsScriptTemplate = "${project.basedir}/windowsScriptTemplate.txt"
    def windowsScriptTemplateJavaw = "${project.basedir}/windowsScriptTemplateJavaw.txt"
    launchers.forEach { launcher ->
        if (distr.isNix && launcher.winNoConsole)
            return

        def scriptName = launcher.name + "." + (distr.isNix ? "sh" : "bat")
        def scriptFile = "${distr.jlinkedHome}/bin/$scriptName"
        println "Generating script file ${scriptFile}..."

        def templateFile = distr.isNix
                ? unixShellScriptTemplate
                : launcher.winNoConsole ? windowsScriptTemplateJavaw : windowsScriptTemplate

        def templatevars = [
                "mainClassName"        : launcher.mainClass as String,
                "applicationName"      : applicationName,
                "appNameSystemProperty": "",
                "appHomeRelativePath"  : "../",
                "optsEnvironmentVar"   : optsEnvironmentVar,
                "defaultJvmOpts"       : defaultJvmOpts,
                "exitEnvironmentVar"   : "1",
        ]
        def templateEnvVars = [
                "BADASS_RUN_IN_BIN_DIR"          : "true",
                "BADASS_CDS_ARCHIVE_FILE_LINUX"  : null,
                "BADASS_CDS_ARCHIVE_FILE_WINDOWS": null,
        ]

        def script = ""
        def savedEnvVars = new HashMap<String, String>()
        try {
            templateEnvVars.forEach { String key, String val ->
                savedEnvVars.put(key, System.getProperty(key))

                if (val == null)
                    System.clearProperty(key)
                else
                    System.setProperty(key, val)
            }

            def engine = new groovy.text.SimpleTemplateEngine()
            script = engine.createTemplate(Files.readString(Path.of(templateFile))).make(templatevars).toString()
        } finally {
            savedEnvVars.forEach { String key, String val ->
                if (val == null)
                    System.clearProperty(key)
                else
                    System.setProperty(key, val)
            }
        }

        Files.writeString(Path.of(scriptFile), script)
    }

    def attachedArtifact = project.attachedArtifacts.stream()
            .filter { it.type == "jlink" && it.classifier == distr.name }
            .findFirst()
            .orElseThrow { new UnsupportedOperationException("Couldn't find AttachedArtifact for ${distr.name}") }

    println "Overwriting ${distr.name} archive -> ${attachedArtifact.file}..."
    def zipArchiver = container.lookup("org.codehaus.plexus.archiver.Archiver", "zip") as ZipArchiver
    zipArchiver.addDirectory(new File(distr.jlinkedHome))
    zipArchiver.setDestFile(attachedArtifact.file)
    zipArchiver.createArchive()
}

