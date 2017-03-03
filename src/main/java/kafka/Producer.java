package kafka;

import com.google.gson.Gson;
import novemberizing.util.Log;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 * Created by novemberizing on 17. 2. 9.
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
public class Producer {
    private static final String Tag = "producer>";
    private KafkaProducer<String, String> __producer = null;
    private HashMap<String, Object> __properties = new HashMap<>();
    private boolean __cancel = false;
    private Gson __gson;
    private String __topic;

    public Producer add(String key, Object value) {
        __properties.put(key, value);
        return this;
    }

    public Producer topic(String topic){
        __topic = topic;
        return this;
    }

    public Producer gson(Gson gson){
        __gson = gson;
        return this;
    }

    public Gson gson(){ return __gson; }

    public void cancel(boolean v){ __cancel = v; }

    public void send(innoticon.ds.Record record){
        if(__producer!=null) {
            try {
                __producer.send(new ProducerRecord<>(__topic, record.key(), __gson.toJson(record)));
            } catch(Exception e){
                Log.e(Tag, e.getMessage());
                FailDB db = FailDB.Get();
                db.add(__topic, record);
            }
        } else {
            FailDB db = FailDB.Get();
            db.add(__topic, record);
        }
    }

    public void init(){
        if(__producer==null){
            __cancel = false;
            Properties properties = new Properties();
            for(Map.Entry<String, Object> entry : __properties.entrySet()){
                properties.put(entry.getKey(), entry.getValue());
            }
            __producer = new KafkaProducer<>(properties);
        } else {
            Log.e(Tag, "__producer!=null");
        }
    }
}
