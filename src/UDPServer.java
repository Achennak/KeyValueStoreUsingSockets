import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * UDP Server implementation
 */
public class UDPServer extends AbstractServerImpl{

    private DatagramSocket udpSocket;

    /**
     * compile: javac UDPServer.java
     * java UDPServer <Port>
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid inputs");
            System.exit(1);
        }
        UDPServer udpServer = new UDPServer();
        try {
            int port = Integer.parseInt(args[0]);
            udpServer.startServer(port);
        } catch (IOException e) {
            udpServer.logError("Error starting UDP Server: " + e.getMessage());
        } finally{
            try{
                udpServer.stopServer();
            } catch(IOException ex){
                udpServer.logError("Error stopping UDP Sever: " + ex.getMessage());
            }
        }
    }

    /**
     * Helper function to validate the checksum of the request from client
     * @param packet data received from client
     * @return returns true if data is valid else false
     */
    private boolean validateChecksum(DatagramPacket packet) {
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
        int receivedChecksum = buffer.getInt();
        byte[] receivedData = new byte[packet.getLength() - Integer.BYTES];
        buffer.get(receivedData);

        int calculatedChecksum = calculateChecksum(receivedData);

        if (receivedChecksum == calculatedChecksum) {
            // Set the packet data to the new data without checksum
            packet.setData(receivedData);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void startServer(int port) throws IOException {
        udpSocket = new DatagramSocket(port);
        logMessage("UDP Server started on port " + port);
        KeyValueStore keyValueStore = new KeyValueStore();

        while (true) {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            udpSocket.receive(receivePacket);
            if (validateChecksum(receivePacket)) {
                handleUDPRequest(receivePacket, keyValueStore);
            } else {
                logError("Malformed or corrupted UDP packet received");
            }
        }
    }

    /**
     * Handles requests from UDP clients.
     * @param receivePacket datagram packet to communicate with client
     * @param keyValueStore keyvalue store
     */
    private void handleUDPRequest(DatagramPacket receivePacket,KeyValueStore keyValueStore) {
        try {
            String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            logMessage("Received UDP request from " + clientAddress + ":" + clientPort + " - " + request);
            // Process the request and send the response
            String response = processRequest(request,keyValueStore);
            sendUDPResponse(response, clientAddress, clientPort);
            logMessage("Sent UDP response to " + clientAddress + ":" + clientPort + " - " + response);

        } catch (Exception e) {
            logError("Error handling UDP request: " + e.getMessage());
        }
    }

    /**
     * Sends the response back to udp client
     * @param response response to be sent
     * @param clientAddress client host address
     * @param clientPort client port
     */
    private void sendUDPResponse(String response, InetAddress clientAddress, int clientPort) {
        try {
            byte[] sendData = response.getBytes();
            int checksum = calculateChecksum(sendData);
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + sendData.length);
            buffer.putInt(checksum);
            buffer.put(sendData);
            DatagramPacket sendPacket = new DatagramPacket(buffer.array(), buffer.array().length, clientAddress, clientPort);
            udpSocket.send(sendPacket);
        } catch (IOException e) {
            logError("Error sending UDP response: " + e.getMessage());
        }
    }

    @Override
    public void stopServer() throws IOException {
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
            logMessage("UDP Server stopped.");
        }
    }
}
