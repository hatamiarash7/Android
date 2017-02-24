package helper;

import com.android.volley.NetworkResponse;

@SuppressWarnings("serial")
public class Error extends Exception {
    public final NetworkResponse networkResponse;
    private long networkTimeMs;

    public Error() {
        networkResponse = null;
    }

    public Error(NetworkResponse response) {
        networkResponse = response;
    }

    public Error(String exceptionMessage) {
        super(exceptionMessage);
        networkResponse = null;
    }

    public Error(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public Error(Throwable cause) {
        super(cause);
        networkResponse = null;
    }

    public Error(NetworkResponse response, Throwable cause) {
        super(cause);
        networkResponse = response;
    }

    public void setNetworkTimeMs(long networkTimeMs) {
        this.networkTimeMs = networkTimeMs;
    }

    public long getNetworkTimeMs() {
        return networkTimeMs;
    }
}