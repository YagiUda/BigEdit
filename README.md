# BigEdit
A client/server based word processor leveraging EJB

A stateful session bean was used because it would be easier to control future features down the road such as checking a file in and out on the server when there are multiple clients connecting (preventing the same file from being edited at the same time).  A String array object is used send to the server where line counts easily verified (example: server.readFile(server.getWhiteBoard(), lines) ensures there is a set amount of lines to be iterated over the data). This is an alternative to creating custom sockets when using java programming.
