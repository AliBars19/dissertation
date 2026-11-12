package logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    private final Connection con;

    private HashMap<String, Integer> lineIds = new HashMap<>(); //for speed were gna cache each response so that we can look up the data from a line with just a single number nothing else
    private HashMap<String, Integer> stationIds = new HashMap<>();// this style of compacting also helps reduce data size by a LOT and is just smart lmfao
    private HashMap<String, Integer> platformIds = new HashMap<>();
    private HashMap<String, Integer> towardsIds = new HashMap<>();
    private HashMap<String, Integer> locationIds = new HashMap<>();

    public Database(String dbFilePath) throws SQLException, IOException{
        con = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);//creates a DB file if one doesnt exist already
        con.createStatement().execute("PRAGMA foreign_keys = ON");//turn on foreign keys for our schema to function as PRAGMA is not inside schema.sql
        
        try(InputStream stream = Database.class.getResourceAsStream("/schema.sql")){
            String schema = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            con.createStatement().executeUpdate(schema);
        }
    } 

    public void close() throws SQLException{
        con.close();
    }

    private Integer getOrCreate(String key, HashMap<String, Integer> map, String insertSQL, String selectSQL, String... values)throws SQLException{//reason why its  string... is cos we need multipple values in there and that datatype is like an array of strings basically.
        //Purpose of this function is to check if the one piece of text from one line of the JSON exists or not. if exists cool if not you get
        if(key == null){
            return null;//protects from values that TFL have left out
        }
        Integer id = map.get(key); //reason why im using Integer and not int is for null values
        if(id != null){
            return id;
        }

        try(PreparedStatement insert = con.prepareStatement(insertSQL)){
            for(int i = 0; i<values.length; i++){//heavy futureproofing right here 
                insert.setString(1+i, values[i]);
            }
            insert.executeUpdate();
        }

        try(PreparedStatement select = con.prepareStatement(selectSQL)){
            select.setString(1, key);
            try(ResultSet rs = select.executeQuery()){
                rs.next();//goes down one row so instead of the text its now looking at the value(number)
                id = rs.getInt(1);//selects the first row fo the column its looking at, this is the ID. there will ONLY be one row
            }
        }

        map.put(key,id);//write it to the cache
        return id;
    }

    public int insertRequest(String sentAt, String receivedAt, Integer httpStatus, Integer bytes, Integer recordCount, String tflNewestTimestamp, String error, String rawFile, String lines)  throws SQLException{
        String insertSQL = ("INSERT INTO tflrequests(sent_at, received_at, http_status, bytes, record_count, tfl_newest_timestamp, error, raw_file, lines) VALUES(?,?,?,?,?,?,?,?,?) RETURNING request_id");//exec query cos of the RETURNING at the end of sql
        try(PreparedStatement insert = con.prepareStatement(insertSQL)){
            insert.setObject(1,sentAt);//point of this function is to keep track of our global counter for requesti_id
            insert.setObject(2,receivedAt);
            insert.setObject(3,httpStatus);
            insert.setObject(4,bytes);
            insert.setObject(5,recordCount);
            insert.setObject(6,tflNewestTimestamp);
            insert.setObject(7,error);
            insert.setObject(8,rawFile);
            insert.setObject(9,lines);
            try(ResultSet rs = insert.executeQuery()){
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int lineId(String tflLine)throws SQLException{
        String insertSQL = "INSERT OR IGNORE INTO lines(tfl_line) VALUES (?)";
        String selectSQL = "SELECT line_id FROM lines WHERE tfl_line = ?";
        int ret = getOrCreate(tflLine,lineIds,insertSQL,selectSQL, tflLine);
        return ret;
    }

    public Integer stationId(String naptanID, String name)throws SQLException{
        String insertSQL = "INSERT OR IGNORE INTO stations(naptan_id, name) VALUES (?, ?)";
        String selectSQL = "SELECT station_id FROM stations WHERE naptan_id = ?";
        Integer ret = getOrCreate(naptanID, stationIds,insertSQL,selectSQL, naptanID,name);
        return ret;
    }

    public int platformId(String name)throws SQLException{
        String insertSQL = "INSERT OR IGNORE INTO platforms(name) VALUES(?)";
        String selectSQL = "SELECT platform_id FROM platforms WHERE name = ?";
        int ret = getOrCreate(name,platformIds,insertSQL,selectSQL,name);
        return ret;
    }

    public int towardsId(String name)throws SQLException{
        String insertSQL = "INSERT OR IGNORE INTO towards(name) VALUES (?)";
        String selectSQL = "SELECT towards_id FROM towards WHERE name = ?";
        int ret = getOrCreate(name,towardsIds,insertSQL,selectSQL,name);
        return ret;
    }

    public Integer locationId(String name)throws SQLException{
        String insertSQL = "INSERT OR IGNORE INTO locations(name) VALUES (?)";
        String selectSQL = "SELECT location_id FROM locations WHERE name = ?";
        Integer ret = getOrCreate(name,locationIds,insertSQL,selectSQL,name);
        return ret;
    }
}