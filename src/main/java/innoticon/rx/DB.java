package innoticon.rx;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import novemberizing.ds.tuple.Pair;
import novemberizing.rx.Req;
import novemberizing.rx.Scheduler;
import novemberizing.rx.Subscribers;
import novemberizing.util.Log;
import redis.clients.jedis.Jedis;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 25.
 */
@SuppressWarnings("DanglingJavadoc")
public class DB {
    private static final String Tag = "innoticon.rx.DB";

    public static String getSinglePath(String path){
        String ret = "";
        if(path!=null) {
            ret = novemberizing.util.Str.Merge(path.split("[/]"),":",1);
        } else {
            Log.e(Tag, "path==null");
        }
        return ret;
    }

    public static Pair<String, String> getPath(String path){
        Pair<String, String> ret = null;
        if(path!=null) {
            String[] strings = path.split("[/]");
            if (strings.length > 3) {
                ret = new Pair<>();
                ret.first = novemberizing.util.Str.Merge(strings, ":", 1, 3);
                ret.second = novemberizing.util.Str.Merge(strings, ":", 3);
            } else {
                ret = new Pair<>();
                ret.first = novemberizing.util.Str.Merge(strings, ":", 1);
                ret.second = "";
            }
        } else {
            Log.e(Tag, "path==null");
        }
        return ret;
    }

    public static class Get<Z> extends Req<Z> {

        public Get<Z> value(novemberizing.ds.on.Single<Z> callback){
            success(callback);
            return this;
        }

        public Get(String key, GenericTypeIndicator<Z> indicator){
            super(key, new novemberizing.ds.on.Pair<String, Callback<Z>>(){
                @Override
                public void on(String key, Callback<Z> res) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference(key);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                Z out = snapshot.getValue(indicator);
                                if(out!=null) {
                                    res.next(out, true);
                                } else {
                                    res.next(null, true);
                                }
                            } catch(Exception e){
                                res.error(e, true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            res.error(error.toException(), true);
                        }
                    });
                }
            });
        }

        public Get(String key){
            super(key, new novemberizing.ds.on.Pair<String, Callback<Z>>(){
                @Override
                public void on(String key, Callback<Z> res) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference(key);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                Z out = snapshot.getValue(new GenericTypeIndicator<Z>(){});
                                if(out!=null) {
                                    res.next(out, true);
                                } else {
                                    res.next(null, true);
                                }
                            } catch(Exception e){
                                res.error(e, true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            res.error(error.toException(), true);
                        }
                    });
                }
            });
        }

        public Get(String key, Class<Z> c, boolean cache){
            super(key, c, new novemberizing.ds.on.Triple<String, Class<Z>, Callback<Z>>(){
                @Override
                public void on(String key, Class<Z> c, Callback<Z> res) {
                    Z out = null;
                    if(cache){
                        Gson gson = innoticon.Client.Gson();
                        Pair<String, String> keys = getPath(key);
                        if(keys!=null){
                            try (Jedis jedis = redis.Pool.Jedis()) {
                                out = gson.fromJson(jedis.hget(keys.first, keys.second), c);
                            } catch(Exception e){
                                Log.e("cache" + key + ":error", e.getMessage());
                            }
                        } else {

                        }
                    } else {
                        Log.e("cache" + key + ":error", "fail to get at cache because of getPath(\"" + key + "\")");
                    }
                    if(out==null){
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference(key);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                try {
                                    Z out = snapshot.getValue(c);
                                    if(out!=null) {
                                        res.next(out, true);
                                    } else {
                                        res.next(null, true);
                                    }
                                } catch(Exception e){
                                    res.error(e, true);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                res.error(error.toException(), true);
                            }
                        });
                    } else {
                        res.next(out, true);
                    }
                }
            });
        }
    }

    public static class Set extends novemberizing.rx.operators.Sync<Pair<String, Object>, Boolean> {

        @Override
        protected void on(Task<Pair<String, Object>, Boolean> task, Pair<String, Object> in) {
            if(in!=null){
                if(in.first!=null && in.first.length()>0) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference(in.first);
                    reference.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            mutableData.setValue(in.second);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError error, boolean commit, DataSnapshot snapshot) {
                            if(error!=null){
                                task.error(error.toException());
                                task.next(false, true);
                            } else {
                                task.next(true, true);
                            }
                        }
                    });
                } else {
                    task.error(new RuntimeException("in.first!=null && in.first.length()>0"), true);
                }
            } else {
                task.error(new RuntimeException("in=null"), true);
            }
        }
    }

    private final Set __set = new Set();

    public novemberizing.rx.Task<Pair<String, Object>, Boolean> set(String key, Object value){
        /**
         * todo implement local store ...
         */
        novemberizing.rx.Task<Pair<String, Object>, Boolean> task = __set.exec(new Pair<>(key, value));
        task.subscribe(new Subscribers.Just<Boolean>(key){
            @Override public void onNext(Boolean res){ Log.d(key, res); }
            @Override public void onError(Throwable e){ Log.e(key, e); }
            @Override public void onComplete(){ Log.d(key, "complete"); }
        });
        return task;
    }

    public novemberizing.rx.Task<Pair<String, Object>, Boolean> set(String key, Object value, boolean cache){
        if(cache){
            Gson gson = innoticon.Client.Gson();
            Pair<String, String> keys = getPath(key);
            if (keys != null) {
                try (Jedis jedis = redis.Pool.Jedis()){
                    jedis.hset(keys.first, keys.second, gson.toJson(value));
                } catch(Exception e){
                    Log.e("cache" + key + ":error", e.getMessage());
                }
            } else {
                Log.e("cache" + key + ":error", "fail to set at cache because of getPath(\"" + key + "\")");
            }
        }
        novemberizing.rx.Task<Pair<String, Object>, Boolean> task = __set.exec(new Pair<>(key, value));
        task.subscribe(new Subscribers.Just<Boolean>(key){
            @Override public void onNext(Boolean res){ Log.d(key, res); }
            @Override public void onError(Throwable e){ Log.e(key, e); }
            @Override public void onComplete(){ Log.d(key, "complete"); }
        });
        return task;
    }

    public novemberizing.rx.Task<Pair<String, Object>, Boolean> del(String key, boolean cache){
        if(cache){
            Pair<String, String> keys = getPath(key);
            if (keys != null) {
                Jedis jedis;
                try {
                    jedis = redis.Pool.Jedis();
                    jedis.hdel(keys.first, keys.second);
                } catch(Exception e){
                    Log.e("cache" + key + ":error", e.getMessage());
                }
            } else {
                Log.e("cache" + key + ":error", "fail to set at cache because of getPath(\"" + key + "\")");
            }
        }
        return set(key, null);
    }

    public novemberizing.rx.Task<Pair<String, Object>, Boolean> del(String key){
        return set(key, null);
    }

    public <Z> Get<Z> get(String key){
        innoticon.rx.DB.Get<Z> req = new innoticon.rx.DB.Get<>(key);
        req.execute(Scheduler.Local());
        return req;
    }

    public <Z> Get<Z> get(String key, GenericTypeIndicator<Z> indicator){
        innoticon.rx.DB.Get<Z> req = new innoticon.rx.DB.Get<>(key, indicator);
        req.execute(Scheduler.Local());
        return req;
    }

    public <Z> Get<Z> get(String key, Class<Z> c){
        innoticon.rx.DB.Get<Z> req = new innoticon.rx.DB.Get<>(key, c, false);
        req.execute(Scheduler.Local());
        return req;
    }

    public <Z> Get<Z> get(String key, Class<Z> c, boolean cache){
        innoticon.rx.DB.Get<Z> req = new innoticon.rx.DB.Get<>(key, c, cache);
        req.execute(Scheduler.Local());
        return req;
    }

    public void push(String key, String value){
        if(key.startsWith("/")){
            Pair<String, String> keys = getPath(key);
            key = keys.first + (keys.second!=null && keys.second.length()>0 ? ":" : "") + keys.second;
        }
        try (Jedis jedis = redis.Pool.Jedis()){
            jedis.rpush(key, value);
        } catch(Exception e){
            Log.e(Tag, e.getMessage());
        }
    }


    public DB(){}
}
