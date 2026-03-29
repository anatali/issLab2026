package conway.io;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsConnectContext;
 
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;

public class IoJavalin {
	
	private static AtomicInteger pageCounter = new AtomicInteger(0);
	private WsMessageContext pageCtx, lifeCtrlCtx ;
	private String name;
	private String firstCaller        = null;
	private String controllerName     = null;
	private String controllerProtocol = null;
	private String controllerPort     = null;
	private Interaction controllerconn= null;
	
	private Vector<WsConnectContext> allConns = new Vector<WsConnectContext>();

	public IoJavalin(String name) { //name="guiserver"
		this.name = name;
        var app = Javalin.create(config -> {
        	// Configurazione globale del timeout per le connessioni (dalla versione 6.x in avanti)
            //config.http.asyncTimeout = 300000L; // 5 minuti in millisecondi
        	config.jetty.modifyWebSocketServletFactory(factory -> {
                // Imposta il timeout (ad esempio 5 minuti)
                factory.setIdleTimeout(Duration.ofMinutes(30));
            });
        	config.staticFiles.add(staticFiles -> {
				staticFiles.directory = "/page";
				staticFiles.location = Location.CLASSPATH; // Cerca dentro il JAR/Classpath
				/*
				 * i file sono "impacchettati" con il codice, non cercati sul disco rigido esterno.
				 */
		    });
		}).start(8080);
 
/*
 * --------------------------------------------
 * Parte HTTP        
 * --------------------------------------------
 */
        app.get("/", ctx -> {
    		//Path path = Path.of("./src/main/resources/page/ConwayInOutPage.html");    		    
        	/*
        	 * Java cercherà il file all'interno del Classpath 
        	 * (dentro il JAR o nelle cartelle dei sorgenti di Eclipse), 
        	 * rendendo il codice universale
         	 */
        	//var inputStream = getClass().getResourceAsStream("/page/ConwayInOutPage.html");  
        	var inputStream = getClass().getResourceAsStream("/page/LifeIInOutCanvas.html");     
        	if (inputStream != null) {
        		// Trasformiamo l'inputStream in stringa (o lo mandiamo come stream)
        	    String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        	    ctx.html(content);
        	} else {
		        ctx.status(404).result("File non trovato nel file system");
		    }
		    //ctx.result("Hello from Java!"));  //la forma più semplice di risposta
        }); 
        
        
 /*
 * --------------------------------------------
 * Parte Websocket
 * --------------------------------------------
 */      
        app.ws("/eval", ws -> {
            ws.onConnect(
		        ctx -> { 
	    	        int idAssegnato = pageCounter.incrementAndGet();
	    	        String callerName = "caller"+idAssegnato;
	     	        sendsafe(ctx, "ID:" + callerName);
	     			if( firstCaller == null ) {
	     				firstCaller = callerName;
	     				//ownerctx    = ctx;
     			}
     			CommUtils.outmagenta("connected ..." + callerName);
     			allConns.add(ctx);
     			sendToAll("Nuova connessione " + allConns.size());
     			 
        // Ogni 20 secondi invia un segnale per "svegliare" i proxy
//    	heartbeatTask = executor.scheduleAtFixedRate(
//            () -> { if(ctx.session.isOpen()) sendsafe(ctx,"PING");}, //lambda // CommUtils.outcyan("PING");
//                    20,  //QUANTO ASPETTARE LA PRIMA VOLTA. Se 0, il primo PING parte istantaneamente (inutile)
//                    20,  //OGNI QUANTO RIPETERE
//                    TimeUnit.SECONDS
//            );
		            });
             
            ws.onMessage(ctx -> {
                String message = ctx.message();     
                   	//Il server 'parla'   IApplMessage
	                IApplMessage m;
	                try {
	                	m = new ApplMessage(message);
	                }catch( Exception e) {
	                	CommUtils.outyellow(name + "receives a non ApplMessage:" + message);
	                	return;
	                }
//                    CommUtils.outcyan(name + " | receives from:" + m.msgSender() + 
//                    		" dest=" + m.msgReceiver() + " mid=" + m.msgId() +
//                    		" pageCtx=" + (pageCtx!=null) + " allConns:" +allConns.size());
  
            		if(  m.msgSender().equals("unknown") && m.msgContent().contains("canvasready") && pageCtx==null) { 
                      	pageCtx = ctx;  //memorizzo connessione pagina
                    	CommUtils.outmagenta(name + " |  memorizzo pageCtx:" + pageCtx);
                    	return;
                    }		                	
            		//if( m.msgSender().equals("lifectrl") ) {
            			if( m.msgReceiver().equals(name) && m.msgId().contains("setcontroller")) { 
            				//setcontroller(name,protocol,port)
            				Struct payload = (Struct) Term.parse(m.msgContent());
            				CommUtils.outcyan(name + " | payload=" + payload );
            				if( payload != null ) {
            					controllerName     = payload.getArg(0).toString();
            					controllerProtocol = payload.getArg(1).toString();
            					controllerPort     = payload.getArg(2).toString().replace("'", "");
            				}
            	        	CommUtils.outcyan(name + " | controllerPort=" + controllerPort );
            	        	if( controllerPort.equals("0")) {
	           		       	    lifeCtrlCtx = ctx; //memorizzo connessione controller
	            	        	CommUtils.outmagenta(name + " |  memorizzo lifeCtrlCtx:" + lifeCtrlCtx );
            	        	}else {
            	        		CommUtils.outmagenta("todo: set connection to " + controllerName);
            	        		if( controllerProtocol.equals("ws")) {
            	        			controllerconn =  WsConnection.create( controllerPort, "eval",null);
            	        		}else {
            	        			CommUtils.outmagenta("sorry, only ws supported  "  );
            	        		}
            	        	}
            	        	return;
            			}
            	    if( m.msgSender().equals(controllerName) ) hanleMsgFromAppl(m);
                	//}
                	else if( m.msgSender().equals(firstCaller)  ) hanleMsgFromPage(m);
             });
        });        
	}
	
	
	protected void hanleMsgFromPage(IApplMessage m) {
		if( m.msgContent().contains("cell(") ) {
     		CommUtils.outmagenta("send cell cmd to controller " + m);
    		//if( lifeCtrlCtx != null ) sendsafe( lifeCtrlCtx, m.toString() );
     		sendMsgToController(m);
		}else if( m.msgReceiver().equals("lifectrl") ) {
			//Giro il messaggio al livello applicativo
    		//if( lifeCtrlCtx != null )  sendsafe( lifeCtrlCtx, m.toString() );		
			sendMsgToController(m);
		}
	}
	
	protected void sendMsgToController( IApplMessage msg ) {
		if( lifeCtrlCtx != null ) sendsafe( lifeCtrlCtx,msg.toString() );
		else 
		if( controllerconn != null ) {
 			try {
				controllerconn.forward( msg );
			} catch (Exception e) {
				CommUtils.outred("sendMsgToController ERROR: " + e.getMessage());
			}
		}else {
			CommUtils.outred("ERROR: non connection to the controller");
		}
	}
	
	protected void hanleMsgFromAppl (IApplMessage m ) {
       	if( m.msgReceiver().equals(name) && m.msgContent().startsWith("[[")) { //canvas rep
            //CommUtils.outcyan(name + " | receives [[" + " from " + m.msgSender() + " to " + m.msgReceiver());
//            pageCtx.send( m.msgContent()); 
            sendToAll(  m.msgContent() );   //così aggiorno tutte le pagine e gli observer
    		return;
    	}
    	if( m.msgReceiver().equals(name) && m.msgContent().contains("cell(")) {    
       	 //Il controller remoto ha detto di modificare il colore di una cella
	       	if (pageCtx != null) {
	           	//Ci sono 3 arg - es. cell(5,6,1)
	       		pageCtx.send( m.msgContent()); 
	        }
    	}
		
	}
	
    protected void sendsafe(WsContext ctx, String msg) {
    	synchronized (ctx.session) {  
            if (ctx.session.isOpen()) {
                ctx.send(msg);
            }
        }
    }
    
    protected void sendToAll(String m) {
    	allConns.forEach( (conn) ->	conn.send( m ) );
    }
	

	
	public static void main(String[] args) {
		var resource = IoJavalin.class.getResource("/pages");
		CommUtils.outgreen("DEBUG: La cartella /page si trova in: " + resource);
		new IoJavalin("guiserver");
	}

}
