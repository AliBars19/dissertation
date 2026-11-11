package logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;//all of these imports are for JSON parsing and mapping
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;


public class Parser {
    
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);// Create a single ObjectMapper instance, the congiruation we added is to skip the fields that we deliberately are going to ignore.

    public static List<Prediction> parsePrediction(String json) throws JsonProcessingException {
        return MAPPER.readValue(json, new TypeReference<List<Prediction>>() {});//The typereference keeps our prediction record and the mapper.readvalue simply parses the json string into a list of prediction records.
    }
}