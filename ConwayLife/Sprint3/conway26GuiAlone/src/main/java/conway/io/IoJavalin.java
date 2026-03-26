package conway.io;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
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
	//private WsConnectContext ownerctx = null;
	protected Vector<WsConnectContext> allConns = new Vector<WsConnectContext>();

	public IoJavalin(String name) {
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
        
//        app.get("/greet/{name}", ctx -> {
//            String pname = ctx.pathParam("name");
//            ctx.result("Hello, " + pname + "!");
//        }); //http://localhost:8080/greet/Alice
//        
//        app.get("/api/users", ctx -> {
//            Map<String, Object> user = Map.of("id", 1, "name", "Bob");
//            ctx.json(user); // Auto-converts to JSON
//        });
        
        /*
         * Javalin v5+: Si passa solo la "promessa" (il Supplier del Future). 
         * Javalin è diventato più intelligente: se il Future restituisce una Stringa, 
         * lui fa ctx.result(stringa). Se restituisce un oggetto, lui fa ctx.json(oggetto).
         * 
         */
//        app.get("/async", ctx -> {
//        	ctx.future(() -> {
//	        	// Creiamo il future
//	            CompletableFuture<String> future = new CompletableFuture<>();
//	            
//	            // Eseguiamo il lavoro in un altro thread
//	            new Thread(() -> { 
//	                try {
//	                    Thread.sleep(2000); // Simulazione calcolo pesante
//	                    future.complete(name + " | Risultato calcolato asincronamente");
//	                } catch (Exception e) {
//	                    future.completeExceptionally(e);
//	                }
//	            });
//	            
//	            return future; // Restituiamo il future a Javalin
//        	});
//        });
//        
//        app.get("/async1", ctx -> {
//            ctx.future(() -> CompletableFuture.supplyAsync(() -> {
//                // Simuliamo l'operazione lenta
//                try {
//                    Thread.sleep(2000); 
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return name + " | Risultato calcolato con supplyAsync";
//            }));
//        });
/*
 * --------------------------------------------
 * Parte Websocket
 * --------------------------------------------
 */      
        app.ws("/eval", ws -> {
            ws.onConnect(
		            ctx -> { //myctx=ctx; 
    	        int idAssegnato = pageCounter.incrementAndGet();
    	        String callerName = "caller"+idAssegnato;
     	        sendsafe(ctx, "ID:" + callerName);
     			if( firstCaller == null ) {
     				firstCaller = callerName;
     				//ownerctx    = ctx;
     			}
     			CommUtils.outmagenta("connected ..." + callerName);
     			allConns.add(ctx);
     			 
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
                try {
                	//La pagina e il mondo esterno comunicano col server con IApplMessage
                	IApplMessage m = new ApplMessage(message);
                    //Il server ha inviato la rep della pagina
                	if( m.msgContent().startsWith("[[")) {
                        CommUtils.outcyan(name + " | receives [[" + " from " + m.msgSender() + " to " + m.msgReceiver());
                        pageCtx.send( m.msgContent()); 
                		return;
                	}
                    CommUtils.outcyan(name + " | receives:" + message + " pageCtx=" + (pageCtx!=null) + " allConns:" +allConns.size());
                    if(  m.msgSender().equals("unknown") && m.msgContent().contains("canvasready") && pageCtx==null) { 
                    //Pa pagina iniziale ha inviato canvasready
                    	pageCtx = ctx;  //memorizzo connessione pagina
                    	CommUtils.outmagenta(name + " |  memorizzo pageCtx:" + pageCtx);
                    }else if( m.msgSender().equals("lifectrl") && m.msgId().contains("setcontroller")) { 
                    //Il controller remoto ha inviato setcontroller su una connessione 8080
                    	lifeCtrlCtx = ctx; //memorizzo connessione controller
                    	CommUtils.outmagenta(name + " |  memorizzo lifeCtrlCtx:" + lifeCtrlCtx );
            			sendsafe( lifeCtrlCtx, "msg( eval, dispatch, caller1, lifectrl, clear, 0 )" );
                     }else if( m.msgReceiver().equals(name) && m.msgContent().contains("cell(")) {    
                     //Il controller remoto ha detto di modificare il colore di una cella
                    	if (pageCtx != null) {
                        	//Funziona se ci sono 3 arg - es. cell(5,6,1)
                    		pageCtx.send( m.msgContent()); 
                     	}
                    	if( m.isRequest() ) {  
                    		//m viene da fuori, non dalla pagina
                    		IApplMessage reply = CommUtils.buildReply(name,"replyTo_"+m.msgId(),"done",m.msgSender()); 
                    		ctx.send(reply.toString());
                      	}else if( m.msgSender().equals(firstCaller) ){
                      	//La pagina ha inviato il comando di modificare il colore di una cella
                     		//ctx.send("updateCellColor sent from page");
                     		CommUtils.outmagenta("send to lifeCtrlCtx " + m);
                    		if( lifeCtrlCtx != null ) 
                    			sendsafe( lifeCtrlCtx, m.toString() );
                     	}
                    	
                    }else if( m.msgReceiver().equals("lifectrl") ) {  
                    	
                     	if( m.msgSender().equals("unknown") ){
                    		//Nuova paginna collegata
                    		return;
                    	}
                    	if( m.msgSender().equals(firstCaller) ){
                    		//La pagina sta inviando un comando al server un comando rivolto al controller
                    		//Es. [canvasready catturato prima] cell(x,y) clicked
                       		CommUtils.outred(name + " sending to lifeCtrlCtx: " + m);
                    		if( lifeCtrlCtx != null ) 
                    			sendsafe( lifeCtrlCtx, m.toString() );
                    		//msg( eval, dispatch, caller1, lifectrl, clear, 0 )
                     	}else {
                     		CommUtils.outred(" send to lifeCtrlCtx ???" + m.msgContent() );
//                    		if( pageCtx != null ) 
//                    			sendsafe( pageCtx,  m.msgContent() );                   		
                     	}
                    }
//                    CommUtils.outcyan(name + " |  allConns:" +allConns.size());
//                    allConns.forEach( (conn) ->	conn.send(m.msgContent()) );
                }catch(Exception e) {
                	CommUtils.outred(name + " |  not a IApplMessage:" + message);
//                	allConns.forEach( (conn) ->	conn.send(message) );
                }               
            });
        });        
	}
	
	protected void xxx(String cmd) {
		try {
			CommUtils.outgreen(name + " | connectToServer ..................... :");
			Interaction conn = WsConnection.create("localhost:8070", "eval");
	     	IApplMessage cmdmsg = CommUtils.buildRequest( "eval", "caller1", "lifectrl", cmd ); //"clear"
	     	//CommUtils.outblue("LifeGameInteraction | forward " + cmdmsg);
	     	IApplMessage answer = conn.request(cmdmsg);
	     	CommUtils.outblue("LifeGameInteraction | answer " + answer);
		} catch (Exception e) {
 			e.printStackTrace();
		}				
	}
    protected void sendsafe(WsContext ctx, String msg) {
    	synchronized (ctx.session) {  
            if (ctx.session.isOpen()) {
                ctx.send(msg);
            }
        }
    }
	

	
	public static void main(String[] args) {
		var resource = IoJavalin.class.getResource("/pages");
		CommUtils.outgreen("DEBUG: La cartella /page si trova in: " + resource);
		new IoJavalin("guiserver");
	}

}
