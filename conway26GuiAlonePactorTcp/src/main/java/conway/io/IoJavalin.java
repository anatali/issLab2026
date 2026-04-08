package conway.io;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import conwaygui.GuiServer;
import conwaygui.MainGuiServer;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsConnectContext;
 
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.mqtt.MqttInteraction;
import unibo.basicomm23.msg.ApplMessage;

public class IoJavalin {
	/*
	 * Interazione con la pagina HTML via WS
	 * Interazione con il lifegame via MQTT
	 */
	private static AtomicInteger pageCounter = new AtomicInteger(0);
	public WsMessageContext pageCtx;
	private WsMessageContext lifeCtrlCtx ;
	private String name;
	private String firstCaller        = null;
	private String controllerName     = "lifegame";
	private String controllerProtocol = null;
	private String controllerUrl      = null;
	
	private GuiServer controller;
//	private Interaction controllerconn= null;
	
	private Vector<WsConnectContext> allConns = new Vector<WsConnectContext>();

	public IoJavalin(String name, GuiServer controller) { //name="guiserver"
		this.name       = name;
		this.controller = controller;
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
     			//sendToAll("Nuova connessione " + allConns.size());
     			 
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
//                    CommUtils.outcyan(name + " | receives from:" + m.msgSender() + " mid=" + m.msgId() + " dest=" + m.msgReceiver());
//                    		" pageCtx=" + (pageCtx!=null) + " allConns:" +allConns.size());
  
            		if(  m.msgSender().equals("unknown") && m.msgContent().contains("canvasready") && pageCtx==null) { 
                      	pageCtx = ctx;  //memorizzo connessione pagina
                    	CommUtils.outmagenta(name + " |  memorizzo pageCtx:" + pageCtx);
                    	return;
                    }		                	

            	    if( m.msgSender().equals(controllerName) )   hanleMsgFromAppl(m,ctx);
                	else if( m.msgSender().equals(firstCaller) ) hanleMsgFromPage(m);
             });
        });        
	}
 
	protected void hanleMsgFromPage(IApplMessage m) {
		if( MainGuiServer.workingForPolling) controller.answerToReadPolling( m );
		else controller.answerToReadEvent( m );
	}
	
	/*
	 * La gui emette un dispatch. Per payload cell(x,y) si può traformare in request
	 */
 	
	protected void hanleMsgFromAppl (IApplMessage m, WsMessageContext ctx) {
       	if( m.msgReceiver().equals(name) && m.msgContent().startsWith("[[")) { //canvas rep
            //CommUtils.outcyan(name + " | receives [[" + " from " + m.msgSender() + " to " + m.msgReceiver());
            sendToAll(  m.msgContent() );   //così aggiorno tutte le pagine e gli observer
    		return;
    	}
    	if( m.msgReceiver().equals(name) && m.msgContent().contains("cell(")) {    
       	 //Il controller remoto ha detto di modificare il colore di una cella
	       	if (pageCtx != null) {
	           	//Ci sono 3 arg - es. cell(5,6,1)
	       		pageCtx.send( m.msgContent()); 
	        }
	       	return;
    	}
    	if( m.msgReceiver().equals(name) && m.msgId().contains("endremoteclient")) { 
    		CommUtils.outmagenta(name + " | receives endremoteclient. Removing a ctx");
    		allConns.remove(ctx);
    	}
	}
	
    protected void sendsafe(WsContext ctx, String msg) {
    	synchronized (ctx.session) {  
            if (ctx.session.isOpen()) {
                ctx.send(msg);
            }
        }
    }
    
    public void sendToAll(String m) {
    	//CommUtils.outmagenta(name + " | sendToAll " + allConns.size());
    	allConns.forEach( (conn) ->	conn.send( m ) );
    }
	

	

}
