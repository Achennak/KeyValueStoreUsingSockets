import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements the server interface methods and abstracts the common code.
 */
public abstract class AbstractServerImpl extends Helper implements AbstractServer{

    @Override
    public abstract void startServer(int port) throws IOException;

    @Override
    public  abstract void stopServer() throws IOException;

    @Override
    public String processRequest(String request, KeyValueStore keyValueStore) {
        String[] parts = request.split(" ");

        String operation = parts[0];

        switch (operation.toUpperCase()) {
            case "PUT":
                if (parts.length == 3) {
                    String key = parts[1];
                    String value = parts[2];
                    keyValueStore.put(key, value);
                    return "PUT successful";
                } else {
                    return "Invalid PUT request";
                }

            case "GET":
                if (parts.length == 2) {
                    String key = parts[1];
                    String value = keyValueStore.get(key);
                    return value != null ? "Get operation successfully returned with value "+ value : "Key not found";
                } else {
                    return "Invalid GET request";
                }

            case "DELETE":
                if (parts.length == 2) {
                    String key = parts[1];
                    boolean result = keyValueStore.delete(key);
                    return result ? "DELETED successfully" :"Key not found";
                }   else {
                    return "Invalid DELETE request";
                }

            default:
                return "Invalid request";
        }
    }


}
