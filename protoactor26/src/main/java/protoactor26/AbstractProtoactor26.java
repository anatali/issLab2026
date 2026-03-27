package protoactor26; 
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import io.javalin.Javalin;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.utils.CommUtils;

/*
 */
	public abstract class AbstractProtoactor26 {
	
		protected String name ;
		//protected ProtoActorContext26 context;
		protected ProtoActorContextInterface context;
// 		protected Javalin server;
		protected ScheduledExecutorService msgexecutor  = Executors.newSingleThreadScheduledExecutor();
	
//	protected record WorkTask(
//	     IApplMessage message, 
//    	 CompletableFuture<IApplMessage> future // Il "canale" di ritorno
//    ) {}
 	
	public AbstractProtoactor26(String name, ProtoActorContextInterface  ctx) { //ProtoActorContext26
		this.name     = name;
		this.context  = ctx;
//		server        = ctx.getServer();
		context.register(this); 
		proactiveJob(  );
	}

    /*
     * Abstract methods to be implemented in the application class
     */
    protected abstract void elabDispatch(IApplMessage m );    
    protected abstract IApplMessage elabRequest(IApplMessage req  );
    protected abstract void elabReply(IApplMessage req  );
    protected abstract void elabEvent(IApplMessage ev );
    protected abstract void proactiveJob(  );
 
	/*
	 * ----------------------------------------------------
	 * Called by context for internal interactions
	 * The task given to the executor calls methods
	 * defined in the specialized actor
	 * ----------------------------------------------------
	 */     
    public IApplMessage execMsg(IApplMessage am) {
        if (am.isEvent()) {
            executeTask( msgexecutor, ( ) -> elabEvent(am) );
            return null;
        } else if (am.isDispatch()) {
            executeTask(msgexecutor, () -> elabDispatch(am) );
            return null;
        } else if (am.isRequest()) { 
        	return dorequestSynch(am);
        	//return dorequestAsynch(am);
        }else if (am.isReply()) {
            executeTask(msgexecutor, () -> elabReply(am) );
            return null;        	
        }else return null;      
      }
    
    protected IApplMessage dorequestSynch(IApplMessage am) {
    	try {//pianifica l'azione da eseguire al completamento => blocking
			Future<IApplMessage> res = msgexecutor.submit( () -> { 
				//System.out.println("Thread " + Thread.currentThread().getName() + " request: " + am);
				IApplMessage replyMsg = elabRequest(am);
		    	//CommUtils.outyellow("AbstractProtoactor26 request send reply " +  replyMsg);
		    	return replyMsg;
	 		});  
			IApplMessage answer = res.get(); //blocca fino al completamento
			//CommUtils.outyellow("AbstractProtoactor26 request send reply "  );
			return answer;
    	}catch( Exception e) {
    		return null;
    	}      	
    }
    
    protected IApplMessage dorequestAsynch(IApplMessage am) {
        executeTask(msgexecutor, () -> {
        	IApplMessage r = elabRequest(am);
        	CommUtils.outyellow("AbstractProtoactor26 request r=" + r  );
        	//dico al contesto che deve elaborare la reply
        	context.elabMsg( r  );
        });
        return null;
    }

    /*
     * -----------------------------------------------------
     * Elaborazione dei messaggi
     * -----------------------------------------------------
     */
    protected void executeTask(ScheduledExecutorService taskexecutor, Runnable todoTask) {
        taskexecutor.execute(() -> {
        	//CommUtils.outmagenta(name + " executeTask " + Thread.currentThread().getName() + " elabora: " + todoTask);
            try {
                todoTask.run();
            } catch (Exception ex) {
            	CommUtils.outred(name + " Errore nell'elaborazione del messaggio: " + ex.getMessage());
            }
        });
      } 

 
 
     
 /*
  * ----------------------------------------------------
  * PARTE InteractionBasic
  * ----------------------------------------------------
  */
    
    protected void forward(IApplMessage msg) {
    	//CommUtils.outyellow(name + " forward "+ msg);    	
    	context.elabMsg( msg );
     }
     
	protected IApplMessage request(IApplMessage msg)   {
     	//CommUtils.outyellow(name + " doing request "+ msg);
    	IApplMessage answer = context.elabMsg( msg );   
    	//CommUtils.outyellow(name + " answer to request: "+ answer);
		return answer;
	}

    protected void emitInfo(IApplMessage ev) {
    	//CommUtils.outyellow(name + " emitInfo "+ ev);
    	context.emitInfo(ev);
    }

 
}//AbstractProtoactor26


 
