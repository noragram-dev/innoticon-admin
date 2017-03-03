package innoticon.ds;

import com.google.gson.annotations.Expose;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 */
@SuppressWarnings("unused")
public class Record {
    @Expose private String key;
    @Expose private String value;
    @Expose private String topic;
    @Expose private long offset;
    @Expose private long timestamp;
    @Expose private long checksum;
    @Expose private int partition;
    @Expose private int error;
    @Expose private String message;

    public String key(){ return key; }
    public String value(){ return value; }
    public String topic(){ return topic; }
    public long offset(){ return offset; }
    public long timestamp(){ return timestamp; }
    public long checksum(){ return checksum; }
    public int partition(){ return partition; }
    public int error(){ return error; }
    public String message(){ return message; }

    public void key(String v){ key = v; }
    public void value(String v){ value = v; }
    public void topic(String v){ topic = v; }
    public void offset(long v){ offset = v; }
    public void timestamp(long v){ timestamp = v; }
    public void checksum(long v){ checksum = v; }
    public void partition(int v){ partition = v; }
    public void error(int v){ error = v; }
    public void message(String v){ message = v; }

    public Record(ConsumerRecord<String, String> record, int error, String message){
        this.key = record.key();
        this.value = record.value();
        this.topic = record.topic();
        this.offset = record.offset();
        this.timestamp = record.timestamp();
        this.checksum = record.checksum();
        this.partition = record.partition();
        this.error = error;
        this.message = message;
    }

    public Record(ConsumerRecord<String, String> record){
        key = record.key();
        value = record.value();
        topic = record.topic();
        offset = record.offset();
        timestamp = record.timestamp();
        checksum = record.checksum();
        partition = record.partition();
        this.error = 0;
        this.message = "";
    }
}
