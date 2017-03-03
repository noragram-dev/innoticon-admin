package novemberizing.rx.db;

import com.google.gson.Gson;
import novemberizing.ds.tuple.Triple;
import novemberizing.rx.Observable;
import novemberizing.rx.Scheduler;
import novemberizing.util.Log;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 */
@SuppressWarnings({"DanglingJavadoc", "WeakerAccess", "unused", "Convert2Lambda"})
public class Hash extends Observable<Triple<Integer,String, String>> implements Runnable {

    public static <T> novemberizing.rx.Req<T> Bulk(String category, String key, Map<String, T> map, Gson gson){
        novemberizing.rx.Req<T> req = novemberizing.rx.Operator.Req(category, map,
                (__category, __map, res)->{
                    Jedis jedis;
                    try {
                        HashMap<String, String > real = new HashMap<>();
                        try {
                            for(Map.Entry<String, T> entry : __map.entrySet()){
                                real.put(key + ":" + entry.getKey(),gson.toJson(entry.getValue()));
                            }
                        } catch(Exception e){
                            Log.e("bulk:error>", e.getMessage());
                        }
                        if(real.size()>0){
                            jedis = redis.Pool.Jedis();
                            jedis.hmset(category, real);
                        } else {
                            res.error(new Throwable("no data"));
                        }
                    } catch(Exception e){
                        res.error(e);
                    } finally {
                        res.complete();
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }
    public static <T> novemberizing.rx.Req<T> Set(String category, String key, T o, Gson gson){
        novemberizing.rx.Req<T> req = novemberizing.rx.Operator.Req(category, key, o,
                (__category, __key, value, res)->{
                    Jedis jedis;
                    try {
                        jedis = redis.Pool.Jedis();
                        jedis.hset(__category, __key, gson.toJson(value));
                    } catch(Exception e){
                        res.error(e);
                    } finally {
                        res.complete();
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }

    public static novemberizing.rx.Req<Object> Del(String category, String key){
        novemberizing.rx.Req<Object> req = novemberizing.rx.Operator.Req(category, key,
                (__category, __key, res)->{
                    Jedis jedis;
                    try {
                        jedis = redis.Pool.Jedis();
                        jedis.hdel(__category, __key);
                        res.complete();
                    } catch(Exception e){
                        res.error(e);
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }

    public static <T> T Get(String category, String key, Class<T> c, Gson gson){
        T ret = null;
        try {
            Jedis jedis = redis.Pool.Jedis();
            String v = jedis.hget(category, key);
            if(v!=null){
                ret = gson.fromJson(v, c);
            }
        } catch(Exception e){
            Log.d("novemberizing.rx.data>", e.getMessage());
        }
        return ret;
    }

    public static class Req {
        public static <T> novemberizing.rx.Req.Factory<T> Set(String category, String key, T o, Gson gson){
            return new novemberizing.rx.Req.Factory<T>(){
                @Override public novemberizing.rx.Req<T> call() { return Hash.Set(category, key, o, gson); }
            };
        }
        public static novemberizing.rx.Req.Factory<Object> Del(String category, String key){
            return new novemberizing.rx.Req.Factory<Object>(){
                @Override public novemberizing.rx.Req<Object> call() { return Hash.Del(category, key); }
            };
        }
    }

    @Override public void run() {}
}
