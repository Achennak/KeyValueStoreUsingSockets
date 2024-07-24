import java.io.*;
import java.net.*;

/**
 * TCP Server implementation
 */
public class TCPServer extends AbstractServerImpl  {

    /**
     * compile: javac TCPServer.java
     * run: java TCPServer <Port>
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid inputs");
            System.exit(1);
        }
        TCPServer tcpServer = new TCPServer();
        try {
            int port = Integer.parseInt(args[0]);
            tcpServer.startServer(port);
        } catch (IOException e) {
            tcpServer.logError("Error starting TCP Server: " + e.getMessage());
        }
    }

    /**
     * Helper function to validate the checksum of the request from client
     * @param data request from the client
     * @param receivedChecksum checksum set along with request
     * @return returns true if data is valid else false
     */
    private boolean validateChecksum(byte[] data, int receivedChecksum) {
        int calculatedChecksum = calculateChecksum(data);
        return receivedChecksum == calculatedChecksum;
    }

    @Override
    public void startServer(int port) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)){
            logMessage("TCP Server Started on port "+port);
            KeyValueStore keyValueStore = new KeyValueStore();
            while(true){
                Socket clientSocket = serverSocket.accept();
                logMessage("processing client req");


                new Thread(() -> handleTCPRequest(clientSocket, keyValueStore)).start();
               // handleTCPRequest(clientSocket,keyValueStore);
            }
        } catch(IOException ex) {
            logError("TCP Server connection failed with error "+ex.getMessage());
        }
    }

    /**
     * handles tcp request from the tcp client
     * @param clientSocket socket connection to communicate with client
     * @param keyValueStore keyvalue store
     */
    private void handleTCPRequest(Socket clientSocket,KeyValueStore keyValueStore) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            // Read the TCP request from the client
            String request;
           while((request=reader.readLine())!=null)
           {
                InetAddress clientAddress = clientSocket.getInetAddress();
                int clientPort = clientSocket.getPort();

                String[] tokens = request.split(" ");

                if (tokens.length < 2) {
                    logError("Malformed request received from " + clientAddress + ":" + clientPort);
                    return;
                }

               // Extract the checksum and data from the request
               int receivedChecksum = Integer.parseInt(tokens[0]);
               String requestData = request.substring(tokens[0].length() + 1);

                // Process the request and send the response
               logMessage("Received TCP request from " + clientAddress + ":" + clientPort + " - " + requestData);

               if (validateChecksum(requestData.getBytes(), receivedChecksum)) {
                   // Process the request and send the response
                   String response = processRequest(requestData, keyValueStore);
                   int responseChecksum = calculateChecksum(response.getBytes());

                   // Send the response back to the client with the checksum
                   String responseWithChecksum = responseChecksum + " " + response;
                   writer.println(responseWithChecksum);
                   logMessage("Sent TCP response to " + clientAddress + ":" + clientPort + " - " + response);
               } else {
                   logError("Malformed or corrupted TCP packet received " + clientAddress + ":" + clientPort);
               }

          }
        } catch (Exception e) {
            logError("Error handling TCP request: " + e.getMessage());
        } finally{
            try{
                clientSocket.close();
            } catch(IOException ex){
                System.out.println("TCP client socket close failed");
            }
        }
    }

    @Override
    public void stopServer() throws IOException {

        //server should run continuously

    }

}

