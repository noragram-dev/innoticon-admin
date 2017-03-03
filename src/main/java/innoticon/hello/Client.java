package innoticon.hello;

import com.google.gson.annotations.Expose;
import innoticon.ds.User;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 11.
 */
public class Client extends innoticon.ds.Req {
    @Expose public String device = null;
    @Expose public String app = null;
    @Expose public User user = null;
    @Expose public innoticon.ds.Client.Key key = null;

    public String device() {
        return this.device;
    }

    public String app() {
        return this.app;
    }

    public User user() {
        return this.user;
    }

    public innoticon.ds.Client.Key key() {
        return this.key;
    }

    public Client() {
    }
}

