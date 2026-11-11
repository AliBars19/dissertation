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
        try{
            InputStream inputStream = ParserTest.class.getResourceAsStream("/predictions-sample.json");//load the sample json file from resources folder
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);//read the bytes and convert to string
            predictions = Parser.parsePrediction(json);//parse the json string into a list of prediction records
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void parsesEveryRecord() {
        assertEquals(3, predictions.size());//check that the number of records parsed is 3
    }

}