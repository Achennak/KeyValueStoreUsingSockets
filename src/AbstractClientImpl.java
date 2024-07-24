/**
 * Implements the client interface methods and abstracts the common code.
 */
public abstract class AbstractClientImpl  extends Helper implements AbstractClient{
    protected static final int TIMEOUT = 5000; // 5 seconds timeout
    public abstract void startClient(String host, int port);
    public abstract void stopClient();

}
