import java.io.IOException;

/**
 *  Interface which has common methods used across UDP and TCP servers.
 */
public interface AbstractServer {

    /**
     * creates server socket connection
     * @param port portnumber
     * @throws IOException throws exception if there is an error in creating socket
     */
    void startServer(int port) throws IOException;

    /**
     * closes server socket
     * @throws IOException throws exception if there is an error in closing socket
     */
    void stopServer() throws IOException;

    /**
     *  helper function which process the client request and send response back
     * @param request request from client
     * @param keyValueStore keyvalue store
     * @return returns response to client
     */
    String processRequest(String request, KeyValueStore keyValueStore);


}
