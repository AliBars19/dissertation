package logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

class ParserTest {

    private static List<Prediction> predictions;//list of predictions for testing

    @BeforeAll
    static void load() throws IOException {
        InputStream inputStream = ParserTest.class.getResourceAsStream("/predictions-sample.json");//load the sample json file from resources folder this happens before every test hence @beeforeall
        String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);//read the bytes and convert to string
        predictions = Parser.parsePrediction(json);//parse the json string into a list of prediction records
        
    }

    @Test
    void parseEveryRecord() {
        assertEquals(3, predictions.size());//check that the number of records parsed is 3
    }

    @Test
    void allFieldsFilled() {
        Prediction p = predictions.get(0);//get the first record and compare EVERY field to the expected value
        assertEquals("227", p.vehicleId());
        assertEquals("victoria", p.lineId());
        assertEquals("940GZZLUVIC", p.naptanId());
        assertEquals("Victoria Underground Station", p.stationName());
        assertEquals("Southbound - Platform 4", p.platformName());
        assertEquals("inbound", p.direction());
        assertEquals("940GZZLUBXN", p.destinationNaptanId());
        assertEquals("Brixton Underground Station", p.destinationName());
        assertEquals("Brixton", p.towards());
        assertEquals("At Green Park", p.currentLocation());
        assertEquals(226, p.timeToStation());
        assertEquals("2026-11-11T12:45:48Z", p.expectedArrival());
        assertEquals("2026-11-11T12:42:02.5944272Z", p.timestamp());
    }

    @Test
    void missingDirectionBecomesNull() {
        Prediction p = predictions.get(1);//get the second record and check that the direction field is null
        assertNull(p.direction());//make the direction field null as it is missing in the json file
        assertEquals("940GZZLUBXN", p.destinationNaptanId());
        assertEquals(76, p.timeToStation());
    }

    @Test
    void missingDestinationNull() {
        Prediction p = predictions.get(2);//get the third record and check that the destinationNaptanId field is null and leading zero is kept because sometimes it can default to 34 not 034 which is wbat we want
        assertNull(p.destinationNaptanId());//make the destinationNaptanId field null as it is missing in the json file
        assertNull(p.destinationName());
        assertEquals("Check Front of Train", p.towards());//context lmfao i accidetally did "Check Front of Traing" and it instantly failed. it works hehe
        assertEquals("034", p.vehicleId());
    }

    @Test
    void invalidJsonThrowsException() {
        assertThrows(JsonProcessingException.class, () -> Parser.parsePrediction("invalid json"));//should be fine because Parser throws the excpetion were testing
    }
}