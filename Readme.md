start with two terminals one for server and one for client at location /src

Server Terminal:
1.compile using javac TCPServer.java/ javac UDPServer.java
2.Run using java TCPServer <Port Number>/java UDPServer <Port Number>
java UDPServer 3200
java TCPServer 3201
3.for TCP server run this before client.

Client Terminal:
1.compile using javac TCPClient.java/javac UDPClient.java
2.Run using TCPClient <IP> <Port Number> / java UDPClient  <Hostname> <Port number>
java UDPClient 127.0.0.1 3200
java TCPClient 127.0.0.1 3201

Note:
When client starts running first five put,get and delete requests are automated to send to server directly .After that user can start typing inputs in client.

Additional Details:

1.while sending operations from client to server both GET/get works ie operations are not case-sensitive.
2.while sending operations from client to server request should be provided as PUT <key> <value> or get <key> or delete <key> ie each word is separated by space.
Always start with put , else we will see key not found errors.
valid operations:
put 3 4
put mango fruits
get 3
get mango
DELETE 3
Invalid operations:
put 
put 5
put 6 7 8
get
delete 5 6 
delete
3.key and values are string data type in the hashmap.
4.Main code for TCP resides in TCPClient,TCPServer and for udp in UDPClient,UDPServer.
5.Main code for hashmap logic lies in KeyValueStore.java and Helper.java has common helper functions lie logging,validating checksum.
6.Both the clients and servers have included checksum validation to detect Malformed requests ie 
client sends checksum along with data and server extracts the checksum , validates it against request , throws error if it doesn't match and similarly server also calculates checksum for response , 
sends it along with response to client and when client receives the response it again does similar validation on it.
7.when server is not responding to client within 5 seconds and client requests something from server , then client recognises the server timeout 
and note in client log and send the remaining requests.
8.In TCP server , I have added threads logic to communicate with multiple clients at the same time.



