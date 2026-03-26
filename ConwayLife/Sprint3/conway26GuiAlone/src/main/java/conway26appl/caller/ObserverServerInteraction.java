package conway26appl.caller;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.CommUtilsOrig;
import unibo.basicomm23.ws.WsConnection;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

 

import java.net.http.HttpClient;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
import java.net.http.WebSocket;
 
/*
 * PREMESSA: lanciare MainConwayGui e LifeGameInteraction
 *  
 */

public class ObserverServerInteraction  implements IObserver{  
     
    public ObserverServerInteraction( ) throws Exception {
    	CommUtilsOrig.outblue("ObserverServerInteraction | STARTS Observing" );
    	Interaction wsconn = WsConnection.create("localhost:8080", "eval", this);
    	CommUtils.delay(60000);
     }
    
     
    
	@Override 
	public void update(Observable o, Object arg) {
		//CommUtils.outyellow("WSConnectionUsage | riceve da observale: " + o + " la info:" + arg);		
		update(arg.toString() );
	}


	@Override
	public void update(String message) {
		if( ! message.contains("[[")) 
		CommUtilsOrig.outgreen("ObserverServerInteraction | update elabora: " + message);
	}
    
    
    public static void main(String[] args) throws Exception {
    	System.out.println("ObserverServerInteraction Java.version="+ System.getProperty("java.version"));
    	new ObserverServerInteraction();
    	CommUtilsOrig.outblue("ObserverServerInteraction | ENDS " );
     }
}

