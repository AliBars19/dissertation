package logger;

class DatabaseTest{
    //in this file we'll be creating 'temporary' databases to work on 

    private Path tempDir;
    private Path dbFile;
    private Database db;

    @BeforeEach
    void openDatabase() throws SQLException, IOException {
        dbFile = tempdir.resolve("test.db");
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
        assertNotEquals(first third);//checks if multiple overwrites can be handled
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
        assertEquals(2, newRequest())
    }

}