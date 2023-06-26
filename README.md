# Project for the course "software engineering" at Politecnico di Milano, academic year 2022/2023

See specs [here](docs/Requisiti.pdf) and the rules
([ita](docs/MyShelfie_Rulebook_ITA.pdf) and [eng](docs/MyShelfie_Rulebook_ENG.pdf))

For documentation regarding the architecture see the [deliverables folder](deliverables)

## Team (Group 31)

- [Francesco Ferlin](https://github.com/Furrrlo)
- [Francesco Guarda](https://github.com/FrancescoGuarda)
- [Giacomo Orsenigo](https://github.com/jackorse)
- [Giovanni Alberto Sartorato](https://github.com/giovannialbertos)

## Implemented features

- Complete rules
- TUI/CLI
- GUI
- Socket
- RMI
- Advanced Features:
    - Multiple games
    - Resilience to disconnections
    - Chat

## Running the application

The application requires an installation of Java 19. Newer versions cannot be used
as preview features were used which may have been changed in Java 20.

The server needs to be run from terminal using the following command:

```shell
java --enable-preview -jar server.jar
```

Both the GUI and TUI clients can be run from both the terminal by executing:

```shell
java --enable-preview -jar client.jar
```

and selecting the preferred mode.

The GUI can also be run by double-clicking the `gui-console.bat`/`gui-noconsole.vbs`
on Windows or `gui.sh` on *nix.

## Coverage

|            |                                                                    Instructions                                                                    |                                                                          Branches                                                                           |
|------------|:--------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------:|
| Model      |   [![coverage](../badges/jacoco-model.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml)    |   [![branch coverage](../badges/branches-model.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml)    |
| Controller | [![coverage](../badges/jacoco-controller.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml) | [![branch coverage](../badges/branches-controller.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml) |
| Network    |  [![coverage](../badges/jacoco-network.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml)   |  [![branch coverage](../badges/branches-network.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml)   |
| Overall    |  [![coverage](../badges/jacoco-overall.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml)   |  [![branch coverage](../badges/branches-overall.svg)](https://github.com/Furrrlo/ing-sw-2023-ferlin-orsenigo-guarda-sartorato/actions/workflows/test.yml)   |

# Development

## Code Style

See [here](CODE_STYLE.md)

## Running using cmd line/IDE

To start the server, execute the compile lifecycle and specify either the `run-server` profile

```shell
mvn compile -Prun-server
```

To start the TUI client, do the same but with the `run-client` profile

```shell
mvn compile -Prun-client
```

To run the JavaFX client, use the IntelliJ 'Run JavaFX' configuration or from the console:

```shell
mvn compile javafx:run
```

while to debug it, use the IntelliJ 'Debug JavaFX' configuration or from the console:

```shell
mvn compile javafx:run -Ddebug-javafx
```

then attach a remote debugger to port 5005 (IntelliJ will prompt for it automatically).

## Building and running

To build the JavaFX client, the assets provided by Cranio must be placed in the assets directory
in the root folder of the project.

Then, execute the package lifecycle:

```shell
mvn clean package
```

The built jars can be found in `PSP031-client/target` and `PSP031-server/target`.
To run the client:

```shell
java --enable-preview -jar PSP031-client-1.0-SNAPSHOT.jar
```

To run the server:

```shell
java --enable-preview -jar PSP031-server-1.0-SNAPSHOT.jar
```