package it.polimi.ingsw.rmi;

import it.polimi.ingsw.client.ClientNetManagerCloseTest;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.controller.ControllersIntegrationTest;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.updater.UpdatersIntegrationTest;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RmiIntegrationTest {

    @Test
    void testRmiUpdaters() throws Exception {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final RMIPortCapturingServerSocketFactory portCapturingServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        UpdatersIntegrationTest.doTestUpdaters(
                serverController -> {
                    try {
                        return RmiConnectionServerController.bind(
                                LocateRegistry.createRegistry(0, null, portCapturingServerSocketFactory),
                                remoteName,
                                serverController);
                    } catch (RemoteException e) {
                        throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                    }
                },
                nick -> {
                    try {
                        return RmiClientNetManager.connect(
                                null, portCapturingServerSocketFactory.getFirstCapturedPort(),
                                remoteName, nick);
                    } catch (RemoteException | NotBoundException | NickNotValidException e) {
                        throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                    }
                });
    }

    @Test
    void testRmiControllers() throws Throwable {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final RMIPortCapturingServerSocketFactory portCapturingServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        ControllersIntegrationTest.doTestControllers(
                serverController -> {
                    try {
                        return RmiConnectionServerController.bind(
                                LocateRegistry.createRegistry(0, null, portCapturingServerSocketFactory),
                                remoteName,
                                serverController);
                    } catch (RemoteException e) {
                        throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                    }
                },
                nick -> {
                    try {
                        return RmiClientNetManager.connect(
                                null, portCapturingServerSocketFactory.getFirstCapturedPort(),
                                remoteName, nick);
                    } catch (NotBoundException | RemoteException | NickNotValidException e) {
                        throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                    }
                });
    }

    @Test
    void testClientNetManagerSubsequentCloses() throws Throwable {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final RMIPortCapturingServerSocketFactory portCapturingServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        ClientNetManagerCloseTest.doTestSubsequentCloses(
                serverController -> {
                    try {
                        return RmiConnectionServerController.bind(
                                LocateRegistry.createRegistry(0, null, portCapturingServerSocketFactory),
                                remoteName,
                                serverController);
                    } catch (RemoteException e) {
                        throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                    }
                },
                nick -> {
                    try {
                        return RmiClientNetManager.connect(
                                null, portCapturingServerSocketFactory.getFirstCapturedPort(),
                                remoteName, nick);
                    } catch (NotBoundException | RemoteException | NickNotValidException e) {
                        throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                    }
                });
    }
}
