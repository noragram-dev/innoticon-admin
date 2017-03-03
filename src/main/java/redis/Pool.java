package redis;

import com.google.gson.annotations.Expose;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 */
@SuppressWarnings("WeakerAccess")
public class Pool {
    private static Pool __singleton = null;

    public static Pool Init(String host){
        synchronized (redis.Pool.class) {
            if(__singleton==null){
                __singleton = new Pool(host);
            }
        }
        return __singleton;
    }

    public static Pool Get(){ return __singleton; }

    public static Jedis Jedis(){
        Pool pool = Get();
        return pool.get();
    }

    private JedisPool __pool = null;
    @Expose private String __host = null;

    public Pool(String host){
        __host = host;
        if(__host==null || __host.length()==0){
            __host = "127.0.0.1";
        }
        init();
    }

    public Jedis get(){ return __pool.getResource(); }

    public void init(){
        if(__pool==null) {
            final JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(128);
            poolConfig.setMaxIdle(128);
            poolConfig.setMinIdle(16);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
            poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
            poolConfig.setNumTestsPerEvictionRun(3);
            poolConfig.setBlockWhenExhausted(true);
            __pool = new JedisPool(poolConfig, __host);
        }
    }
}
