package kafka;

import com.google.gson.Gson;
import novemberizing.util.Log;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static server.Exception.*;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 8.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Consumer implements Runnable {
    private static final String Tag = "kafka>";
    public interface On extends novemberizing.ds.on.Quadruple<  innoticon.ds.Req,
                                                                ConsumerRecord<String, String>,
                                                                novemberizing.ds.on.Single<innoticon.ds.Res>,
                                                                novemberizing.ds.on.Single<Throwable>> {}

    private KafkaConsumer<String, String> __consumer = null;
    private HashMap<String, Object> __properties = new HashMap<>();
    private String __topic;
    private boolean __cancel = false;
    private Gson __gson;
    private Producer __producer;

    private HashMap<Integer, On> __callbacks = new HashMap<>();

    public Consumer gson(Gson gson){
        __gson = gson;
        return this;
    }

    public Consumer set(Producer producer){
        __producer = producer;
        return this;
    }

    public Consumer add(String key, Object value){
        __properties.put(key, value);
        return this;
    }

    public Consumer add(int type, Consumer.On on){
        __callbacks.put(type, on);
        return this;
    }

    public Consumer topic(String topic){
        __topic = topic;
        return this;
    }

    public <T> T from(String json, Class<T> c){ return Log.i(c.getName().toLowerCase() + ":convert>",__gson.fromJson(json, c)); }

    public void cancel(boolean v){ __cancel = v; }

    public void init(){
        if(__consumer==null){
            __cancel = false;
            Properties properties = new Properties();
            for(Map.Entry<String, Object> entry : __properties.entrySet()){
                properties.put(entry.getKey(), entry.getValue());
            }
            __consumer = new KafkaConsumer<>(properties);
            if(__producer!=null){
                if(__producer.gson()==null) {
                    __producer.gson(__gson);
                }
                __producer.init();
            } else {
                Log.e(Tag, "__producer!=null");
            }
            __consumer.subscribe(novemberizing.util.Collections.List(__topic));
        } else {
            Log.e(Tag, "__consumer!=null");
        }
    }

    public void exception(innoticon.ds.Record record){ __producer.send(record); }

    public void next(innoticon.ds.Req req, ConsumerRecord<String, String> record){
        if(req!=null){
            On callback = __callbacks.get(req.type());
            if(callback!=null){
                callback.on(req, record,
                    res->{
                        if(res!=null){
                            try {
                                innoticon.rx.Data.Set(db.Path.getResponsePath(req), res, innoticon.ds.Res.class)
                                    .fail(e -> exception(Log.e(Tag, new innoticon.ds.Record(record, FAIL_TO_SET_RES, e.getMessage()))));
                            } catch (Exception e) {
                                exception(new innoticon.ds.Record(record, FAIL_TO_GET_RESPONSE_PATH,"res==null"));
                            }
                        } else {
                            exception(new innoticon.ds.Record(record, FAIL_TO_GENERATE_RES,"fail to db.Path.getResponsePath(req)"));
                        }
                    },
                    e->exception(new innoticon.ds.Record(record, FAIL_TO_CALLBACK, e.getMessage())));
            } else {
                exception(new innoticon.ds.Record(record, NOT_EXIST_CALLBACK,"callback==null"));
            }
        } else {
            exception(new innoticon.ds.Record(record, FAIL_TO_DESERIALIZE_STRING_TO_REQ, "req==null"));
        }
    }

    @Override
    public void run() {
        init();
        while(!__cancel){
            ConsumerRecords<String, String> records = __consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                try {
                    next(__gson.fromJson(record.value(), innoticon.ds.Req.class), record);
                } catch(Exception e){
                    exception(Log.e(Tag, new innoticon.ds.Record(record, FAIL_TO_DESERIALIZE_STRING_TO_REQ, e.getMessage())));
                }
            }
        }
    }
}