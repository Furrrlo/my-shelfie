/**
 * Contains the interfaces in charge of propagating server model updates to the clients
 * <p>
 * An Updater is defined as the means which allows the server to send model updates to a specific client.
 * The communication direction is therefore server -> client. The server will use these interfaces in order
 * to send updates, while the client will implement these to actually update the model objects.
 */
package it.polimi.ingsw.updater;