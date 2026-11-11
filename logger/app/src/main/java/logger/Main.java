package logger;
import java.net.*;
import java.net.http.*;
import java.io.IOException;
import java.time.Instant;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.zip.GZIPOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {

    private static final DateTimeFormatter folderFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(java.time.ZoneOffset.UTC);//so for these two var's we get the current times because our design is going to be that raw data is split on an hourly basis with one folder containng an hours worth of raw data right, so this sets that up
    private static final DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH'Z'").withZone(java.time.ZoneOffset.UTC);
    private static int requestId = 0; // Initialize requestId to 0

    public static void main(String[] args) {
        
        String key = System.getenv("TFL_UNIFIED_API_KEY_PRIMARY");
        String dataDir = System.getenv("LOGGER_DATA_DIR");
        String dbFilePath = System.getenv("DB_FILE_PATH");

        if (key == null || key.isEmpty()) {//guard to safely pull api key from env
            System.err.println("Error: TFL_UNIFIED_API_KEY_PRIMARY has failed");
            key = System.getenv("TFL_UNIFIED_API_KEY_SECONDARY");
            if (key == null || key.isEmpty()) {
                System.err.println("Error: TFL_UNIFIED_API_KEY_SECONDARY has failed");
                System.exit(1);
            }
            }

        if (dataDir == null || dataDir.isEmpty()) {//guard to safely pull data dir from env
            System.err.println("Error: LOGGER_DATA_DIR has failed");
            System.exit(1);}

        if (dbFilePath == null || dbFilePath.isEmpty()) {//guard to safely pull db file path from env
            System.err.println("Error: DB_FILE_PATH has failed");
            System.exit(1);}

        
        
        HttpClient client = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(10)).build();//testing a fetch, victoria line seems most reliable so its the one that we are going to be testing i beliebve this was in the decisions log aready at some point
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(java.time.Duration.ofSeconds(10))
                .uri(URI.create("https://api.tfl.gov.uk/Line/victoria/Arrivals?app_key=" + key))
                .build();
        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();//scheduler that we are gna be using to poll the func logandsave
        scheduler.scheduleAtFixedRate(() -> logAndSave(client, request, dataDir), 0, 30, TimeUnit.SECONDS);//polling it eevry 30 seconds


    }

    public static void logAndSave(HttpClient client, HttpRequest request, String dataDir){
        try {
            requestId++; 
            Instant sentTime = Instant.now();

            String folderName = folderFormatter.format(sentTime);
            String fileName = fileFormatter.format(sentTime) + ".jsonl.gz";//jsonl.gz because file will be zipped for compression and jsonl because each line is a seperate json object that will be parsed later on.
            Path filePath = Paths.get(dataDir, "raw", folderName, fileName);//this is the path to the folder that we are going to be creating, it will be in the data dir, then raw, then the folder name which is the date, then the file name which is the date and hour ezpz
            Files.createDirectories(filePath.getParent());
            System.out.println("Folder created: " + filePath);

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if(response.statusCode() == 200) {
                System.out.println("200 OK");

                if( response.body().isEmpty() || response.body().charAt(0) != '[' || response.body().contains("\n")) {//this is to check that the response is healthy and so that we can parse it 
                    System.err.println("Error: Response body is not a valid JSON array or is empty or contains newlines");
                    return;
                } 

                String line = //this is one line of our response of what were gna save
                    "{\"request_id\":" + requestId + ",\"sent_at\":\"" +
                    sentTime.toString() +
                    "\",\"response_body\":" +//this is building each line of the string one by one this is js a test for now
                    response.body() +
                    "}";
                try (
                    BufferedWriter writer = 
                        new BufferedWriter(
                        new OutputStreamWriter(//this turns text to utf8 for json
                        new GZIPOutputStream(//these really messy looking lines are just to make sure that the file is written in a compressed format so that we can save space and not have to worry about running out of space on the server this is important because we are going to be logging a lot of data i mean a LOT of data, two weeks worth.
                        new FileOutputStream(filePath.toFile(), true)), StandardCharsets.UTF_8)))
                    {
                        writer.write(line);
                        writer.write("\n");
                        System.out.println("saved to" + filePath);
                    }
            
                catch (IOException e) {
                    System.err.println("IOException occurred while writing to file: " + e.getMessage());
                }

                try{
                    List<Prediction> predictions = Parser.parsePrediction(response.body());
                    System.out.println("Parsed " + predictions.size() + " predictions");
                }
                catch(JsonProcessingException e){
                    System.err.println("Parse Error: " + e.getClass().getSimpleName());
                }


            }
            else {
                System.err.println("Error " + response.statusCode());
            }
            System.out.println("Size of the response body: " + response.body().getBytes().length + " bytes");
            
        } catch (InterruptedException | IOException e) {//the client.send can return errors so we gotta catch them aswell or this will not compile java wont allow us to 
            System.err.println("IOException or InterruptedException " + e.getClass().getSimpleName() + " occurred");
        }catch (RuntimeException e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}