package redis.db;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 */
public class Key {
    public static String getUserEmailKey(){ return "client:user:email"; }
    public static String getClientUserKey(String uid){ return "client:uid:" + uid; }

}
