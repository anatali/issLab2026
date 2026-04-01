package sistemaS;

import protoactor26.AbstractProtoactor26;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.utils.CommUtils;

public class CallerAsProactor extends AbstractProtoactor26{

	public CallerAsProactor(String name, ProtoActorContextInterface ctx) {
		super(name, ctx);
 	}

	@Override
	protected void elabDispatch(IApplMessage m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IApplMessage elabRequest(IApplMessage req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void elabReply(IApplMessage req) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void elabEvent(IApplMessage ev) {
		CommUtils.outmagenta(name + " perceives " + ev);
 		
	}

	@Override
	protected void proactiveJob() {
		 new Thread() {
			 public void run() {
				 //CommUtils.outmagenta(name + " proactiva start");

					IApplMessage req   = CommUtils.buildRequest(name, "eval", "0.0", "sistemaS");
					CommUtils.outmagenta(name + " | requestSynch=" + req);
					IApplMessage reply = request(req);  //bloccante sincrona long
					CommUtils.outmagenta(name + " | reply=" + reply);
			 
			 }
		 }.start();
		
	}

}
