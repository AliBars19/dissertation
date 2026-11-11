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

    private Integer getOrCreate(String text, HashMap<String, Integer> map, String insertSQL, String selectSQL)throws SQLException{
        //Purpose of this function is to check if the one piece of text from one line of the JSON exists or not. if exists cool if not you get
        if(text == null){
            return null;//protects from values that TFL have left out
        }
        Integer id = map.get(text); //reason why im using Integer and not int is for null values
        if(id != null){
            return id;
        }

        try(PreparedStatement insert = con.prepareStatement(insertSQL)){
            insert.setString(1, text);
            insert.executeUpdate();
        }

        try(PreparedStatement select = con.prepareStatement(selectSQL)){
            select.setString(1, text);
            try(ResultSet rs = select.executeQuery()){
                rs.next()//goes down one row so instead of the text its now looking at the value(number)
                id = rs.getInt(1);//selects the first row fo the column its looking at, this is the ID. there will ONLY be one row
            }
        }

        map.put(text,id);//write it to the cache
        return id;
    }


    public void lineId(String tflLine){

    }

    public void stationId(String naptanID, String name){

    }

    public void platformId(String name){

    }

    public void towardsId(String name){

    }

    public void locationId(String name){

    }
}