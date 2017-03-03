package db;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 10.
 */
public class Path {

    public static String getRequestPath(innoticon.ds.Req req) {
        return req.type()>0 ?
                client.db.Path.getUserRequestPath(req.action.uid(), req.action) :
                server.db.Path.getServerRequestPath(req);
    }

    public static String getResponsePath(innoticon.ds.Req req) {
        return req.type()>0 ?
                client.db.Path.getUserResponsePath(req.action.uid(), req.action) :
                server.db.Path.getServerResponsePath(req); }

    public static String getResponsePath(innoticon.ds.Req req, innoticon.ds.Res.Key key) {
        return req.type()>0 ?
                client.db.Path.getUserResponsePath(req.action.uid(), req.action, key) :
                server.db.Path.getServerResponsePath(req, key); }

    public static String getRequestPath(innoticon.ds.Action action) {
        return action.type()>0 ?
                client.db.Path.getUserRequestPath(action.uid(), action) :
                server.db.Path.getServerRequestPath(action);
    }

    public static String getResponsePath(innoticon.ds.Action action) {
        return action.type()>0 ?
                client.db.Path.getUserResponsePath(action.uid(), action) :
                server.db.Path.getServerResponsePath(action); }

    public static String getResponsePath(innoticon.ds.Action action, innoticon.ds.Res.Key key) {
        return action.type()>0 ?
                client.db.Path.getUserResponsePath(action.uid(), action, key) :
                server.db.Path.getServerResponsePath(action, key); }

    public static String getDialogPath(innoticon.req.Dialog.Key key){
        if(key==null){ return null; }
        return "/dialog/" + key.hex();
    }

    public static String getDialogInfoPath(innoticon.req.Dialog.Key key){
        if(key==null){ return null; }
        return getDialogPath(key) + "/info";
    }

    public static String getDialogUserRootPath(innoticon.req.Dialog.Key key){
        if (key==null){
            return null;
        }
        return getDialogPath(key) + "/user";
    }

    public static String getDialogUserPath(innoticon.req.Dialog.Key key, String uid){
        if (uid == null && key==null){
            return null;
        }
        return getDialogUserRootPath(key) + "/" + uid;
    }

    public static String getDialogInboxPath(innoticon.req.Dialog.Key key){
        if(key==null){ return null; }
        return getDialogPath(key) + "/inbox";
    }



}
