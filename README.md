<h1>Network Checkers</h1>

A simple two player game of checkers that was created for COSC318 - Network Programming.

To run, requires running one copy of ProjectServer.java to act as the server and each client runs a copy of EnterServerFrame.java which prompts the player to enter the IP address of the server and then runs the game.

Functions by connecting the clients to the server by TCP. The server manages the clients by alternating between which client it is listening to and which client it is sending data to. The turn data is serialized into an object (Action.java), sent to the server, then the server sends it to the other player, the other player's client handling the update of their game state using the turn data.
