package conway.io;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsMessageContext;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;

public class IoJavalin {
	
	private WsMessageContext pageCtx ;
	public IoJavalin() {
        var app = Javalin.create(config -> {
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
        	var inputStream = getClass().getResourceAsStream("/page/ConwayInOutPage.html");       	
        	if (inputStream != null) {
        		// Trasformiamo l'inputStream in stringa (o lo mandiamo come stream)
        	    String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        	    ctx.html(content);
        	} else {
		        ctx.status(404).result("File non trovato nel file system");
		    }
		    //ctx.result("Hello from Java!"));  //la forma più semplice di risposta
        }); 
        
        app.get("/greet/{name}", ctx -> {
            String name = ctx.pathParam("name");
            ctx.result("Hello, " + name + "!");
        }); //http://localhost:8080/greet/Alice
        
        app.get("/api/users", ctx -> {
            Map<String, Object> user = Map.of("id", 1, "name", "Bob");
            ctx.json(user); // Auto-converts to JSON
        });
        
        /*
         * Javalin v5+: Si passa solo la "promessa" (il Supplier del Future). 
         * Javalin è diventato più intelligente: se il Future restituisce una Stringa, 
         * lui fa ctx.result(stringa). Se restituisce un oggetto, lui fa ctx.json(oggetto).
         * 
         */
        app.get("/async", ctx -> {
        	ctx.future(() -> {
	        	// Creiamo il future
	            CompletableFuture<String> future = new CompletableFuture<>();
	            
	            // Eseguiamo il lavoro in un altro thread
	            new Thread(() -> { 
	                try {
	                    Thread.sleep(2000); // Simulazione calcolo pesante
	                    future.complete("IoJavalin | Risultato calcolato asincronamente");
	                } catch (Exception e) {
	                    future.completeExceptionally(e);
	                }
	            });
	            
	            return future; // Restituiamo il future a Javalin
        	});
        });
        
        app.get("/async1", ctx -> {
            ctx.future(() -> CompletableFuture.supplyAsync(() -> {
                // Simuliamo l'operazione lenta
                try {
                    Thread.sleep(2000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "IoJavalin | Risultato calcolato con supplyAsync";
            }));
        });
/*
 * --------------------------------------------
 * Parte Websocket
 * --------------------------------------------
 */
        
        app.ws("/chat", ws -> {
            ws.onConnect(ctx -> CommUtils.outgreen("Client connected chat!"));
            ws.onMessage(ctx -> {
                String message = ctx.message();
                CommUtils.outcyan("IoJavalin |  riceve:" + message);
                ctx.send("Echo: " + message);
            });
        });        
        app.ws("/eval", ws -> {
            ws.onConnect(ctx -> CommUtils.outgreen("IoJavalin | Client connected eval"));
            ws.onMessage(ctx -> {
                String message = ctx.message();     
                CommUtils.outblue("IoJavalin |  eval receives:" + message );
                try {
                	IApplMessage m = new ApplMessage(message);
                    CommUtils.outblue("IoJavalin |  eval:" + m.msgContent() );
                    if( m.msgContent().equals("ready")) { pageCtx = ctx; }
                    else if( m.msgContent().contains("cell(")) { pageCtx.send( m.msgContent()); }
                    else ctx.send(m.msgContent());
                }catch(Exception e) {
                	CommUtils.outred("IoJavalin |  error:" + e.getMessage());
                }               
            });
        });        
	}
	
 
	

	
	public static void main(String[] args) {
		var resource = IoJavalin.class.getResource("/pages");
		CommUtils.outgreen("DEBUG: La cartella /page si trova in: " + resource);
		new IoJavalin();
	}

}
