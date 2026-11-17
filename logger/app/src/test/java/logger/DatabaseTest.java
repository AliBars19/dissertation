package logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseTest{
    //in this file we'll be creating 'temporary' databases to work on 

    @TempDir
    Path tempDir;
    private Path dbFile;
    private Database db;

    Prediction malformedRequest1 = new Prediction("999" , "victoria", "940GZZLUVIC", "Victoria Underground Station", null, "inbound", null, null, "Brixton", "At Green Park", "2026-11-16T12:05:00Z", "2026-11-16T12:00:00Z", 300);
    Prediction malformedRequest2 = new Prediction("972" , null, "940GZZLUVIC", "Victoria Underground Station", null, null, null, null, "Brixton", "At Green Park", "2026-11-16T12:05:00Z", "2026-11-16T12:00:00Z", 300);
    Prediction malformedRequest3 = new Prediction(null , null, null, null, null, null, null, null, null, null, null, null, 200);

    //this file is going to use the same predictions sample that ParserTest.java did

    @BeforeEach
    void openDatabase() throws SQLException, IOException {
        dbFile = tempDir.resolve("test.db");
        db = new Database(dbFile.toString());
    }//creates a new db before each test

    @AfterEach
    void closeDatabase() throws SQLException {
        db.close();//closes the db after each test
    }

    private List<Prediction> loadJSON() throws IOException {
        try(InputStream in = DatabaseTest.class.getResourceAsStream("/predictions-sample.json")){
            return Parser.parsePrediction(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }//does as the function name states, it loads the json and parses the raw data so its usable in the test
    }

    private int count(String table) throws SQLException{
        try(
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + dbFile);//setting up the con to sqlite like we do in main
            ResultSet rs = con.createStatement().executeQuery("SELECT count(*) FROM " + table)//it literally counts the rows in the table that were requesting hence the name
        ){
            rs.next();
            return rs.getInt(1);
        }
    }

    private int newRequest() throws SQLException {
        return db.insertRequest("2026-11-16T12:00:00Z", "2026-11-16T12:00:01Z", 200, 1000, 3,
                "2026-11-16T11:59:10Z", null, "test.jsonl.gz", "victoria");
    }

    @Test
    void overwriteTest() throws SQLException { 
        int first = db.platformId("Southbound - Platform 4");
        int second = db.platformId("Southbound - Platform 4");
        int third = db.platformId("Northbound - Platform 3");

        assertEquals(first,second);
        assertNotEquals(first, third);//checks if multiple overwrites can be handled
        assertEquals(2, count("platforms"));
    }

    @Test
    void missingStationTest() throws SQLException{//all tests throw sqlexception btw because were using db in all of them hence name of the file lol
        assertNull(db.stationId(null,null));
        assertEquals(0, count("stations"));
    }
    
    @Test
    void incrementalRequestIdTest() throws SQLException{
        assertEquals(1, newRequest());
        assertEquals(2, newRequest());
    }

    @Test
    void insertMultiplePredictionsTest() throws SQLException, IOException{
        List<Prediction> p = loadJSON();
        db.insertMultiplePredictions(newRequest(), p);
        
        assertEquals(3, count("tflresponses"));
        assertEquals(3, count("stations"));
        assertEquals(2, count("lines"));
        assertEquals(3, count("platforms"));
    }

    @Test
    void insertMultiplePredictionsMalformedTest() throws SQLException, IOException{
        List<Prediction> predictions = new ArrayList<>(loadJSON());
        
        predictions.add(malformedRequest1);
        predictions.add(malformedRequest2);
        predictions.add(malformedRequest3);

        int requestId = newRequest();

        assertThrows(RuntimeException.class, () -> db.insertMultiplePredictions(requestId, predictions));
        assertEquals(0, count("tflresponses"));
    }

}