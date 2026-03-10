package conway.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
//import main.java.conway.domain.GameController;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;

public class IoJavalinOld {
	protected static AtomicInteger pageCounter         = new AtomicInteger(0);
	protected static Vector<WsConnectContext> allConns = new Vector<WsConnectContext>();
	protected String firstCaller = null;
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	protected ScheduledFuture<?> heartbeatTask;
	
	public static boolean canvasMode = false;
	public static boolean pageReady  = false;
	private WsConnectContext myctx = null;
	private WsMessageContext controllerctx = null;
//	private GameController controller;
	private Javalin server;
	private String name = "ioJavalin";
	
	public IoJavalinOld() {
		activateHTTP();
		activateWS();
	}
	
//	public void setController(GameController controller) {
//		this.controller = controller;
//	}
	
	public void setCanvasMode() {
		canvasMode  = true;
		pageReady   = true;
	}
	
	protected void activateHTTP() {
		server = Javalin.create(config -> {
//			config.bundledPlugins.enableCors(cors -> {
//				cors.addRule(it -> it.anyHost());
//			});
			
			config.jetty.modifyWebSocketServletFactory(factory -> {
				factory.setIdleTimeout(Duration.ofMinutes(30));
			});
			config.staticFiles.add(staticFiles -> {
				//C:\Didattica2026\protobook\src\main\java\conway\io
		        staticFiles.directory = "./src/main/java/conway/io"; //"C:/Didattica2026/protobook/src/main/java/conway/io"; //"./src/main/java/conway/io" ;
		        staticFiles.location = Location.EXTERNAL;
		    });
		}).start(8080);
    	
		server.get("/", ctx ->  
    	{   //Directory del progrramma: C:\Didattica2026\protobook
    		Path path = Path.of("./src/main/java/conway/io/ConwayInOutPage.html");  //ConwayInOutPage.html  LifeIInOutCanvas.html
    		//Path path = Path.of("C:/Didattica2026/protobook/src/main/java/conway/io/ConwayInOutPage.html");
		    if (Files.exists(path)) {
		        // Usiamo Files.newInputStream che è più moderno di FileInputStream
		        ctx.contentType("text/html").result(Files.newInputStream(path));
		    } else {
		        ctx.status(404).result("File non trovato nel file system");
		    }
    	});
	}
	
	public void activateWS() {
//		Javalin app =
//		          Javalin.create().start(8080);
		//PARTE WS ------------------------------------
		/*3*/ server.ws("/eval",
		/*4*/  ws -> {
		/*5*/   ws.onConnect(
		            ctx -> {  //ctx.send("welcome"); }
    	        int idAssegnato = pageCounter.incrementAndGet();
    	        String callerNAme = "caller"+idAssegnato;
     			//emitEvent("ID:" + callerNAme, ctx);
    	        sendsafe(ctx, "ID:" + callerNAme);
     			if( firstCaller == null ) {
     				firstCaller = callerNAme;
     				myctx       = ctx;
     				CommUtils.outred(name + " | set myctx=" + myctx);
     			}
     			allConns.add(ctx);
     			CommUtils.outmagenta(name + " | ws: connection from " + callerNAme + " Nconn=" + allConns.size()); 
     			sendsafe(ctx, callerNAme +"_conn_N_" + allConns.size());
//				CommUtils.outyellow(name + " | mqtt: connection added " + ctx.host());
     			 
        // Ogni 20 secondi invia un segnale per "svegliare" i proxy
//    	heartbeatTask = executor.scheduleAtFixedRate(
//            () -> { if(ctx.session.isOpen()) sendsafe(ctx,"PING");}, //lambda // CommUtils.outcyan("PING");
//                    20,  //QUANTO ASPETTARE LA PRIMA VOLTA. Se 0, il primo PING parte istantaneamente (inutile)
//                    20,  //OGNI QUANTO RIPETERE
//                    TimeUnit.SECONDS
//            );
		            });
		
		/*6*/   ws.onMessage(ctx -> {
		        String message = ctx.message();
		        CommUtils.outblue("IoJavalin | onMessage:" + message); //msg( eval, dispatch, unknown, lifectrl, canvasready, 0 )
		        try {
		    		if (message.contains("canvas")) {
		    			//CommUtils.outred("IoJavalin | elabMsg canvas");
		    			setCanvasMode();
		    			return;
		    		}
		    		else if (message.contains("ready")) {
		    			pageReady = true;
		    			return;
		    		}
		        	IApplMessage msg = new ApplMessage( message );
		        	CommUtils.outmagenta("IoJavalin | msg:" + msg );
		        	CommUtils.outblue("IoJavalin | msg:" + msg.msgContent());
		        	
		        	if( msg.isRequest() && msg.msgContent().equals("setcontroller")) {
		        		controllerctx = ctx;
		        		controllerctx.send("ok");
		        		return;
		        	}
		        	
		/*9*/       elabMsg( msg.msgContent() );
		        }catch (Exception e) {
		        	CommUtils.outmagenta("IoJavalin | WARNING:" + e.getMessage() );
		        	//elabMsg( message );
		/*9*/       ctx.send("Unknown:" + e.getMessage());
		        }
		      });
		/*10*/ws.onClose(
		         ctx->{System.out.println("IoJavalin bye");}
		      );
		     });//ws		
	}
	
	protected void sendMsg(String msg) {
		//CommUtils.outblue("IoJavalin | send msg=" + msg);
		//myctx.send(msg);
		allConns.forEach( (conn) -> {if( conn.session.isOpen() ) sendsafe(conn, msg); });
	}
	
    protected void sendsafe(WsContext ctx, String msg) {
    	synchronized (ctx.session) { // Il lucchetto sulla sessione è vitale
            if (ctx.session.isOpen()) {
                ctx.send(msg);
            }
        }
    }
	
	protected void elabMsg(String msg) {
		CommUtils.outyellow("IoJavalin | elabMsg msg=" + msg);
//		if (controller == null) {
//			CommUtils.outred("IoJavalin | elabMsg controller == null");
//			return;
//		}
		if (msg.startsWith("cell(")) {
 			String params = msg.replace("cell(","").replace(")","");
//			CommUtils.outblue("IoJavalin | elabMsg params=" + params);
			String[] xy = params.split(",");
			int x = Integer.parseInt(xy[0]) ;
			int y = Integer.parseInt(xy[1]) ;
			CommUtils.outgreen("IoJavalin | elabMsg cell x=" + x + " y=" + y + " " + myctx);
			myctx.send("cell(X,Y,1)".replace("X",""+y).replace("Y",""+x));
//			controller.switchCellState(x, y);
//		} 
		}else if (msg.equals("start")) {
			 CommUtils.outmagenta("TOD: communicate with caller");
			 if(controllerctx != null) controllerctx.send("start");
//		} else if (msg.equals("stop")) {
//			controller.onStop();
//		} else if (msg.equals("clear")) {
//			controller.onClear();
//		} else if (msg.equals("exit")) {
//			System.exit(0);
		} else {
			CommUtils.outblue("IoJavalin | elabMsg UNKNOWN:" + msg);
		}
	}

}
