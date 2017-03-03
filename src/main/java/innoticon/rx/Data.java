package innoticon.rx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.gson.Gson;
import novemberizing.rx.Scheduler;
import novemberizing.rx.Subscribers;
import novemberizing.util.Log;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 11.
 */
@SuppressWarnings({"DanglingJavadoc", "WeakerAccess", "Convert2Lambda", "unused", "Anonymous2MethodRef", "CodeBlock2Expr"})
public class Data<T> extends firebase.rx.Data<T> {
    private static final String Tag = "innoticon.rx.data>";

    private boolean __emitted = false;

    public Data(String path, Class<T> c, Gson gson, boolean once) {
        super(path, c, false, once);
        novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
        if(keys!=null){
            T v = novemberizing.rx.db.Hash.Get(keys.first, keys.second, c, gson);
            if(v!=null) {
                __emitted = true;
                emit(v);
            } else {
                on();
            }
        } else {
            on();
        }
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        Log.d(Tag, this);
        try {
            emit(convert(snapshot));
            __emitted = true;
        } catch(Exception e){
            error(e);
        }
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Log.e(Tag, this);
        __emitted = true;
        error(error.toException());
    }

    public Data<T> exist(novemberizing.ds.on.Single<T> exist, novemberizing.ds.on.Single<Throwable> not){
        if(__emitted){
            exist.on(__current);
        } else {
            subscribe(new Subscribers.Just<T>(){
                @Override
                public void onNext(T item){
                    if(item!=null) {
                        unsubscribe(this);
                        exist.on(item);
                    } else {
                        onError(new Exception("item==null"));
                    }
                }
                @Override public void onError(Throwable e) {
                    unsubscribe(this);
                    not.on(e);
                }
                @Override public void onComplete(){
                    unsubscribe(this);
                    not.on(new Throwable("item==null"));
                }
            });
        }
        return this;
    }

    public static String getSinglePath(String path){
        String ret = "";
        if(path!=null) {
            ret = novemberizing.util.Str.Merge(path.split("[/]"),":",1);
        } else {
            Log.e(Tag, "path==null");
        }
        return ret;
    }

    public static novemberizing.ds.tuple.Pair<String, String> getPath(String path){
        novemberizing.ds.tuple.Pair<String, String> ret = null;
        if(path!=null) {
            String[] strings = path.split("[/]");
            if (strings.length > 3) {
                ret = new novemberizing.ds.tuple.Pair<>();
                ret.first = novemberizing.util.Str.Merge(strings, ":", 1, 3);
                ret.second = novemberizing.util.Str.Merge(strings, ":", 3);
            } else {
                ret = new novemberizing.ds.tuple.Pair<>();
                ret.first = novemberizing.util.Str.Merge(strings, ":", 1);
                ret.second = "";
            }
        } else {
            Log.e(Tag, "path==null");
        }
        return ret;
    }

    public static class Req {
        public static <T> novemberizing.rx.Req.Factory<T> Set(String path, T o, Class<T> c, Gson gson){
            return new novemberizing.rx.Req.Factory<T>(){
                @Override public novemberizing.rx.Req<T> call() { return Data.Set(path, o, c, gson); }
            };
        }

        public static <T> novemberizing.rx.Req.Factory<String> Push(String path, String key){
            return new novemberizing.rx.Req.Factory<String>(){
                @Override public novemberizing.rx.Req<String> call() { return Data.Push(path, key); }
            };
        }

        public static <T> novemberizing.rx.Req.Factory<T> Set(String path, T o, Class<T> c, Gson gson, String from, String to){
            return new novemberizing.rx.Req.Factory<T>(){
                @Override public novemberizing.rx.Req<T> call() { return Data.Set(path, o, c, gson, from, to); }
            };
        }
        public static novemberizing.rx.Req.Factory Del(String path){
            return new novemberizing.rx.Req.Factory(){
                @Override public Object call() { return Data.Del(path); }
            };
        }
        public static <T> novemberizing.rx.Req.Factory<HashMap<String, T>> Bulk(String path, HashMap<String, T> map, Class<T> c, GenericTypeIndicator<HashMap<String, T>> indicator, Gson gson){
            return new novemberizing.rx.Req.Factory<HashMap<String, T>>(){

                @Override
                public novemberizing.rx.Req<HashMap<String, T>> call() {
                    return Data.Bulk(path, map, c, indicator,gson);
                }
            };
        }
    }

    public static <T> innoticon.rx.Data<T> Get(String path, Class<T> c, Gson gson){
        return new innoticon.rx.Data<>(path,c,gson, true);
    }

    public static novemberizing.rx.Req Del(String path){
        novemberizing.rx.Req req = novemberizing.rx.Operator.Req(path, null,
            new novemberizing.ds.on.Triple<String, Object, novemberizing.rx.Req.Callback<Object>>(){
                @Override
                public void on(String path, Object dummy, novemberizing.rx.Req.Callback<Object> res) {
                    if(path!=null){
                        firebase.rx.Data.Del(path)
                            .success(new novemberizing.ds.on.Empty(){

                                @Override
                                public void on() {
                                    novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
                                    if (keys != null) {
                                        novemberizing.rx.db.Hash.Del(keys.first, keys.second)
                                                .success(new novemberizing.ds.on.Empty(){
                                                    @Override
                                                    public void on() {
                                                        res.complete();
                                                    }
                                                })
                                                .fail(new novemberizing.ds.on.Single<Throwable>() {
                                                    @Override
                                                    public void on(Throwable e) {
                                                        res.error(e);
                                                    }
                                                });
                                    } else {
                                        res.error(new Throwable("fail to set at cache because of getPath(\"" + path + "\")"));
                                    }
                                }
                            })
                            .fail(new novemberizing.ds.on.Single<Throwable>() {
                                @Override public void on(Throwable e) { res.error(e); }
                            });
                    } else {
                        res.error(new Throwable("path is null"));
                    }
                }
            });
        req.execute(Scheduler.Local());
        return req;
    }

    public static <T> novemberizing.rx.Req<HashMap<String, T>> Bulk(String path, HashMap<String, T> map, Class<T> c, GenericTypeIndicator<HashMap<String, T>> indicator, Gson gson){
        novemberizing.rx.Req<HashMap<String, T>> req = novemberizing.rx.Operator.Req(path, map, c, indicator, gson,
                new novemberizing.ds.on.Sextuple<String,HashMap<String, T>,Class<T>,GenericTypeIndicator<HashMap<String, T>>,Gson,novemberizing.rx.Req.Callback<HashMap<String, T>>>(){
                    @Override
                    public void on(String path, HashMap<String, T> map, Class<T> c, GenericTypeIndicator<HashMap<String, T>> indicator, Gson gson, novemberizing.rx.Req.Callback<HashMap<String, T>> res) {
                        if(path!=null) {
                            if(map!=null){
                                firebase.rx.Data.Bulk(path, map, indicator)
                                        .success(new novemberizing.ds.on.Empty() {
                                            @Override
                                            public void on() {
                                                novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
                                                if (keys != null) {
                                                    novemberizing.rx.db.Hash.Bulk(keys.first, keys.second, map, gson)
                                                            .success(new novemberizing.ds.on.Empty(){
                                                                @Override
                                                                public void on() {
                                                                    res.complete();
                                                                }
                                                            })
                                                            .fail(new novemberizing.ds.on.Single<Throwable>() {
                                                                @Override
                                                                public void on(Throwable e) {
                                                                    res.error(e);
                                                                }
                                                            });
                                                } else {
                                                    res.error(new Throwable("fail to set at cache because of getPath(\"" + path + "\")"));
                                                }
                                            }
                                        })
                                        .fail(new novemberizing.ds.on.Single<Throwable>() {

                                            @Override
                                            public void on(Throwable e) {
                                                res.error(e);
                                            }
                                        });
                            } else {
                                res.error(new Throwable("o is null"));
                            }
                        } else {
                            res.error(new Throwable("path is null"));
                        }
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }

    public static <T> novemberizing.rx.Req<T> Set(String path, T o, Class<T> c, Gson gson){
        novemberizing.rx.Req<T> req = novemberizing.rx.Operator.Req(path, o, c, gson,
            new novemberizing.ds.on.Quintuple<String,T,Class<T>,Gson,novemberizing.rx.Req.Callback<T>>(){
                private void __on(String path, T o, Class<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res){
                    firebase.rx.Data.Set(path, o, c)
                        .success(new novemberizing.ds.on.Empty() {
                            @Override
                            public void on() {
                                novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
                                if (keys != null) {
                                    novemberizing.rx.db.Hash.Set(keys.first, keys.second, o, gson)
                                        .success(new novemberizing.ds.on.Empty(){
                                            @Override
                                            public void on() {
                                                res.complete();
                                            }
                                        })
                                        .fail(new novemberizing.ds.on.Single<Throwable>() {
                                            @Override
                                            public void on(Throwable e) {
                                                res.error(e);
                                            }
                                        });
                                } else {
                                    res.error(new Throwable("fail to set at cache because of getPath(\"" + path + "\")"));
                                }
                            }
                        })
                        .fail(new novemberizing.ds.on.Single<Throwable>() {

                            @Override
                            public void on(Throwable e) {
                                res.error(e);
                            }
                        });
                }
                @Override
                public void on(String path, T o, Class<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res) {
                    if(path!=null) {
                        if(o!=null){
                            __on(path, o, c, gson, res);
                        } else {
                            res.error(new Throwable("o is null"));
                        }
                    } else {
                        res.error(new Throwable("path is null"));
                    }
                }
            });
        req.execute(Scheduler.Local());
        return req;
    }

    public static <T> novemberizing.rx.Req<T> Set(String path, T o, Class<T> c, Gson gson, String from, String to){
        novemberizing.rx.Req<T> req = novemberizing.rx.Operator.Req(path, o, c, gson,
                new novemberizing.ds.on.Quintuple<String,T,Class<T>,Gson,novemberizing.rx.Req.Callback<T>>(){
                    private void __on(String path, T o, Class<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res){
                        firebase.rx.Data.Set(path, o, c)
                                .success(new novemberizing.ds.on.Empty() {
                                    @Override
                                    public void on() {
                                        novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
                                        if (keys != null) {
                                            novemberizing.rx.db.Hash.Set(keys.first, keys.second, o, gson)
                                                    .success(new novemberizing.ds.on.Empty(){
                                                        @Override
                                                        public void on() {
                                                            res.complete();
                                                        }
                                                    })
                                                    .fail(new novemberizing.ds.on.Single<Throwable>() {
                                                        @Override
                                                        public void on(Throwable e) {
                                                            res.error(e);
                                                        }
                                                    });
                                        } else {
                                            res.error(new Throwable("fail to set at cache because of getPath(\"" + path + "\")"));
                                        }
                                    }
                                })
                                .fail(new novemberizing.ds.on.Single<Throwable>() {

                                    @Override
                                    public void on(Throwable e) {
                                        res.error(e);
                                    }
                                });
                    }
                    @Override
                    public void on(String path, T o, Class<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res) {
                        if(path!=null) {
                            if(o!=null){
                                if(from!=null && to!=null) {
                                    innoticon.rx.Data.Get(client.db.Path.getUserBlockPath(to, from), Long.class, gson)
                                        .exist(exist->{
                                            Log.e("block", o);
                                            res.complete();
                                        },not->{
                                            innoticon.rx.Data.Get(client.db.Path.getUserMutePath(to, from), Long.class, gson)
                                                .exist(exist->{
                                                    __on(client.db.Path.getUserMuteInboxPath(to, path), o, c, gson, res);
                                                }, ok->{
                                                    __on(path, o, c, gson, res);
                                                });
                                        });
                                } else {
                                    __on(path, o, c, gson, res);
                                }
                            } else {
                                res.error(new Throwable("o is null"));
                            }
                        } else {
                            res.error(new Throwable("path is null"));
                        }
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }

    public static <T> novemberizing.rx.Req<T> Set(String path, T o, GenericTypeIndicator<T> c, Gson gson, String from, String to){
        novemberizing.rx.Req<T> req = novemberizing.rx.Operator.Req(path, o, c, gson,
                new novemberizing.ds.on.Quintuple<String,T,GenericTypeIndicator<T>,Gson,novemberizing.rx.Req.Callback<T>>(){
                    private void __on(String path, T o, GenericTypeIndicator<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res){
                        firebase.rx.Data.Set(path, o, c)
                                .success(new novemberizing.ds.on.Empty() {
                                    @Override
                                    public void on() {
                                        novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
                                        if (keys != null) {
                                            novemberizing.rx.db.Hash.Set(keys.first, keys.second, o, gson)
                                                    .success(new novemberizing.ds.on.Empty(){
                                                        @Override
                                                        public void on() {
                                                            res.complete();
                                                        }
                                                    })
                                                    .fail(new novemberizing.ds.on.Single<Throwable>() {
                                                        @Override
                                                        public void on(Throwable e) {
                                                            res.error(e);
                                                        }
                                                    });
                                        } else {
                                            res.error(new Throwable("fail to set at cache because of getPath(\"" + path + "\")"));
                                        }
                                    }
                                })
                                .fail(new novemberizing.ds.on.Single<Throwable>() {

                                    @Override
                                    public void on(Throwable e) {
                                        res.error(e);
                                    }
                                });
                    }
                    @Override
                    public void on(String path, T o, GenericTypeIndicator<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res) {
                        if(path!=null) {
                            if(o!=null){
                                if(from!=null && to!=null) {
                                    innoticon.rx.Data.Get(client.db.Path.getUserBlockPath(to, from), Long.class, gson)
                                       .exist(exist->{
                                           Log.e("block", o);
                                           res.complete();
                                       },not->{
                                           innoticon.rx.Data.Get(client.db.Path.getUserMutePath(to, from), Long.class, gson)
                                               .exist(exist->{
                                                   __on(client.db.Path.getUserMuteInboxPath(to, path), o, c, gson, res);
                                               }, ok->{
                                                   __on(path, o, c, gson, res);
                                               });
                                       });
                                } else {
                                    __on(path, o, c, gson, res);
                                }
                            } else {
                                res.error(new Throwable("o is null"));
                            }
                        } else {
                            res.error(new Throwable("path is null"));
                        }
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }

    public static <T> novemberizing.rx.Req<T> Set(String path, T o, GenericTypeIndicator<T> c, Gson gson){
        novemberizing.rx.Req<T> req = novemberizing.rx.Operator.Req(path, o, c, gson,
                new novemberizing.ds.on.Quintuple<String,T,GenericTypeIndicator<T>,Gson,novemberizing.rx.Req.Callback<T>>(){
                    private void __on(String path, T o, GenericTypeIndicator<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res){
                        Log.e(Tag, "");
                        firebase.rx.Data.Set(path, o, c)
                            .success(new novemberizing.ds.on.Empty() {
                                @Override
                                public void on() {
                                    novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
                                    if (keys != null) {
                                        novemberizing.rx.db.Hash.Set(keys.first, keys.second, o, gson)
                                            .success(new novemberizing.ds.on.Empty(){
                                                @Override
                                                public void on() {
                                                    res.complete();
                                                }
                                            })
                                            .fail(new novemberizing.ds.on.Single<Throwable>() {
                                                @Override
                                                public void on(Throwable e) {
                                                    res.error(e);
                                                }
                                            });
                                    } else {
                                        res.error(new Throwable("fail to set at cache because of getPath(\"" + path + "\")"));
                                    }
                                }
                            })
                            .fail(new novemberizing.ds.on.Single<Throwable>() {

                                @Override
                                public void on(Throwable e) {
                                    res.error(e);
                                }
                            });
                    }
                    @Override
                    public void on(String path, T o, GenericTypeIndicator<T> c, Gson gson, novemberizing.rx.Req.Callback<T> res) {
                        if(path!=null) {
                            if(o!=null){
                                __on(path, o, c, gson, res);
                            } else {
                                res.error(new Throwable("o is null"));
                            }
                        } else {
                            res.error(new Throwable("path is null"));
                        }
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }

    public static <T> novemberizing.rx.Req<T> Push(String path, String key){
        novemberizing.rx.Req<T> req = novemberizing.rx.Operator.Req(path, key,
                new novemberizing.ds.on.Triple<String, String, novemberizing.rx.Req.Callback<T>>(){
                    @Override
                    public void on(String path, String key, novemberizing.rx.Req.Callback<T> res) {
                        if(path.startsWith("/")){
                            novemberizing.ds.tuple.Pair<String, String> keys = getPath(path);
                            path = keys.first + (keys.second!=null && keys.second.length()>0 ? ":" : "") + keys.second;
                        }
                        try (Jedis jedis = redis.Pool.Jedis()) {
                            jedis.rpush(path, key);
                            res.complete();
                        } catch(Exception e){
                            res.error(e);
                        }
                    }
                });
        req.execute(Scheduler.Local());
        return req;
    }
}
