package main.java.javalin; 
import java.util.Map;
import org.json.simple.JSONObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.websocket.WsMessageContext;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
 

public class SistemaSJavalinBetter {

	private Javalin app = null;
	
    /*1*/ protected double eval(double x) {
    	System.out.println("eval: " + x);
        if (x > 4.0) {
            CommUtils.outmagenta("eval | Simulo ritardo per x=" + x);
            CommUtils.delay(8000);
          }
    	return Math.sin(x) + Math.cos( Math.sqrt(3)*x);
    }

	protected void setUpServer( boolean forWS ) {
		CommUtils.outmagenta("setUpServer forWS=" + forWS );
		if (forWS ) {
			if( app == null ) app = Javalin.create().start(8080);
		}else { //forHTTP			
			if (app == null) {
				app = Javalin.create(config -> {
					config.bundledPlugins.enableCors(cors -> {
						cors.addRule(it -> it.anyHost());
					});
				}).start(8080);
			}else {
				CommUtils.outmagenta("Server già avviato. Configuro per CORS ");
	    		app.before(ctx -> {
	    		    ctx.header("Access-Control-Allow-Origin", "*"); // Permette a TUTTI (o metti il tuo dominio)
	    		    ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	    		    ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
	    		    ctx.header("Access-Control-Allow-Credentials", "true");
	    		});
	
	    		// Gestisce le richieste OPTIONS (Preflight)
	    		app.options("/*", ctx -> {
	    		    ctx.status(204); // No Content - conferma che il server accetta la chiamata
	    		});
			}//app != null
		}
		
 
	}
	
 /* 
  * -------------------------------------------------
  * PARTE HTTP  - Stile funzionale
  * -------------------------------------------------
  */
 
    protected double readInputHTTP(JSONObject b) throws NumberFormatException{
        String xs = ""+b.get("x");
        double x  = Double.parseDouble(xs);
        CommUtils.outblue("x="+x  );
        return x;
    }
    
    protected String handlerHTTP(Context ctx) {
   	 //See https://javalin.io/documentation#context
        try {
        	JSONObject m  = CommUtils.parseForJson(ctx.body());
        	CommUtils.outblue("m="+m  );
        	double x      = readInputHTTP(m);
            double result = eval(x);                
            return "risultato HTTP="+result;     
        } catch (NumberFormatException e) {
           return "Errore HTTP: numero non valido";
        }
    }
    
    protected void setWorkHTTP( ) {
    	setUpServer( false ); //per HTTP
    	app.get("/", ctx -> ctx.result("Hello World via HTTP/1.1")); 
        
    	app.get("/eval/{n}", ctx -> {
    		System.out.println("Ricevuta richiesta per il valore: " + ctx.pathParam("n"));
    		//ctx.result("Valutato: " + 20);
    		String numeroStr = ctx.pathParam("n");
    		double valore    = Double.parseDouble(numeroStr);
    		double r         = eval( valore );
    		System.out.println("Risultatoe: " + r);
    		//ctx.json(Map.of("fullUrl", ctx.fullUrl(), "result", r));
    		ctx.result("Valutato: " + r);
    	});
    	
//        app.get("/eval", ctx -> {
//	          double x = Double.parseDouble(ctx.queryParam("x"));
//              double r = eval( x );
//              ctx.json(Map.of("fullUrl", ctx.fullUrl(), "result", r));
//	     });
              
        app.post("/evaluate", ctx -> {  //Warning: check CORS
    	  		 String result = handlerHTTP( ctx );
       	         //Invia risposta in JSON
                 ctx.json(Map.of("fullUrl", ctx.fullUrl(), "body", ctx.body(), "result", result));
       });
    }
 
/* 
 * -------------------------------------------------
 * PARTE WS  - Stile funzionale
 * -------------------------------------------------
*/


    protected double readInputWS(String message) throws NumberFormatException{
    	System.out.println("Messaggio ricevuto su WS: " + message);
        double x = Double.parseDouble(message);   
    	return x;
    }
    
    protected double readInputApplMessageWS(String msjson) throws NumberFormatException{
    	System.out.println("ApplMessage ricevuto: " + msjson);
    	IApplMessage m = ApplMessage.cvtJson( msjson );
    	double x = Double.parseDouble(m.msgContent());
    	return x;
    }

    
    protected String handlerWS(WsMessageContext ctx) {
        try {
        	String m       = ctx.message();     
        	CommUtils.outmagenta(	"handlerWS m="+m  );
        	double x       = readInputWS(m);
            double result  = eval(x);  
            return "risultato WS="+result + " per x="+x;  
        } catch (NumberFormatException e) {
           return "Errore WS: numero non valido";
        }
    }
  
    protected void setWorkWS( ) {
       	/*2*/ setUpServer( true ); //per WS


        /*3*/ app.ws("/eval", 
        /*4*/  ws -> {  //ws di tipo `io.javalin.websocket.WsConfig`
        /*5*/   ws.onConnect( ctx -> {  //ctx di tipo `io.javalin.websocket.WsConnectContext`
        			//Thread.sleep(1000); //SOLO PER TEST!
        			String ss = "{\"msg\" : \"welcome\" }";
        			CommUtils.outgreen("server: send msg proactive .... ");
        			ctx.send( ss  ); //Invia messaggio di benvenuto  
					CommUtils.outgreen("server: connection established");
				});
        /*6*/   ws.onMessage(ctx -> { //ctx di tipo `io.javalin.websocket.WsMessageContext`
	                String message = ctx.message();
 	                String answer  = handlerWS( ctx );
 	                CommUtils.outcyan("server invia risposta: " + answer);
	    /*7*/   ctx.send( answer );
              });
        /*8*/ ws.onClose(ctx -> { //ctx di tipo `io.javalin.websocket.WsCloseContext`
        	   System.out.println("server: connection closed"); 
        	  } );
            });
     }

/* ----------------------------------------------------
* Attivo sia pa rate WS che la parte HTTP
* ----------------------------------------------------
*/    
    public void configureTheSystem() {   
    	setWorkWS( );   
    	setWorkHTTP();
        CommUtils.outblue("server avviato su ws://localhost:8080/eval e su HTTP");
    }

	
    public static void main(String[] args) {
    	new SistemaSJavalinBetter().configureTheSystem();
     }
}//SistemaSJavalinBetter


 
