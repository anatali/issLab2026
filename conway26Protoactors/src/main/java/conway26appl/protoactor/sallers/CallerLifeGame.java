package conway26appl.protoactor.sallers;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

public class CallerLifeGame {
	private String name;
	private Interaction conn;
	
	public CallerLifeGame(String name) {
		this.name = name;
	}
	public void doJob() {
		conn = ConnectionFactory.createClientSupport(ProtocolType.ws, "localhost:8070","eval");
		CommUtils.outblue( name + " | doJob connected ");
		
		//msg(eval,dispatch,caller1,lifectrl,cell(7,7),0)		
		IApplMessage cmd = CommUtils.buildDispatch(name, "eval", "cell(5,5)", "lifectrl");
		/*
		 * Interagisce con lifectrl che potrebbe fare un chcck sul nome del caller
		 */
		try {
			conn.forward(cmd);
		} catch (Exception e) {
 			e.printStackTrace();
		}

		
		IApplMessage req = CommUtils.buildRequest(name, "eval", "cell(5,5)", "lifectrl");
		/*
		 * Interagisce con lifectrl che potrebbe fare un chcck sul nome del caller
		 */
		try {
			IApplMessage reply = conn.request(req);
			CommUtils.outblue( name + " | reply = " + reply);
		} catch (Exception e) {
 			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new CallerLifeGame("caller").doJob();
 	} 
}
