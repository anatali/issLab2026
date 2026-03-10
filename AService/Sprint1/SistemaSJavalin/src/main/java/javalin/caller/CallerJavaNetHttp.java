package main.java.javalin.caller;
/*
 * Questa versione non richiede 
 * import org.java_websocket.client.WebSocketClient;
 * import org.java_websocket.handshake.ServerHandshake;
 * 
 * Usa java.net.http.WebSocket è strettamente un Client. 
 * pensata per connettersi a server esistenti
 * E' nativa di Java 11 e progettata per essere non bloccante e moderna:
 * 
 * Lato server Javalin gestisce già i WebSocket internamente (usando Jetty).
 *  Non c'è bisogno di org.java_websocket sul server; 
 *  si usano le API di Javalin (app.ws("/path", ws -> { ... })).
 * 
 */
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class CallerJavaNetHttp {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String WS_URL = "ws://localhost:8080/eval";
    private static final HttpClient client = HttpClient.newHttpClient();



    /** Listener per gestire i messaggi in arrivo dal Server */
    private static class WebSocketListener implements WebSocket.Listener {
        private final CountDownLatch latch;

        public WebSocketListener(CountDownLatch latch) { this.latch = latch; }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("WebSocket connesso!");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("Messaggio ASINCRONO dal server: " + data);
            // Esempio: se riceviamo "fine", chiudiamo il programma
            if(data.toString().contains("fine")) latch.countDown();
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            error.printStackTrace();
        }
    }

    private static void callGetEval(double n) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/eval/" + n))
                .GET().build();
        System.out.println("--- callGetEval --- " + request);
        HttpResponse<String> answer = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("--- answer --- " + answer.body());
    }


    public static void main(String[] args) throws Exception {
        // 1. Apriamo il WebSocket in modo asincrono
        CountDownLatch latch = new CountDownLatch(1); // Per non far chiudere il main subito
        
        System.out.println("--- Apertura WebSocket ---");
        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), new WebSocketListener(latch))
                .join();

        // 2. Facciamo una chiamata GET classica
//        callGetEval(0.0);

        // 3. Inviamo un messaggio tramite WebSocket
        CompletableFuture<WebSocket> r = webSocket.sendText("4.0", true);
        System.out.println("--- r --- " + r);
/*       
        webSocket.sendText("Richiesta aggiornamento dal Client", true).join();
        //join() blocca il thread corrente finché l'invio non è terminato.
        
        webSocket.sendText("Dati valutazione", true)
        .thenAccept(ws -> System.out.println("Invio completato su: " + ws.toString()))
        .exceptionally(ex -> {
            System.err.println("Errore nell'invio: " + ex.getMessage());
            return null;
        });
        
        //Il fatto che la CompletableFuture contenga l'oggetto WebSocket le permette di 
        //inviare messaggi in sequenza in modo ordinato (pipelining):
        
        webSocket.sendText("Parte 1 ", false) // false = il messaggio non è finito
        .thenCompose(ws -> ws.sendText("Parte 2 ", false))
        .thenCompose(ws -> ws.sendText("Fine.", true))
        .thenRun(() -> System.out.println("Intera sequenza inviata!"));
*/        
         

        // Aspettiamo un po' per vedere i messaggi asincroni dal server
        latch.await(); 
    }




}