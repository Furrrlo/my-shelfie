# Sequence diagrams

`ClientSocketManager` and `ServerSocketManager` are implementations of `SocketManager` interface. 
It can handle socket communication from multiple threads, using a pair of ingoing and outgoing queues to allow sending and receiving acks and replies:
- The `send` method sends a packet and waits for an ack of the given type (SimpleAckPacket if not specified).
- The `receive` method waits for a packet of the given type.

Both methods return a context object that allows to send a reply.

All update packets sent by the server, are broadcast to all connected clients. 

Every few seconds, a ping packet is sent from server to all clients to check if they still connected. An ack packet is always sent in response to ping messages.

## Game access
When a player join, the server assign it to a lobby, creating a new one if none are free.
Players can notify the server when they are ready to start the game. When all players are ready, the game is started.

If the player was already in a game and was disconnected, the server return the previous lobby, and a GameUpdaterPacket is sent immediately.

## Make move
When a player make a move, a packet with a list of tiles and the column of the shelfie is sent.
The server sent to all players two packet to update board and shelfie.

If it is not the turn of the player who made the move, He is probably playing with a modded client, so we disconnect him. 

## Common objective completion
There is no difference between the client of the player who completed the goal and the others.
Two packets are sent to all clients: 
- `UpdateAchievedCommonGoalPacket` contains a list of players who have completed a common goal, in order of achievement.
- `UpdatePlayerScorePacket` contains the new score of the player who just achieved the common goal.