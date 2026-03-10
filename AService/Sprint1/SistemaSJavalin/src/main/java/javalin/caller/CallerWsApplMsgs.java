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
 * PREMESSA: lanciare SistemaSJavalinBetterApplMsgs o SistemaSJavalApplMsgsQueued
 *  
 * Componente che usa un WebSocketClient
 * per inviare messaggi su una WebSocket
 * e per elaborare i messaggi inviati dal server
 * Usa un CountDownLatch per terminare
 */

public class CallerWsApplMsgs  {  
	private IApplMessage reqmsg = CommUtils.buildRequest("clientjava", "eval", "0.0", "server"  );
	// Un latch per evitare che il programma termini prima di ricevere la risposta
    private CountDownLatch latch = new CountDownLatch(1); //Inizializzo a 1 perché aspetto UNA risposta dal server
    private WebSocketClient client;
    
    public CallerWsApplMsgs(URI serverUri) {

    	client = new WebSocketClient(serverUri) {
			@Override
			public void onOpen(ServerHandshake handshakedata) {
				System.out.println("CallerWsApplMSgs | Connessione aperta!");
			}

			@Override
			public void onMessage(String message) {
				try {
					IApplMessage msg = new ApplMessage(message);
					CommUtils.outmagenta("CallerWsApplMSgs | onMessage SLOW ricevuto: " + msg);
					if (msg.isReply()) latch.countDown();
				} catch (Exception e) {
					CommUtils.outgreen("CallerWsApplMSgs | onMessage: " + message);
				}
			}

			@Override
			public void onMessage(ByteBuffer message) {
				System.out.println("CallerWsApplMSgs | Messaggio ByteBuffer ricevuto! ");
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				System.out.println("CallerWsApplMSgs | Connessione chiusa: " + reason);
			}

			@Override
			public void onError(Exception ex) {
				System.err.println("CallerWsApplMSgs | Errore: " + ex.getMessage());
			}
    	};
        doJob();
    }
    
    public void doJob() {
        try {
            System.out.println("connect: "  );
            client.connect();
            while( ! client.isOpen() ) {
            	CommUtils.outblue("waiting connections ...");
            	CommUtils.delay(100);
            }
            client.send(reqmsg.toJsonString());           
            latch.await();
            client.close();         
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    }
  
    
    public static void main(String[] args) {
    	System.out.println("Java.version="+ System.getProperty("java.version"));
    	CallerWsApplMsgs client = new CallerWsApplMsgs(
				URI.create("ws://localhost:8080/eval"));
     }
}

