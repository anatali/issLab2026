package clients;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import unibo.basicomm23.coap.CoapConnection;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import org.eclipse.californium.core.CoapClient;
 

public class RobotsmartCoapClient implements CoapHandler{
protected String name = "coapclient";
protected boolean connected = false;
protected Interaction coapConn;
protected CoapObserveRelation relation;

protected IApplMessage dostep    = CommUtils.buildRequest(name, "step", "step(345)", "robotsmart");
protected IApplMessage turnleft  = CommUtils.buildDispatch(name, "move", "move(l)", "robotsmart");
protected IApplMessage turnright = CommUtils.buildDispatch(name, "move", "move(r)", "robotsmart");


	public void doJob() throws Exception {
		connect();
		test1();
		relation.proactiveCancel();
		CommUtils.delay(3000);
		CommUtils.outblue("ActorObserver | ENDS"   );
		System.exit(0);
	}
	protected void connect(){
	       CommUtils.outblue(name + " | connect " );
	       if( connected ) return;	
	        connected   = true;
//	        coapConn = ConnectionFactory.createClientSupport23(ProtocolType.coap,"localhost:8020", "ctxrobotsmart/robotsmart");
	        coapConn = ConnectionFactory.createClientSupport23(ProtocolType.coap,"localhost:8020", "ctxrobotsmart/robotmnemo");
	        //((Connection)commChannel).trace = true;
	        CommUtils.outblue(name + " | connect commChannel=" + coapConn);
	        addObservation(coapConn);
	 }
	
	protected void test1() throws Exception {
		coapConn.forward(turnleft);
		coapConn.forward(turnright);
        IApplMessage answer = coapConn.request(dostep);   
        CommUtils.outblue(name + " | test1 answer=" + answer );
	}
	    
	
	protected void addObservation(Interaction conn) {
		CoapConnection coapConn = (CoapConnection)conn;
		CoapClient client = coapConn.getClient();
		
	    CommUtils.outblue("callerCoap addObservation client"  );
/*
Quando si chiama client.observe(handler), succede quanto segue:

- Thread di Invio: La richiesta iniziale di "registrazione" dell'osservazione viene inviata utilizzando 
  uno dei thread del pool di rete di Californium (solitamente gestito dall'elemento Connector).

- Thread di Ricezione (Notification): Quando il server invia una notifica (un cambiamento di stato), 
  la risposta viene catturata dallo stack di rete e consegnata al tuo CoapHandler (il metodo onLoad).

- Executor Service: Californium utilizza internamente un ExecutorService per invocare i callback. 
  Questo significa che il codice dentro il tuo onLoad viene eseguito su un thread gestito dal framework, 
  non sul thread che ha originato la chiamata observe.	     
  */
		
	    relation = client.observe( this );
//				new CoapHandler() {
//					@Override public void onLoad(CoapResponse response) {
//						String content = response.getResponseText();
//						CommUtils.outgreen("ActorObserver | value=" + content );
//					}					
//					@Override public void onError() {
//						CommUtils.outred("OBSERVING FAILED  ");
//					}
//				});	
		
	}
	
	/*
	 * CoapHandler
	 */
	@Override
	public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
        CommUtils.outcyan(name + " | onLoad: " + content );
	}
	@Override
	public void onError() {
		CommUtils.outred(name + " | FAILED: " );
		
	}
	
	 public static void main(String[] args) {
	        try {
	        	new RobotsmartCoapClient().doJob();
 	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	  }
}


