package kafka;

import com.google.gson.Gson;
import novemberizing.ds.tuple.Triple;
import novemberizing.util.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FailDB extends sqlite.DB {
    private static final String Tag = "local>";

    public static final String TB_INNOTICON_RETRY_RECORD = "TB_INNOTICON_RETRY_RECORD";

    private static FailDB __singleton = null;

    private static FailDB Init(String url, Gson gson){ return new FailDB(url, gson); }

    public static FailDB Get(){ return __singleton; }

    private Gson __gson;

    private FailDB(String url, Gson gson){
        super(url);
        __gson = gson;
        initialize();
    }

    public void initialize(){
        if(open()){
            try {
                Statement statement = __connection.createStatement();
                statement.setQueryTimeout(30);
                statement.executeUpdate(String.format(Locale.getDefault(),
                        "CREATE TABLE IF NOT EXISTS %s (" +
                                "ID INTEGER PRIMARY KEY, " +
                                "TOPIC TEXT NOT NULL, " +
                                "RECORD TEXT NOT NULL, " +
                                "TIMESTAMP LONG NOT NULL)",
                        TB_INNOTICON_RETRY_RECORD));
                statement.close();
            } catch (SQLException e) {
                Log.e(Tag, e.getMessage());
            }
        }
    }

    public void add(String topic, innoticon.ds.Record record){
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO " + TB_INNOTICON_RETRY_RECORD + " " +
                    "(TOPIC, RECORD, TIMESTAMP) " +
                    "VALUES " +
                    "(?, ?, ?)";
            statement = __connection.prepareStatement(sql);
            statement.setString(1, topic);
            statement.setString(2, __gson.toJson(record));
            statement.setLong(3, System.currentTimeMillis());
            statement.execute();
        } catch(SQLException e){
            Log.e(Tag, e.getMessage());
        } finally {
            if(statement!=null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Triple<Integer, String, innoticon.ds.Record>> records(int count){
        List<Triple<Integer, String, innoticon.ds.Record>> ret = null;
        String sql = "SELECT * FROM " + TB_INNOTICON_RETRY_RECORD + " LIMIT " + count + " ORDER BY ID ASC";
        Statement statement = null;
        try {
            statement = __connection.createStatement();
            ResultSet set = statement.executeQuery(sql);
            ret = new LinkedList<>();
            while(set.next()){
                try {
                    ret.add(new Triple<>(   set.getInt(1),
                            set.getString(2),
                            __gson.fromJson(set.getString(3), innoticon.ds.Record.class)));
                } catch(Exception e){
                    Log.e(Tag, e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement!=null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    Log.e(Tag, e.getMessage());
                }
            }
        }
        return ret;
    }

    public void del(Triple<Integer, String, innoticon.ds.Record> tuple){
        String sql = "DELETE FROM " + TB_INNOTICON_RETRY_RECORD + " WHERE ID=?";
        PreparedStatement statement = null;
        try {
            statement = __connection.prepareStatement(sql);
            statement.setInt(1, tuple.first);
            statement.execute();
        } catch (SQLException e) {
            Log.e(Tag, e.getMessage());
        } finally {
            if(statement!=null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    Log.e(Tag, e.getMessage());
                }
            }
        }
    }
}