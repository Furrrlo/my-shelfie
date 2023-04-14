package it.polimi.ingsw.rmi;

import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.controller.ControllersIntegrationTest;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.updater.UpdatersIntegrationTest;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

public class RmiIntegrationTest {
    static @Nullable RMITimeoutSocketFactory rmiServerSocketFactory = null;

    @BeforeAll
    static void beforeAll() {
        rmiServerSocketFactory = new RMITimeoutSocketFactory();
        try {
            RMISocketFactory.setSocketFactory(rmiServerSocketFactory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
                () -> new RmiClientNetManager(null, portCapturingServerSocketFactory.getFirstCapturedPort(),
                        remoteName));
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
                () -> new RmiClientNetManager(null, portCapturingServerSocketFactory.getFirstCapturedPort(),
                        remoteName));
    }
}
