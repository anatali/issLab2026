package protoactor26;
import java.time.Duration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import io.javalin.Javalin;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;

public class ProtoActorContext26 implements ProtoActorContextInterface{
	private String name;
	private int port;
	private  Javalin server                    = null;
	private  Vector<WsConnectContext> allConns = new Vector<WsConnectContext>();
	private Map<String, AbstractProtoactor26> protoactors = new ConcurrentHashMap<>();

	public ProtoActorContext26(String name, int port) {
		this.name = name;
		this.port = port;
		configureTheSystem();		
	}
	
	@Override
	public void register( AbstractProtoactor26 pactor) {
		protoactors.put(pactor.name, pactor );
		CommUtils.outgreen("registered " + pactor.name + " in " + name );
	}

	/*
	 * ----------------------------------------------------
	 * CNFIGURAZIONE DEL SERVER
	 * ----------------------------------------------------
	 */ 
	    protected void configureTheSystem() {  
	    	setUpServer(   );
 		    setWorkWS( );   
 	    }
	    
	    public Javalin getServer() {
	    	return server;
	    }

		protected void setUpServer(   ) {
 			if( server == null ) server = Javalin.create(config -> {
		        // In Javalin 6 si usa modifyWebSocketServletFactory 
		        config.jetty.modifyWebSocketServletFactory(factory -> {
		            factory.setIdleTimeout(Duration.ofMinutes(30)); 
		        });
		   }).start(port);
		}

        protected void setWorkWS( ) {
  
          server.ws("/eval", 
            ws -> {  //ws di tipo `io.javalin.websocket.WsConfig`		            
            
            ws.onConnect( ctx -> {  //ctx di tipo `io.javalin.websocket.WsConnectContext`
             			allConns.add(ctx);
             			CommUtils.outgreen(name + " | ws: connection   " + "" + " Nconn=" + allConns.size()); 	             			 
                // Ogni 20 secondi invia un segnale per "svegliare" i proxy
//            	heartbeatTask = executor.scheduleAtFixedRate(
//                    () -> { if(ctx.session.isOpen()) sendsafe(ctx,"PING");}, //lambda // CommUtils.outcyan("PING");
//                            20,  //QUANTO ASPETTARE LA PRIMA VOLTA. Se 0, il primo PING parte istantaneamente (inutile)
//                            20,  //OGNI QUANTO RIPETERE
//                            TimeUnit.SECONDS
//                    );
            });//ws.onConnect
            

            
            ws.onMessage(ctx -> { //ctx di tipo `io.javalin.websocket.WsMessageContext`
            	IApplMessage am = readInputWS( ctx.message() );
//            	CommUtils.outyellow("			---- ProtoActorContext26 onMessage " + am);

            	IApplMessage answer = elabMsg( am ); //elabMsg2( am,ctx );
//              CommUtils.outyellow("			---- ProtoActorContext26 reply " + answer);
               	if( am.isRequest() && answer != null ) ctx.send(answer.toString());
            });
            
            ws.onClose(ctx -> { //ctx di tipo `io.javalin.websocket.WsCloseContext`
            	   //emitInfo(name + " | ws: connection closed:" + allConns.size() );
//            	System.out.println("Sorgente chiusura: " + (ctx.status() == 1006 ? "Anomala/Timeout" : "Volontaria"));
//                System.out.println("Codice Status: " + ctx.status());
//                System.out.println("Motivo: " + ctx.reason());
                CommUtils.outmagenta(name + " | ws: connection closed from " + ctx.host() ); 
              });
            });
         }

        /*
         * Individua il protoactor destinatario e gli fa accodare 
         * il task appropriato di elaborazione-messaggio 
         */
        @Override 
        public IApplMessage elabMsg( IApplMessage am ) {
        	CommUtils.outyellow(name + " elabMsg : " + am.msgId() + " from " + am.msgSender() + " to " + am.msgReceiver()   ); 
        	String dest = am.msgReceiver();
    		AbstractProtoactor26 pactor=protoactors.get(am.msgReceiver());     		
    		if( pactor != null ) {
    			IApplMessage answer = pactor.execMsg( am );
    			return answer;
    		}
    		else{ //ADDED MARCH 19
    			//dest non è un pactor locale => assumo sia remoto su una delle conn correnti
    			allConns.forEach( conn -> {
    				CommUtils.outyellow("invio a dest remoto:" + am);
    				sendsafe(conn, am.toString()); 
    			});
    			return am;   	
    		}
        }
        
		        
		        protected IApplMessage readInputWS(String message) throws Exception{
		        	CommUtils.outyellow(	"readInputWS message=" + message  );
		        	try {
		    			IApplMessage m = new ApplMessage(message);  
		    			return m;
		    		} catch (Exception e) {
		    			try {
		    				//CommUtils.outred("readInputWS retrying after error e=" + e.getMessage());
		    				IApplMessage m = ApplMessage.cvtJson(message);
		    				return m;
		    			} catch (Exception e2) {
		    				//CommUtils.outred("readInputWS 2nd ERROR e2=" + e2.getMessage());
		    				//CommUtils.outred("readInputWS ERROR for message=" + message + " e=" + e.getMessage());
		    				IApplMessage ev = CommUtils.buildEvent("context","input",message );
		    				return ev;
		    			}
		     		}
		        }
		     
/* Utility */
		        @Override
		        public void emitInfo(IApplMessage event) {
		        	//CommUtils.outcyan("			emitInfo " + s);
 		        	//Invio a tutti i componenti esterni
		        	allConns.forEach( (conn) -> {
		        		if( conn.session.isOpen() ) sendsafe(conn, event.toString()); 
		        	});
		        	
		        	//Invio a tutti gli attori locali al contesto
	        		protoactors.forEach((id, pa) -> {
	        		    pa.execMsg( event );  
	        		});
	        	}

		        protected void sendsafe(WsContext ctx, String msg) {
		        	synchronized (ctx.session) { 
		                if (ctx.session.isOpen()) {
		                    ctx.send(msg);
		                }
		            }
		        }
}
