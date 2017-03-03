package innoticon.ds.server;

import com.google.gson.annotations.Expose;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 11.
 */
public class Profile {
    @Expose public String name;
    @Expose public String app;
    @Expose public innoticon.ds.Client.Key key;

    public String name(){ return name; }
    public String app(){ return app; }
    public innoticon.ds.Client.Key key(){ return key; }

    public String name(String v){ return name = v; }
    public String app(String v){ return app = v; }
    public innoticon.ds.Client.Key key(innoticon.ds.Client.Key v){ return key = v; }

    public Profile(){

    }
}
