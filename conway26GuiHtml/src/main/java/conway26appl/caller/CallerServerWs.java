package conway26appl.caller;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.net.http.HttpClient;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
import java.net.http.WebSocket;
 
/*
 * PREMESSA: lanciare SistemaSJavalApplMsgsQueued
 *  
 * Componente che usa un WebSocketClient
 * per inviare messaggi su una WebSocket
 * e per elaborare i messaggi inviati dal server
 * Usa un CountDownLatch per terminare
 */

public class CallerServerWs  {  
	private IApplMessage reqmsg    = CommUtils.buildRequest("clientjava", "eval", "CELL", "server"  );
	private IApplMessage setctrl   = CommUtils.buildRequest("clientjava", "eval", "setcontroller", "server"  );
	// Un latch per evitare che il programma termini prima di ricevere la risposta
	protected CountDownLatch latch = new CountDownLatch(1); //Inizializzo a 1 perché aspetto UNA risposta dal server
    protected HttpClient client    = HttpClient.newHttpClient();  
    protected String name;
    
    public CallerServerWs( ) throws Exception {
    	//sendRawMessage( );
    	sendCellChange( );
    }

    protected void sendRawMessage( ) throws InterruptedException {
    	//HttpClient client = HttpClient.newHttpClient();
        
        WebSocket webSocket = client.newWebSocketBuilder()
            .buildAsync(URI.create("ws://localhost:8080/chat"), new WebSocketListener(latch))
            .join();

        // Invio di un messaggio al server
        webSocket.sendText(setctrl.toString(), true);
        
       // webSocket.sendText("ciao valeria", true);
        
//        String c56 = reqmsg.toString().replace("CELL", "cell(5,6)");
//        webSocket.sendText(c56, true);

        // Aspetta che la connessione venga chiusa o interrotta
        latch.await();
        CommUtils.outred("CallerServerWs | setup1 finito");
   }     

    
    protected void sendCellChange( ) throws InterruptedException {
    	//HttpClient client = HttpClient.newHttpClient();
        
        WebSocket webSocket = client.newWebSocketBuilder()
            .buildAsync(URI.create("ws://localhost:8080/eval"), new WebSocketListener(latch))
            .join();
        
        // Invio di un messaggio al server
        // webSocket.sendText(setctrl.toString(), true);
        
        String c56 = reqmsg.toString().replace("CELL", "cell(5,6,1)");
        CommUtils.outmagenta("CallerServerWs | send " + c56);
        webSocket.sendText(c56, true);

        // Aspetta che la connessione venga chiusa o interrotta
        latch.await();
    }     
    
    
    
    private static class WebSocketListener implements WebSocket.Listener {
        private final CountDownLatch latch;

        public WebSocketListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("CallerServerWs | --- Connessione aperta ---");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        /*
         * I metodi del listener restituiscono un CompletionStage. 
         * Questo permette di gestire messaggi molto grandi o operazioni lente in modo non bloccante. 
         * Di default, richiamare super.onText è sufficiente.
         * 
         */
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            CommUtils.outmagenta("CallerServerWs | Messaggio ricevuto dal server: " + data);
            return WebSocket.Listener.super.onText(webSocket, data, last);
            //non fa "nulla" di operativo, ma serve a gestire il flusso dei dati (backpressure).
            /*
             * super.onText(...) dice: "Esegui l'implementazione predefinita prevista dai 
             * progettisti di Java per questo metodo" che fa essenzialmente due cose:

				Richiede il messaggio successivo: Segnala al sistema che il listener è 
				pronto a ricevere altri dati.
				
				Restituisce null (o un CompletionStage già completato): 
				Indica che l'elaborazione del messaggio corrente è terminata e 
				non ci sono operazioni asincrone in sospeso.
				
				onText non restituisce un void, ma un CompletionStage<?>. 
				Questo serve per la Backpressure (gestione del carico):
				
				- Se restituisci super.onText(...): Dichiari che hai finito di leggere. 
				  Il WebSocket continuerà a inviarti messaggi non appena arrivano dal buffer di rete.

				- Se volessi fare un'operazione lenta: Potresti restituire un tuo CompletableFuture. 
				  Il WebSocket aspetterebbe che quel futuro sia completato prima di invocare 
				  nuovamente onText per il messaggio successivo. 
				  Questo evita che il server "sommerga" il tuo client di dati 
				  se il tuo codice è lento a elaborarli 
				  (ad esempio, se devi ricalcolare tutta la griglia di Conway 
				   prima di passare al turno successivo).
 
             */
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println("CallerServerWs | --- Connessione chiusa: " + reason + " ---");
            latch.countDown();
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("Errore: " + error.getMessage());
            latch.countDown();
        }
    }

    
  
    
    public static void main(String[] args) throws Exception {
    	System.out.println("Java.version="+ System.getProperty("java.version"));
    	CallerServerWs client = new CallerServerWs();
     }
}

