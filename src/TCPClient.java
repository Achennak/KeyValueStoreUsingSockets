import java.io.*;
import java.net.*;

/**
 * TCP client implementation
 */
public class TCPClient extends AbstractClientImpl {
    private Socket socket;

    /**
     *  Run TCPServer first in order to run this on same port
     *  compile: javac TCPClient.java
     *  run: java TCPClient <Hostname> <Port>
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect arguments.Please provide both <IP> and <port>");
            System.exit(1);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        TCPClient tcpClient = new TCPClient();
        tcpClient.startClient(hostname,port);
    }

    /**
     * Helper function to validate the response from the server
     * @param data response received from server
     * @param receivedChecksum checksum sent along with response
     * @return returns true if data is valid else false
     */
    private boolean validateChecksum(byte[] data, int receivedChecksum) {
        int calculatedChecksum = calculateChecksum(data);
        return receivedChecksum == calculatedChecksum;
    }

    @Override
    public void startClient(String host, int port) {

        logMessage("Starting TCP Client on " + host + ":" + port);
        try {
            socket = new Socket(host,port);
            socket.setSoTimeout(TIMEOUT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Automated PUTs,GETs and Delete's
            for (int i = 0; i < 5; i++) {
                String automatedPut = "PUT key" + i + " v" + (i+1);
                sendAndReceiveMessage(writer,reader,automatedPut);
                String automatedGet = "GET key" + i;
                sendAndReceiveMessage(writer,reader,automatedGet);
                String automatedDelete = "DELETE key" + i;
                sendAndReceiveMessage(writer,reader,automatedDelete);
            }
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                logMessage("Enter operation: PUT <key> <value> or GET <key> or DELETE <key>");
                String userInput = userInputReader.readLine();
                sendAndReceiveMessage(writer, reader, userInput);
            }

        }  catch (IOException e) {
            logError("Error connecting to the server: "  +e.getMessage());
        } finally {
            stopClient();
        }

    }

    /**
     * Helper method to send and recieve messages from the server.
     * @param writer outputstream
     * @param reader inputstream
     * @param message message to server
     * @throws IOException throws error if any exception occurs
     */
    private void sendAndReceiveMessage(PrintWriter writer, BufferedReader reader, String message) {
        // Calculate checksum for the message
        int checksum = calculateChecksum(message.getBytes());

        // Concatenate checksum and message and send to the server
        String messageWithChecksum = checksum + " " + message;
        writer.println(messageWithChecksum);

        // Read server responses
        String response = null;
        try {
            response = reader.readLine();
            if(response!=null) {

                // Extract checksum and data from the response
                String[] tokens = response.split(" ", 2);
                int receivedChecksum = Integer.parseInt(tokens[0]);
                String responseData = tokens[1];

                // Validate the checksum
                if (validateChecksum(responseData.getBytes(), receivedChecksum)) {
                    logMessage(responseData);
                } else {
                    logError("Malformed or corrupted TCP response received");
                }
            } else{
                throw new SocketTimeoutException();
            }
        } catch (SocketTimeoutException e) {
            logError("Timeout occurred. Server may have exited.");
        } catch (IOException e) {
            logError("Error connecting to the server: "  +e.getMessage());
        }
    }

    @Override
    public void stopClient() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logMessage("TCP Client stopped.");
            }
        } catch (IOException e) {
            logError("Error while stopping TCP Client: " + e.getMessage());
        }
    }
}
