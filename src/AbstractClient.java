import java.io.IOException;

/**
 * Interface which has common methods used across UDP and TCP clients.
 */
public interface AbstractClient {

    /**
     * creates client socket connection
     * @param host hostname/ipaddress of client
     * @param port clientport
     * @throws IOException throws exception if there is an error in creating socket
     */
    void startClient(String host, int port) throws IOException;

    /**
     * close the client socket
     * @throws IOException throws exception if there is an error in closing socket
     */
    void stopClient() throws IOException;
}
