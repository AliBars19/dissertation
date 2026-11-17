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
import java.sql.SQLException;

public class Main {

    private static final DateTimeFormatter folderFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(java.time.ZoneOffset.UTC);//so for these two var's we get the current times because our design is going to be that raw data is split on an hourly basis with one folder containng an hours worth of raw data right, so this sets that up
    private static final DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH'Z'").withZone(java.time.ZoneOffset.UTC);
    private static final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("HH:mm:ss'Z'").withZone(java.time.ZoneOffset.UTC);

    private static int consecutiveFailures = 0;

    public static void main(String[] args) throws SQLException, IOException {
        
        String key = System.getenv("TFL_UNIFIED_API_KEY_PRIMARY");
        String dataDir = System.getenv("LOGGER_DATA_DIR");
        String dbFilePath = System.getenv("DB_FILE_PATH");
        String lines = System.getenv("LOGGER_LINES");

        if (lines == null){
            System.err.println("Error: LoggerLines is NULL");
            System.exit(1);}
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
        Database db = new Database(dbFilePath);
        if (db == null){
            System.err.println("Error: DB is NULL");
            System.exit(1);}

        
        
        HttpClient client = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(10)).build();//testing a fetch, victoria line seems most reliable so its the one that we are going to be testing i beliebve this was in the decisions log aready at some point
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(java.time.Duration.ofSeconds(10))
                .uri(URI.create("https://api.tfl.gov.uk/Line/"+ lines +"/Arrivals?app_key=" + key))
                .build();
        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();//scheduler that we are gna be using to poll the func logandsave
        scheduler.scheduleAtFixedRate(() -> logAndSave(client, request, dataDir, db, lines), 0, 30, TimeUnit.SECONDS);//polling it eevry 30 seconds
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log("Shutting Down");
            scheduler.shutdown();
            try{
                scheduler.awaitTermination(15, TimeUnit.SECONDS);
                db.close();
            }
            catch(InterruptedException | SQLException e){
                logError("Shutdown problem: " + e.getClass().getSimpleName());
            }
            log("Stopped cleanly");
        }));

    }

    public static void logAndSave(HttpClient client, HttpRequest request, String dataDir, Database db, String lines){
        Instant sentTime = Instant.now();
        
        try {

            Path filePath = rawFilePath(dataDir, sentTime);

            Integer recordCount = null;
            String tflNewest = null;

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Instant receivedAt = Instant.now();

            if(response.statusCode() >= 200 && response.statusCode() < 300) {

                if(!isUsableBody(response.body())){
                    recordFailure(db, sentTime, receivedAt, response.statusCode(), "Bad body", lines);
                    return;
                }

                int bytes = response.body().getBytes().length;
                List<Prediction> predictions = null;

                try{
                    
                    predictions = Parser.parsePrediction(response.body());
                    recordCount = predictions.size();
                    tflNewest = predictions.isEmpty() ? null : predictions.get(0).timestamp();
                }
                catch(JsonProcessingException e){
                    logError("Parse Error: " + e.getClass().getSimpleName());
                }

                int requestId = db.insertRequest(sentTime.toString(), receivedAt.toString(), response.statusCode(), bytes, recordCount, tflNewest, null, filePath.toString(), lines);

                writeRawLine(filePath, requestId, sentTime, response.body());

                if(predictions !=null){
                    db.insertMultiplePredictions(requestId, predictions);
                }
                log("#" + requestId + " saved " + recordCount + " predictions (" + bytes/1024 + "KB)");

                if(consecutiveFailures > 10){
                    log("Logger failed " + consecutiveFailures + " times, it is now back to 0");
                    
                }
                consecutiveFailures = 0;
            }
            else {
                logError("HTTP " + response.statusCode());
                recordFailure(db, sentTime,receivedAt,response.statusCode(),"HTTP " + response.statusCode(),lines);            
            }
        } 
        catch (InterruptedException | IOException | SQLException e) {//the client.send can return errors so we gotta catch them aswell or this will not compile java wont allow us to 
            logError("Request Failed " + e.getClass().getSimpleName() + " occurred");
            recordFailure(db, sentTime,null,null, e.getClass().getSimpleName(),lines);
            }
        catch (RuntimeException e) {
            logError("An unexpected error occurred: " + e.getClass().getSimpleName());
            recordFailure(db, sentTime,null,null, e.getClass().getSimpleName(),lines);        
        }
    }

    public static void recordFailure(Database db, Instant sentTime, Instant receivedAt, Integer httpStatus,String error, String lines ){
        consecutiveFailures++;
        if(consecutiveFailures > 10){
           logError("WARNING " + consecutiveFailures + " FAILURES IN A ROW ");
        }
        try{
            db.insertRequest(sentTime.toString(), receivedAt ==null ? null : receivedAt.toString(), httpStatus, null, null, null, error, null, lines);//building a failed fetch
        }
        catch (SQLException e){
            logError("Couldnt record failure: " + e.getClass().getSimpleName());
        }
    }

    private static boolean isUsableBody(String body){
        if(body.isEmpty() || body.charAt(0) != '[' || body.contains("\n")) {//this is to check that the response is healthy and so that we can parse it 
            logError("Error: Bad body");
            return false;
        }
        return true; 
    }

    public static Path rawFilePath(String dataDir, Instant sentTime) throws IOException{
        String folderName = folderFormatter.format(sentTime);
        String fileName = fileFormatter.format(sentTime) + ".jsonl.gz";//jsonl.gz because file will be zipped for compression and jsonl because each line is a seperate json object that will be parsed later on.
        Path filePath = Paths.get(dataDir, "raw", folderName, fileName);//this is the path to the folder that we are going to be creating, it will be in the data dir, then raw, then the folder name which is the date, then the file name which is the date and hour ezpz
        Files.createDirectories(filePath.getParent());
        return filePath;
    }

    public static void writeRawLine(Path filePath, int requestId, Instant sentTime, String body){
        String line = //this is one line of our response of what were gna save
                    "{\"request_id\":" + requestId + ",\"sent_at\":\"" +
                    sentTime.toString() +
                    "\",\"response_body\":" +//this is building each line of the string one by one this is js a test for now
                    body +
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
        }
        catch (IOException e) {
            logError("Error while writing to file: " + e.getClass().getSimpleName());
        }
    }

    //these two helper functions are going to be so logs are a litle more readable and clean up some code in the body of logandsave and in main aswell.

    private static void log(String message){
        System.out.println("[" + logFormatter.format(Instant.now())+"] " + message);
    }

    private static void logError(String message){
        System.err.println("[" + logFormatter.format(Instant.now())+"] " + message);
    }
}