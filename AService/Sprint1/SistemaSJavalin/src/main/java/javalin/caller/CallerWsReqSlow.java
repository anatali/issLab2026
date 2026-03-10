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
  */

public class CallerWsReqSlow  extends CallerWsReqFast{  
   
    public CallerWsReqSlow(String name,URI serverUri) {
    	super( name,serverUri );
    }

    protected void sendToServer() {
    	client.send("5.0");
    }
    
    public static void main(String[] args) {
    	System.out.println("Java.version="+ System.getProperty("java.version"));
    	CallerWsReqSlow client = new CallerWsReqSlow("calleslow",
				URI.create("ws://localhost:8080/eval"));
     }
}

