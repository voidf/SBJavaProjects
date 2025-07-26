package web;
import javax.security.auth.callback.Callback;

public interface RecvCallback extends Callback {
    default void callback(String args) {
        invoke(args);
    }

    void invoke(String recvstr);
}
