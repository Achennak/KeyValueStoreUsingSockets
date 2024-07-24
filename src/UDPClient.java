import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * UDP client implementation
 */
public class UDPClient extends AbstractClientImpl {
    private DatagramSocket socket;

    /**
     * Run UCPServer first on the same port
     * compile: javac UDPClient.java
     * run: java UDPClient <Hostname> <Port number>
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect arguments.Please provide both <IP> and <port>");
            System.exit(1);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        UDPClient udpClient = new UDPClient();
        udpClient.startClient(hostname, port);
    }

    /**
     * Helper function to validate the checksum of the response from server
     * @param packet response received from server
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
    public void startClient(String host, int port) {
        logMessage("Starting UDP Client on " + host + ":" + port);

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Automated PUTs,GETs and Deletes
            for (int i = 0; i < 5; i++) {
                String automatedPut = "PUT key" + i + " value" + i;
                sendAndReceiveMessage(automatedPut, host, port);
                String automatedGet = "GET key" + i;
                sendAndReceiveMessage(automatedGet, host, port);
                String automatedDelete = "DELETE key" + i;
                sendAndReceiveMessage(automatedDelete, host, port);
            }

            while (true) {
                logMessage("Enter operation: PUT <key> <value> or GET <key> or DELETE <key>");
                String userInput = reader.readLine();
                try {
                    sendAndReceiveMessage(userInput, host, port);
                } catch (SocketTimeoutException e) {
                    logError("Timeout occurred. Server may have exited.");
                }
            }
        }  catch (IOException e) {
            logError("Error communicating with the server: " +e.getMessage());
        } finally {
            stopClient();
        }
    }

    /**
     * Helper method to send and recieve messages from the server.
     * @param userInput username from the console
     * @param host hostname/ipaddress of the client
     * @param port portnumber
     * @throws IOException throws exception when there is an error
     */
    private void sendAndReceiveMessage(String userInput,String host,int port) throws IOException {
        byte[] sendData = userInput.getBytes();
        int checksum = calculateChecksum(sendData);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + sendData.length);
        buffer.putInt(checksum);
        buffer.put(sendData);
        DatagramPacket sendPacket = new DatagramPacket(buffer.array(), buffer.array().length, InetAddress.getByName(host), port);
        socket.send(sendPacket);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        if (validateChecksum(receivePacket)) {
            // Process the response
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            logMessage(response);
        } else {
           logError("Malformed or corrupted UDP response received");
        }
    }

    @Override
    public void stopClient() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            logMessage("UDP Client stopped.");
        }
    }
}
