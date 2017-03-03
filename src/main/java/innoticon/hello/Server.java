package innoticon.hello;

import com.google.gson.annotations.Expose;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 11.
 */
public class Server extends innoticon.ds.Req {
    @Expose public innoticon.ds.server.Profile profile;

    @Expose public String name = null;
    @Expose public String app = null;
    @Expose public innoticon.ds.Client.Key key = null;

    public String name(){ return profile!=null ? profile.name() : null; }
    public String app(){ return profile!=null ? profile.app() : null; }
    public innoticon.ds.Client.Key key(){ return profile!=null ? profile.key() : null; }
    public innoticon.ds.server.Profile profile(){ return profile; }

    public Server() {
    }
}
