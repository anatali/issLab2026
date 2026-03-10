package main.java.javalin.caller;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
 
/*
 * PREMESSA: lanciare SistemaSJavalApplMsgsQueued
 *  
 * Componente che usa un WebSocketClient
 * per inviare messaggi su una WebSocket
 * e per elaborare i messaggi inviati dal server
 * Usa un CountDownLatch per terminare
 */

public class CallerWsReqFast  {  
	//private IApplMessage reqmsg = CommUtils.buildRequest("clientjava", "eval", "4.0", "server"  );
	// Un latch per evitare che il programma termini prima di ricevere la risposta
	protected CountDownLatch latch = new CountDownLatch(1); //Inizializzo a 1 perché aspetto UNA risposta dal server
    protected WebSocketClient client;
    protected String name;
    
    public CallerWsReqFast(String name, URI serverUri) {
    	this.name = name;
    	setUp(serverUri);
        doJob();    	
    }
    
    protected void setUp(URI serverUri) {
    	client = new WebSocketClient(serverUri) {
			@Override
			public void onOpen(ServerHandshake handshakedata) {
				System.out.println("Caller | " + name + " Connessione aperta!");
			}

			@Override
			public void onMessage(String message) {
				try {					
					CommUtils.outmagenta("Caller  | " + name + " onMessage ricevuto: " + message);
					if( ! message.contains("welcome")) latch.countDown();
				} catch (Exception e) {
					CommUtils.outred("Caller  | " + name + " ERROR: " + message);
				}
			}

			@Override
			public void onMessage(ByteBuffer message) {
				System.out.println("Caller  | " + name + " Messaggio ByteBuffer ricevuto! ");
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				System.out.println("Caller  | " + name + " Connessione chiusa: " + reason);
			}

			@Override
			public void onError(Exception ex) {
				System.err.println("Caller  | " + name + " Errore: " + ex.getMessage());
			}
    	};
     }
    
    protected void sendToServer() {
    	client.send("0.0");
    }
    
    public void doJob() {
        try {
            System.out.println("connect: "  );
            client.connect();
            while( ! client.isOpen() ) {
            	CommUtils.outblue("waiting connections ...");
            	CommUtils.delay(500);
            }
            sendToServer( );          
            latch.await();
            client.close();        
            //System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }   	
    }
  
    
    public static void main(String[] args) {
    	System.out.println("Java.version="+ System.getProperty("java.version"));
    	CallerWsReqFast client = new CallerWsReqFast("callefast",
				URI.create("ws://localhost:8080/eval"));
     }
}

