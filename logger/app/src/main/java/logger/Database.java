package logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Database {

    private final Connection con;

    public Database(String dbFilePath) throws SQLException, IOException{
        con = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);//creates a DB file if one doesnt exist already
        con.createStatement.execute("PRAGMA foreign_keys = ON");//turn on foreign keys for our schema to function as PRAGMA is not inside schema.sql
    } 

}