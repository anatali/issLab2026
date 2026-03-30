package conway26appl.protoactor0;
import java.util.Observable;
import com.fasterxml.jackson.databind.ObjectMapper;
import protoactor26.AbstractProtoactor26;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

/*
 * Cattura i messaggi inviati dal  guiserver e li gira a lifectrl
 * Invia comandi a lifectrl come 'delgato' di lifectrl
 * 
 * OutInGuiProtoactor è un Observer sulla connessione WS verso guiserver
 * MA ORA CI POSSONO ESSERE  ALTRI POSSIBILI INPUT DEVICES, che inviano comandi a lifectrl
 * come CallerLifeGame.
 * Dunque connectToServer sarebbe meglio fosse una request che dà come risposta il nome
 * di un owner, se esiste
 */
public class OutInGuiPattore extends AbstractProtoactor26 implements  IObserver{
	private Interaction connToGui ;
	
	public OutInGuiPattore(String name,   ProtoActorContextInterface ctx){
		super(name,ctx);
 	}
 
	protected void displayToGui(String msg) {
		CommUtils.outyellow(name + " | display " + connToGui);
		try {
			IApplMessage cmdmsg = CommUtils.buildDispatch("lifectrl", "eval", msg, "guiserver"  );
			//WARNING: il sender deve essere lifectrl
			if( connToGui != null  ) connToGui.forward( cmdmsg );
		} catch (Exception e) {
			CommUtils.outred("name + | displayToGui error " + e.getMessage() );
 		}
	}

//	@Override
//	public void close() {
//		CommUtils.outblue(name + " | close "  );
//	}



/*
 * IObserver sulla connToGui
 */
	@Override
	public void update(Observable o, Object arg) {
		//CommUtils.outblue(name + " | update2 "  );
		update(""+arg);
	}

	@Override
	public void update(String value) {
		if( value.startsWith("[[") ) {
			return;  //evito di visualizare la grid	
		}
		CommUtils.outblue(name + " | update "  + value);
		try {
			IApplMessage msg = new ApplMessage( value );
			CommUtils.outmagenta(name + " | msg= "  + msg);			
			//GIRO IL MSG a lifectrl
			forward(msg);
		}catch(Exception e) {
			
		}
	}

	//https://www.baeldung.com/jackson-object-mapper-tutorial
	protected String toJson(  boolean[][] gridrep ) {
		ObjectMapper mapper = new ObjectMapper();
 		try {
			String jsonArrayGridRep = mapper.writeValueAsString(gridrep);
			CommUtils.outblack( jsonArrayGridRep);
			return jsonArrayGridRep;
		} catch (Exception e) {
 			e.printStackTrace();
 			return null;
		}		
	}

	public void close() {
		if( connToGui != null  )
			try {
				CommUtils.outmagenta(name + "closeConnToGui");
				IApplMessage endmsg = CommUtils.buildDispatch("lifectrl", "endremoteclient", "end", "guiserver"  );
				connToGui.forward( endmsg );
				CommUtils.delay(1000);
				connToGui.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/*
	 * Riceve da LifeControllerProtoactor
	 */
	@Override
	protected void elabDispatch(IApplMessage m) {
		CommUtils.outcyan(name+" elabDispatch " + m.msgId());
		if( m.msgId().equals("connect")  ) {
			connectToServer();
		}
		else if( m.msgId().equals("display") ) {
             displayToGui(m.msgContent());
		}  
		else if( m.msgId().equals("closeConnToGui") ) {
            close();
		}  

	}

	//Reazione a un msg del controller
 	protected void connectToServer() {
		if( connToGui == null  )
		try {
			CommUtils.outgreen(name + " | connectToServer ..................... :");
			connToGui = WsConnection.create("localhost:8080", "eval",this);
	     	IApplMessage cmdmsg = CommUtils.buildDispatch("lifectrl", "setcontroller", "set(lifectrl,ws,'localhost:8070')", "guiserver"  );
	     	CommUtils.outblue(name + " | forward " + cmdmsg);
	     	connToGui.forward(cmdmsg);
		} catch (Exception e) {
 			e.printStackTrace();
		}		
	}

	@Override
	protected IApplMessage elabRequest(IApplMessage req) {
 		return null;
	}

	@Override
	protected void elabReply(IApplMessage req) {
		// TODO Auto-generated method stub		
	}

	@Override
	protected void elabEvent(IApplMessage ev) {
		// TODO Auto-generated method stub		
	}

	@Override
	protected void proactiveJob() {
		// TODO Auto-generated method stub		
	}

}

