package sistemaS;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import protoactor26.AbstractProtoactor26;
import protoactor26.ProtoActorContext26;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.utils.CommUtils;

public class SistemaSAsProtoactor extends AbstractProtoactor26{
    protected ScheduledExecutorService proactiveJobExecutor;

	public SistemaSAsProtoactor(String name, ProtoActorContext26 context) {
		super(name, context);
	}

	protected void lastwishes() {}
	
//	protected void prepareCmdHTTP(  ) {
//		//http://localhost:8050/evaluate?x=4
//		if(server != null) server.get("/evaluate", ctx -> {
//        double x = Double.parseDouble(ctx.queryParam("x"));
//              double r = eval( x );
//              ctx.json(Map.of("fullUrl", ctx.fullUrl(), "result =", r));
//        });
//	}

	 
    protected double eval(double x) {
    	CommUtils.outblue(name + " | eval: " + x);
        if (x > 4.0) {
            CommUtils.outmagenta(name + " | Simulo ritardo per x=" + x);
            CommUtils.delay(3000);
          }
    	return Math.sin(x) + Math.cos( Math.sqrt(3)*x);
    }
    
	@Override
    protected void elabEvent(IApplMessage ev ) {
		CommUtils.outblue(name + " | perceives:" + ev);
	}
 
	@Override
    protected IApplMessage elabRequest(IApplMessage req ) {
    	CommUtils.outblue(name + " | elabRequest:" + req);
    	//emitInfo( name + " elab request from:" + req.msgSender());
    	//emitInfo(name + " elab request from:" + req.msgSender() + " NumConn=" + allConns.size());
        if( req.msgId().equals("eval")){
            double x      = Double.parseDouble(req.msgContent());
            double result = eval(x);
            String resMsg = "f(V,R)".replace("V",req.msgContent()).replace("R",""+result);   //term PrologS
            IApplMessage replyMsg = 
            CommUtils.buildReply(name,req.msgId(),resMsg,req.msgSender()); 
//            emitInfo(name + " request from:" + req.msgSender() + " NumConn=" + allConns.size() + " input="+ x + " result=" + result);
            return replyMsg;
          }else{
            IApplMessage replyMsg = 
              CommUtils.buildReply(name,req.msgId(),"requestUnkown",req.msgSender());
            return replyMsg;
          }
    }

	@Override
    protected void elabDispatch(IApplMessage m ) {
    	CommUtils.outgreen(name + " | elabDispatch from" + m.msgSender());
//     	emitInfo( "elab_dispatch_" + m.msgContent() );
    }
	
	@Override
	protected void elabReply(IApplMessage req) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void proactiveJob() {
 		proactiveJobExecutor = Executors.newSingleThreadScheduledExecutor();
		Future<?>  playTask = proactiveJobExecutor.submit( () -> proactiveTask() );	
		
	}
	
	protected void proactiveTask() {
		CommUtils.outgreen(name + " | proactiveTask started ....."  );
		for(int i=1; i<=5; i++) {
				String time = LocalTime.now().getMinute() + ":"  + LocalTime.now().getSecond();
				CommUtils.outyellow(name + " | emitInfo "  + time);	
				IApplMessage evtime = CommUtils.buildEvent(name, "info", time);
				emitInfo( evtime );				
				CommUtils.delay(2000);
		} 
		CommUtils.outgreen(name + " | proactiveTask ENDS "  );
//		if(playTask !=null) playTask.cancel(true);
	}

	


 
}
