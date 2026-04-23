package sistemasqak;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

public class CallerSistemas {
	private Interaction conn ;
	private String name = "acaller";
	private IApplMessage evalRequest = CommUtils.buildRequest(name, "evalr",  "argr(2.0)", "sistemas");
	
	public CallerSistemas() {
		doJob();
	}
	
	protected void doJob() {
		conn = ConnectionFactory.createClientSupport(ProtocolType.tcp, "localhost", "8010");
		CommUtils.outblue(name + " | sending=" + evalRequest);
		try {
			IApplMessage answer = conn.request(evalRequest);
			CommUtils.outblue(name + " | answer=" + answer.msgContent());
		} catch (Exception e) {
 			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new CallerSistemas();
	}

}
