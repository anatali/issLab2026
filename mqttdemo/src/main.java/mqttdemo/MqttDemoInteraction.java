package mqttdemo;

import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.mqtt.MqttConnection;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.CommUtilsOrig;
import unibo.basicomm23.utils.Connection;

public class MqttDemoInteraction {
private String brokerAddr    = "tcp://localhost:1883"; //"tcp://broker.hivemq.com";  


//private final String caller    = "demo";


//String sender, String msgId, String payload, String dest
private String helloMsg  = CommUtilsOrig.buildDispatch("sender", "cmd", "hello","receiver").toString();

public MqttDemoInteraction() {
	doJob();
}

public void doJob() {
	simulateReceiver("receiver","receiverIn");
	CommUtils.delay(500);
	
	simulateSender("sender","receiverIn");
	CommUtils.delay(5000);
	CommUtils.outcyan(  "doJob ENDS"  );
	System.exit(0);
}

 
/*
 * -----------------------------------------------------------------------
 * Versione che usa MqttConnection
 * -----------------------------------------------------------------------
 */
public void simulateReceiver(String name, String topic) {
	new Thread() {
		public void run() {
		try { //topicIn-topicOut
			Interaction mqtt = MqttConnection.create(brokerAddr, name+"-receiverIn-senderIn"); 
			//((Connection) mqtt).setTrace(true);
 			CommUtils.outmagenta( name + " as receiver STARTS and waits on " + mqtt );
			String inputMNsg = mqtt.receiveMsg();
			CommUtils.outmagenta( name + " as receiver RECEIVED:"+ inputMNsg );
 		} catch (Exception e) {
 			CommUtils.outred(name + " as receiver  | Error:" + e.getMessage());
	 	}
		}//run
	}.start();
}

public void simulateSender(String name, String topic) {
	new Thread() {
		public void run() {
		try {//topicIn-topicOut
			Interaction mqtt = MqttConnection.create(brokerAddr, name+"-senderIn-receiverIn"); 
			//((Connection) mqtt).setTrace(true);
 			CommUtils.outgreen( name + " as sender STARTS and send   " + helloMsg );
			mqtt.forward(helloMsg);
			CommUtils.outgreen( name + " as sender ENDS "   );
  		} catch (Exception e) {
 			CommUtils.outred(name + " as sender  | Error:" + e.getMessage());
	 	}
		}//run
	}.start();
}

 
/*
 * -------------------------------------------------
 * REQUEST - RESPONSE
 * -------------------------------------------------
 */


//public void simulateCalled( String name ) {
//	new Thread() {
//		public void run() {
//		try {
// 			CommUtilsOrig.outgreen(name + " | STARTS " + brokerAddr );
// 			//Interaction mqtt = MqttConnection.create(brokerAddr, name+"-demoOut-demoIn");
// 			Interaction mqtt = new MqttInteraction(name, brokerAddr, "demoOut", "demoIn");
//			String inputMNsg = mqtt.receiveMsg();  //Si blocca sulla coda popolata da 
//			CommUtils.outgreen(name + " | RECEIVED:"+ inputMNsg );
////Elaborate and send answer			
// 			IApplMessage msgInput = new ApplMessage(inputMNsg);
// 			CommUtils.outgreen(name + " | input=" + msgInput  );
//			String caller = msgInput.msgSender();
//			String reqId  = msgInput.msgId();
//			String myReply = CommUtilsOrig.buildReply(name ,  reqId, "ANSWER", caller  ).toString();
//			String content = "time('" + java.time.LocalTime.now() + "')";
// 			
//			String answer  = myReply.replace("ANSWER", content );  
//			CommUtilsOrig.outgreen( name + " | replies:"+ answer );
//			mqtt.reply(answer);  			
// 		} catch (Exception e) {
// 			CommUtilsOrig.outred(name + " | Error:" + e.getMessage());
//	 	}
//		}//run 
//	}.start(); 
//}
//
//public void doRequestRespond() {
//	simulateCalled( "called1");
//	simulateCalled( "called2");
//	//Caller part 	
//	try {
//		CommUtilsOrig.outgreen(caller + " | STARTS "  );
//		Interaction mqtt = MqttConnection.create(brokerAddr, caller+"-demoIn-demoOut" );
//		String req1    = CommUtils.buildRequest(caller,  "query","getTime",  "called1").toString();
//		String answer1 = mqtt.request(req1);	 //blocking
//		CommUtils.outblue(caller + " RECEIVED answer1:"+ answer1 );
//
//		String req2 = CommUtils.buildRequest(caller,  "query","getTime",  "called2").toString();
// 		String answer2 = mqtt.request(req2);	   //blocking
// 		CommUtils.outblue(caller + " RECEIVED answer2:"+ answer2 );
// 	} catch (Exception e) {
//		e.printStackTrace();
//	}
//}
 
	public static void main(String[] args) throws Exception  {
		 new MqttDemoInteraction();	
 	}

}
