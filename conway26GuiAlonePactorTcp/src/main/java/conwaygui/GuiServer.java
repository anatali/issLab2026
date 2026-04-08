package conwaygui;

import protoactor26.AbstractProtoactor26;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import conway.io.IoJavalin;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.mqtt.MqttSupport;
import unibo.basicomm23.utils.CommUtils;

public class GuiServer extends AbstractProtoactor26 {
	private IoJavalin jvlnserver;
 	protected ScheduledExecutorService readexecutor = Executors.newSingleThreadScheduledExecutor();
	protected CountDownLatch latchInput      = new CountDownLatch(1);
 	protected String MqttBroker              = "tcp://localhost:1883"; //"tcp://broker.hivemq.com"; 
 	protected MqttSupport  mqttsupport       = new MqttSupport( ); 
 	
 	/*
 	 * Una connessione è un singolo "tubo"  tra P e S
 	 * TCP garantisce che i byte arrivino nell'ordine giusto, ma non separa i messaggi per te. 
 	 * Se P scrive velocemente, il server S potrebbe leggere R1R2 come un unico blocco di testo 
 	 * indistinguibile.
 	 * Devi usare un Delimitatore (es. ogni messaggio finisce con \n) o 
 	 * un Header di Lunghezza (es. i primi 4 byte dicono quanto è lungo il messaggio).
 	 */
	public GuiServer(String name, ProtoActorContextInterface ctx) {
		super(name, ctx);
		jvlnserver = new IoJavalin("javaliniserver", this);
		
		mqttsupport.connectToBroker(name,MqttBroker);
		mqttsupport.cleartopic("lifeGameIn");
	}

	/*
	 * Metodi di elaborazione messaggi in arrivo al server da parte di ....
	 */
	@Override
	protected void elabDispatch(IApplMessage m) {
		CommUtils.outblue(name + " | elabDispatch:" + m.msgId());
		// Eseguo quanto faceva prima iojavalin
		hanleMsgFromAppl(m);
	}
	

	@Override
	protected IApplMessage elabRequest(IApplMessage req) {
		CommUtils.outyellow(name + " | elabRequest:" + req);		 
		if (req.msgId().equals("readGuiCmd")) { // eseguo in modo asincrono
			IApplMessage reply = CommUtils.buildReply(name, req.msgId(), lastCmdIn, req.msgSender());
			lastCmdIn = "nocmd";
			CommUtils.outyellow( name + " | elabRequest reply:" + reply );
			return reply;
		}
		return null;
	}

	@Override
	protected void elabReply(IApplMessage m) {
		CommUtils.outblue(name + " | elabReply:" + m);
	}

	@Override
	protected void elabEvent(IApplMessage ev) {
		CommUtils.outblue(name + " | elabEvent:" + ev);

	}

	@Override
	protected void proactiveJob() {
		// TODO Auto-generated method stub

	}

	/*
	 * GESTIONE
	 */
	
	private String lastCmdIn = "nocmd";
	
	// Called by jvlnserver
	public void answerToReadPolling(IApplMessage m) {
		CommUtils.outcyan(name + " | answerToReadPolling from jvlnserver: " + m);
		lastCmdIn = m.msgContent();
	}
	/*
	 * Called by jvlnserver. Perceived by LifeGamePactorCmdEvent
	 */
	public void answerToReadEvent(IApplMessage m) {
		CommUtils.outmagenta(name + " | answerToReadEvent from jvlnserver publish: " + m );
		mqttsupport.publish(  "lifegameIn",m.toString(),1,false );  //last arg: retained
	}
	


	protected void hanleMsgFromAppl(IApplMessage m) {
		CommUtils.outyellow(name + " | hanleMsgFromAppl " + m.msgId() );
		if (m.msgReceiver().equals(name) && m.msgContent().startsWith("[[")) { // canvas rep
//			CommUtils.outcyan(name + " | receives [[" + " from " + m.msgSender() + " to "
//			+ m.msgReceiver());
			jvlnserver.sendToAll(m.msgContent()); // così aggiorno tutte le pagine e gli observer
			return;
		}
		if (m.msgReceiver().equals(name) && m.msgContent().contains("cell(")) {
			// Il controller remoto ha detto di modificare il colore di una cella
			if (jvlnserver.pageCtx != null) {
				// Ci sono 3 arg - es. cell(5,6,1)
				jvlnserver.pageCtx.send(m.msgContent());
			}
			return;
		}
//    	if( m.msgReceiver().equals(name) && m.msgId().contains("endremoteclient")) { 
//    		CommUtils.outmagenta(name + " | receives endremoteclient. Removing a ctx");
//    		allConns.remove(ctx);
//    	}
	}

}