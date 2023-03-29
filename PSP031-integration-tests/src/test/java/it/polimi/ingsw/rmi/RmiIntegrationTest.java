package it.polimi.ingsw.rmi;

import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.controller.ControllersIntegrationTest;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.updater.UpdatersIntegrationTest;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

public class RmiIntegrationTest {

    @Test
    void testRmiUpdaters() throws Exception {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        UpdatersIntegrationTest.doTestUpdaters(
                serverController -> {
                    try {
                        RmiConnectionServerController.bind(remoteName, serverController);
                    } catch (RemoteException e) {
                        throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                    }
                },
                () -> new RmiClientNetManager(remoteName));
    }

    @Test
    void testRmiControllers() throws Throwable {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        ControllersIntegrationTest.doTestControllers(
                serverController -> {
                    try {
                        RmiConnectionServerController.bind(remoteName, serverController);
                    } catch (RemoteException e) {
                        throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                    }
                },
                () -> new RmiClientNetManager(remoteName));
    }
}
