/**
 * Contains the RMI remotable service interfaces for controllers, updaters and {@link it.polimi.ingsw.HeartbeatHandler},
 * as well as the necessary adapters to conform to those interfaces and the {@link it.polimi.ingsw.rmi.RmiConnectionController}
 * entrypoint, which will be bound by the server in a {@link java.rmi.registry.Registry} and used by the clients
 * to establish an RMI connection
 *
 * @see it.polimi.ingsw.controller
 * @see it.polimi.ingsw.updater
 * @see it.polimi.ingsw.HeartbeatHandler
 */
package it.polimi.ingsw.rmi;