package sistemaSCallers;

import java.util.Observable;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

public class CallerToProtoactor implements IObserver{
	private Interaction conn ;
	private String name;
	private String destName = "sistemaS";
 	
	public CallerToProtoactor(String name ) {
		this.name = name;
		connectToSistemaS();
		doJob();
	}

	protected void connectToSistemaS() {
		if( conn == null  )
		try {
			CommUtils.outgreen(name + " | connectToSiatemaS .....");
			conn = WsConnection.create("localhost:8050", "eval",this);
//			((WsConnection)conn).setTrace(true);
 		} catch (Exception e) {
 			e.printStackTrace();
		}		
	}

	protected void doJob() {		
		try {
			//REMEMBER: sistemaS works elab messages in FIFO order
			sendDispatch();
			sendRequestSynch("2.0");  //expected near -0.03
			sendRequesAsynch("5.0");  //expected -1.68
			sendRequestSynch("0.0");  //expected 1
			
			CommUtils.delay(5000); //La req lenta dura 4sec
			
			CommUtils.outgreen(name + " | ENDS");
			
		} catch (Exception e) {
 			e.printStackTrace();
		}
	}
	
	
	protected void sendDispatch() throws Exception {
		IApplMessage cmd     = CommUtils.buildDispatch(name, "do",  "2.0", destName);
		CommUtils.outcyan(name + " | forward=" + cmd);
		conn.forward(cmd);
	}
	
	protected void sendRequestSynch(String v) throws Exception {
		IApplMessage req   = CommUtils.buildRequest(name, "eval", v, destName);
		CommUtils.outblue(name + " | requestSynch=" + req);
		IApplMessage reply = conn.request(req);  //bloccante sincrona long
		CommUtils.outblue(name + " | reply=" + reply);
	}

	protected void sendRequesAsynch(String v) throws Exception {
		IApplMessage req   = CommUtils.buildRequest(name, "eval", v, destName);
		CommUtils.outmagenta(name + " | requestAynch=" + req);
		conn.forward(req);
	}

	
	
/*
 * -----------------------------------------
 * IObserver
 * -----------------------------------------
 */
  


	@Override
	public void update(Observable o, Object arg) {
		//CommUtils.outblack(name + " | update 2 arg ");		
		update(""+arg);
	}

	@Override
	public void update(String value) {
		CommUtils.outyellow(name + " | update:" + value);
//		try {
//			IApplMessage m = new ApplMessage(value);
//			if( m.isReply() ) CommUtils.outmagenta( "      " + m.toString() );
//		}catch(Exception e) {
//			CommUtils.outyellow(""+value);
//		}
	}

	
    public static void main(String[] args) {
    	new CallerToProtoactor("acalller");
     }

}
