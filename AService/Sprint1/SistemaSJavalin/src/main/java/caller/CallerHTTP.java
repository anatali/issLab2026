package main.java.caller;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class CallerHTTP {

	private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newHttpClient();


    /** Esegue una chiamata GET con il numero nell'URL */
    private static void callGetEval(double n) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/eval?x=" + n))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode());
        System.out.println("GET Response: " + response.body());
    }

    /** Esegue una chiamata POST inviando un JSON */
    private static void callPostEvaluate(double n) throws Exception {
        // Creazione di un semplice JSON manuale per evitare dipendenze esterne (es. Jackson)
    	String ns = ""+n;
        String jsonBody = "{\"x\":  N}".replace("N",ns);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/evaluate"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode());
        System.out.println("POST Response: " + response.body());
    }

    public static void main(String[] args) throws Exception {
        double value = 0.0;

//        System.out.println("--- GET /eval ---");
//        callGetEval(value);

        System.out.println("\n---  POST /evaluate ---");
        callPostEvaluate(value);
    }

}
