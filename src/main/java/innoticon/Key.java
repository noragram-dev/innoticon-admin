package innoticon;

import com.google.gson.Gson;
import novemberizing.util.Log;
import redis.clients.jedis.Jedis;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 12.
 */
public class Key {
    public static <T extends innoticon.ds.Key> T Gen(String category, Class<T> c, Gson gson){
        T ret = null;
        try (Jedis jedis = redis.Pool.Jedis()) {
            Object json = jedis.eval(novemberizing.util.File.Get("generate.key.lua"),1, category);
            if(json instanceof String){
                ret = gson.fromJson((String) json, c);
            } else {
                Log.e("innoticon.key.gem>", "json instanceof String");
            }
        } catch(Exception e){
            Log.e("innoticon.key.gem>", e.getMessage());
        }
        return ret;
    }


}
