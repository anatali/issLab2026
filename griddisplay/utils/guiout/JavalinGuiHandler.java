package guiout;
import java.awt.Desktop;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Vector;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import it.unibo.kactor.ActorBasic;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.mqtt.MqttInteraction;
import unibo.basicomm23.msg.ApplMessage;

public class JavalinGuiHandler {

	public WsMessageContext pageCtx;
	private String name;

 	
 	
	private Vector<WsConnectContext> allConns = new Vector<WsConnectContext>();

	//public JavalinGuiHandler( String name, int port, ActorBasic controller ) { //name="guiserver"
	public JavalinGuiHandler( String name, int port  ) {  
		this.name       = name;
//		this.controller = controller;
		
		CommUtils.outblue(this.name + "  STARTS   "  );
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
		}).start(port);
        
        
 
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
        	var inputStream = getClass().getResourceAsStream("/page/GridNoCanvas.html");     
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
     			CommUtils.outmagenta("connected ..."  );
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
  
            		if(  m.msgSender().equals("unknown") && m.msgContent().contains("ready") && pageCtx==null) { 
                      	pageCtx = ctx;  //memorizzo connessione pagina NON IMPORTA
                    	CommUtils.outmagenta(name + " |  memorizzo pageCtx:" + pageCtx);
                    	return;
                    }		                	

//                	hanleMsgFromPage(m);
             });
        });        
	}
	
	public void setUpPageGranular() {
		try {
			CommUtils.outred("OutInWs | setUpPageGranular"  );
			URI url = new URI("http://localhost:8080");
			Desktop.getDesktop().browse(url);
		} catch (Exception e) {
			CommUtils.outred("OutInWs | setUpPageWithDiv ERROR" + e.getMessage());
		}
	}
  
 	
	public void changeCellState( String x, String y, String color) {
		//CommUtils.outred("changeCellState " + pageCtx);
		if (pageCtx != null) {
			pageCtx.send("cell(VX,VY,C)".replace("VX",x).replace("VY",y).replace("C",color));
		}
	}
 

}
