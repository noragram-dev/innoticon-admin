package server.db;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Path {
    public static String getEventPath(){ return "/event"; }

    public static String getServerRootPath(String sid){
        if(sid==null){ return null; }
        return "/server/" + sid;
    }
    public static String getServerRequestRootPath(String sid){
        if(sid==null){ return null; }
        return getServerRootPath(sid) + "/request";
    }
    public static String getServerResponseRootPath(String sid){
        if(sid==null){ return null; }
        return getServerRootPath(sid) + "/response";
    }

    public static String getServerRequestPath(innoticon.ds.Req req){
        if(req==null){ return null; }
        return getServerRequestPath(req.action);
    }

    public static String getServerResponsePath(innoticon.ds.Req req){
        if(req==null){ return null; }
        return getServerResponsePath(req.action);
    }

    public static String getServerResponsePath(innoticon.ds.Req req, innoticon.ds.Res.Key key){
        if(req==null){ return null; }
        return getServerResponsePath(req.action, key);
    }

    public static String getServerRequestPath(innoticon.ds.Action action){
        if(action==null || action.client==null || action.key==null || action.key.c==null){ return null; }
        return getServerRequestRootPath(action.client.hex()) + "/" + action.key.c.hex();
    }
    public static String getServerResponsePath(innoticon.ds.Action action){
        if(action==null || action.client==null || action.key==null || action.key.c==null){ return null; }
        return getServerResponseRootPath(action.client.hex()) + "/" + action.key.c.hex() + "/main";
    }
    public static String getServerResponsePath(innoticon.ds.Action action, innoticon.ds.Res.Key key){
        if(action==null || action.client==null || action.key==null || action.key.c==null || action.key.s==null){ return null; }
        return getServerResponseRootPath(action.client.hex()) + "/" + action.key.c.hex() + "/" + action.key.s.hex() + "/"+ key.hex();
    }

    public static String getServerProfilePath(String app, String name){
        if(app==null || name==null){ return null; }
        return getServerRootPath("daemon") + "/" + app + "/" + name;
    }
    public static String getServerKeyPath(innoticon.ds.Client.Key key){
        if(key==null){ return null; }
        return getServerRootPath("key") + "/client/" + key.hex();
    }

    public static String getEnvelopeRootPath(){
        return getServerRootPath("envelope");
    }

    public static String getEnvelopePath(innoticon.ds.Envelope envelope){
        if(envelope==null || envelope.action==null || envelope.action.key==null){ return null; }
        return getEnvelopeRootPath() + "/" + envelope.action.key.hex();
    }

    public static String getMessagePath(innoticon.ds.Envelope envelope, String key){
        String path = getEnvelopePath(envelope);
        if(path==null || path.length()==0){ return null; }
        return path + "/" + key;
    }

    public static String getEventPath(innoticon.ds.Envelope envelope,innoticon.ds.Message.Key key){
        if(envelope==null || envelope.action==null || envelope.action.key==null){ return null; }
        if(key==null){ return null; }
        String hex = key.hex();
        if(hex==null || hex.length()==0){ return null; }
        return getEventPath() + "/" + envelope.action.key.hex() + "/" + hex;
    }
}