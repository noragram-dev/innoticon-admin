package common.db;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 11.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Path {
    public static String getRootPath(String category){
        if(category==null){ return null; }
        return "/common/" + category;
    }

    public static String getTelegramRootPath(){ return getRootPath("telegram"); }
    public static String getTelegramRootPath(Long id){
        if(id==null){ return null; }
        return getTelegramRootPath() + "/" + id;
    }

    public static String getEmailRootPath(){ return getRootPath("email"); }
    public static String getEmailPath(String email) {
        if(email==null){ return null; }
        return getEmailRootPath() + "/" + novemberizing.util.Key.Encode(email.getBytes());
    }
    public static String getEventRootPath(){ return getRootPath("event"); }

    public static String getEventPath(innoticon.ds.Req req){
        if(req==null || req.action==null || req.action.key==null){ return null; }
        if(req.type()>0){
            return getEventRootPath() + "/user/" + req.action.key.hex();
        } else {
            return getEventRootPath() + "/server/" + req.action.key.hex();
        }
    }
}
