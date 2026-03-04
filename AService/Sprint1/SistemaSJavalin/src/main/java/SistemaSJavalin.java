package main.java; 
import java.util.Map;
import io.javalin.Javalin;
import unibo.basicomm23.utils.CommUtils;
/*
 * Sistema impostato come da esempi documentali
 * 
 * USAGE: JS   : CallerBasic.html
 * USAGE: JAVA : CallerWsclientReqSlow.java
 */

public class SistemaSJavalin {
	
	protected double eval(double x) {
		if (x > 4.0) {
			CommUtils.outmagenta( "eval | Simulo ritardo per x=" + x);
			CommUtils.delay(8000);
		}
		return Math.sin(x) + Math.cos( Math.sqrt(3)*x);
	} 

	public void doJob() {
		//Create and configure
    	/*2*/ var app = Javalin.create(config -> {   
             	config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        }).start(8080);                   

//Parte WS -------------------------------
        /*3*/ app.ws("/eval", 
        /*4*/  ws -> {
        /*5*/   ws.onConnect( ctx -> { 
        			Thread.sleep(1000); // FERMA il thread per un secondo (SOLO PER TEST!)
        			ctx.send("welcome"); } 
                );
        /*6*/   ws.onMessage(ctx -> {
                String message = ctx.message();
                System.out.println("Messaggio ricevuto: " + message);
                // Il messaggio: numero in formato stringa
				try {
			        double x      = Double.parseDouble(message);
			        double result = eval(x); //Math.sin(x) + Math.cos( Math.sqrt(3)*x);
 		 /*7*/      ctx.send( result );
				} catch (NumberFormatException e) {
         /*7*/	    ctx.send("WS - Errore: numero non valido");
 				}
               });
        /*8*/ ws.onClose(ctx -> { System.out.println("connection closed"); } );
            }); //ws

//Parte HTTP ------------------------------       
             app.get("/", ctx -> ctx.result("Hello World via HTTP/1.1")); 
  	         
             app.get("/eval", ctx -> {
	            double x = Double.parseDouble(ctx.queryParam("x")); //ERROR?
	            double r = eval(x); //Math.sin(x) + Math.cos( Math.sqrt(3)*x);
	            ctx.result("" + r);
	         });
            
  	         app.post("/evaluate", ctx -> {  //Warning: check CORS
            	 //See https://javalin.io/documentation#context
 				try {
	  	        	CommUtils.outmagenta("m="+ctx.body());
	    	         org.json.simple.JSONObject m = CommUtils.parseForJson(ctx.body());
	   	        	//CommUtils.outmagenta("m="+m);
	    	         String xs = ""+m.get("x");
	    	         //CommUtils.outmagenta("xs="+xs);
					double x = Double.parseDouble(xs);
					double r = eval(x); //Math.sin(x) + Math.cos(Math.sqrt(3) * x);
					CommUtils.outblue("HTTP POST x=" + x + " res=" + r);
					// Invia risposta in JSON
					ctx.json(Map.of("fullUrl", ctx.fullUrl(), "body", ctx.body(), "result", r));
				} catch (Exception e) {
					CommUtils.outred("HTTP POST errore:" +  e.getMessage());
					ctx.json(Map.of("fullUrl", ctx.fullUrl(), "body", ctx.body(), 
							"result", "Errore:  " + e.getMessage()));
				}
            });
            			 
            
		System.out.println("server su ws://localhost:8080/eval e http");
	}//doJob
	
    public static void main(String[] args) {
    	new SistemaSJavalin().doJob();
     }
}//SistemaSJavalin


 
