package conway26appl.caller;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.CommUtilsOrig;
import unibo.basicomm23.utils.Connection;
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
 * PREMESSA: lanciare MainConwayGui
 */

public class CallerServerInteraction  implements IObserver{  
	 private String name;
	 
    public CallerServerInteraction( String name ) throws Exception {
    	this.name = name;
     	sendCellChange( );
    }
    
    protected void sendCellChange( ) throws Exception {
    	Interaction wsconn = WsConnection.create("localhost:8080", "eval", this);
    	//((Connection) wsconn).setTrace(true);
     	IApplMessage cmdmsg = CommUtils.buildDispatch(name, "eval", "cell(5,7)", "guiserver"  );
     	CommUtils.outblue(name + " sending " + cmdmsg);
     	wsconn.forward(cmdmsg);
     	
//      	IApplMessage reqmsg = CommUtils.buildRequest(name, "cellcolor", "cell(5,6)", "guiserver"  );
//       	CommUtils.outblue("CallerServerInteraction | send " + reqmsg);
//       	IApplMessage reply = wsconn.request(reqmsg);
//       	CommUtils.outblue("CallerServerInteraction | got reply= " + reply);

     	CommUtils.delay(2000);
     	CommUtils.outblue("CallerServerInteraction | BYE "  );
}     
    
    
	@Override 
	public void update(Observable o, Object arg) {
		//CommUtils.outyellow("WSConnectionUsage | riceve da observale: " + o + " la info:" + arg);		
		update(arg.toString() );
	}


	@Override
	public void update(String message) {
		CommUtilsOrig.outgreen("CallerServerInteraction | update elabora: " + message);
	}
    
    
    public static void main(String[] args) throws Exception {
    	System.out.println("CallerServerInteraction Java.version="+ System.getProperty("java.version"));
    	new CallerServerInteraction("lifectrl");
     }
}

