package it.polimi.ingsw.rmi;

import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.controller.ControllersIntegrationTest;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.updater.UpdatersIntegrationTest;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RmiIntegrationTest {

    @Test
    void testRmiUpdaters() throws Exception {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final var rmiServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        UpdatersIntegrationTest.doTestUpdaters(
                serverController -> {
                    try {
                        return RmiConnectionServerController.bind(
                                LocateRegistry.createRegistry(0, null, rmiServerSocketFactory),
                                remoteName,
                                serverController);
                    } catch (RemoteException e) {
                        throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                    }
                },
                () -> new RmiClientNetManager(null, rmiServerSocketFactory.getFirstCapturedPort(), remoteName));
    }

    @Test
    void testRmiControllers() throws Throwable {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final var rmiServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        ControllersIntegrationTest.doTestControllers(
                serverController -> {
                    try {
                        return RmiConnectionServerController.bind(
                                LocateRegistry.createRegistry(0, null, rmiServerSocketFactory),
                                remoteName,
                                serverController);
                    } catch (RemoteException e) {
                        throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                    }
                },
                () -> new RmiClientNetManager(null, rmiServerSocketFactory.getFirstCapturedPort(), remoteName));
    }
}
