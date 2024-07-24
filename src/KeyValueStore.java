import java.util.HashMap;

/**
 * Java class to manage and store key value pair
 */
public class KeyValueStore {
    private final HashMap<String, String> keyValueStore;

    public KeyValueStore() {
        this.keyValueStore = new HashMap<>();
    }

    /**
     * Handles put operation.
     * @param key key value for the store
     * @param value value intended to store
     */
    public synchronized void put(String key, String value) {
        keyValueStore.put(key, value);
    }

    /**
     * Handles get operation.
     * @param key key value to retrieve
     * @return returns key
     */
    public synchronized String get(String key) {
        return keyValueStore.getOrDefault(key, null);
    }

    /**
     * Handles delete operation
     * @param key key to be deleted
     * @return returns response to client
     */
    public synchronized boolean delete(String key) {
        if(!keyValueStore.containsKey(key)){
            return false;
        }
        else{
            keyValueStore.remove(key);
            return true;
        }

    }
}
