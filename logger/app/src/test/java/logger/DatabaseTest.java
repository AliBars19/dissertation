package logger;

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

    //some helper functions coming in here

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
        List<Prediction> p = loadFixture();
        db.insertMultiplePredictions(newRequest(), p);
        
        assertEquals(3, count("tflresponses"));
        assertEquals(3, count("stations"));
        assertEquals(3, count("lines"));
        assertEquals(3, count("platforms"));
    }

    @Test
    void insertMultiplePredictionsMalformedTest() throws SQLException, IOException{
        List<Prediction> predictions = new ArrayList<>(loadFixture());
        
        predictions.add(malformedRequest1);
        predictions.add(malformedRequest2);
        predictions.add(malformedRequest3);

        int requestId = newRequest();

        assertThrows(RuntimeException.class, () -> db.insertMultiplePredictions(requestId, predictions));
        assertEquals(0, count("tflresponses"));
    }

}