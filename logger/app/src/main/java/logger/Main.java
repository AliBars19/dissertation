package logger;
import java.net.*;
import java.net.http.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        
        String key = System.getenv("TFL_UNIFIED_API_KEY_PRIMARY");
        
        if (key == null || key.isEmpty()) {//guard to safely pull api key from env
            System.err.println("Error: TFL_UNIFIED_API_KEY_PRIMARY has failed");
            key = System.getenv("TFL_UNIFIED_API_KEY_SECONDARY");
            if (key == null || key.isEmpty()) {
                System.err.println("Error: TFL_UNIFIED_API_KEY_SECONDARY has failed");
                System.exit(1);
            }
            
        }

        HttpClient client = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(10)).build();//testing a fetch, victoria line seems most reliable so its the one that we are going to be testing i beliebve this was in the decisions log aready at some point
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(java.time.Duration.ofSeconds(10))
                .uri(URI.create("https://api.tfl.gov.uk/Line/victoria/Arrivals?app_key=" + key))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                System.out.println("200 OK");
            }
            else {
                System.err.println("Error " + response.statusCode());
            }
            System.out.println("Size of the response body: " + response.body().getBytes().length + " bytes");

        } catch (InterruptedException | IOException e) {//the client.send can return errors so we gotta catch them aswell or this will not compile java wont allow us to 
            System.err.println("IOException or InterruptedException " + e.getClass().getSimpleName() + " occurred");
        }
    }
}