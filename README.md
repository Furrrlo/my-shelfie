# Project for the course "software engineering" at Politecnico di Milano, academic year 2022/2023

See specs [here](docs/Requisiti.pdf) and the rules 
([ita](docs/MyShelfie_Rulebook_ITA.pdf) and [eng](docs/MyShelfie_Rulebook_ENG.pdf))

For documentation regarding the architecture see the [deliverables folder](deliverables)

## Code Style

See [here](CODE_STYLE.md)

## Running using cmd line/IDE

Execute the compile lifecycle and specify either the `run-server` or `run-client` profile

```shell
mvn compile -Prun-client
mvn compile -Prun-server
```

## Building and running

Execute the package lifecycle:

```shell
mvn package
```

The built jars can be found in `PSP031-client/target` and `PSP031-server/target`.
To run the client:

```shell
java --enable-preview -jar PSP031-client-1.0-SNAPSHOT.jar
```

To run the server (it requires preview features):

```shell
java --enable-preview -jar PSP031-server-1.0-SNAPSHOT.jar
```