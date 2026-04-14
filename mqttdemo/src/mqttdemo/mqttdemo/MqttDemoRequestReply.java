package mqttdemo;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.mqtt.MqttConnection;
import unibo.basicomm23.mqtt.MqttConnectionBaseInSynch;
import unibo.basicomm23.mqtt.MqttConnectionBaseOut;
import unibo.basicomm23.mqtt.MqttInteraction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.CommUtilsOrig;
import unibo.basicomm23.utils.Connection;

public class MqttDemoRequestReply {
private String brokerAddr    = "tcp://localhost:1883"; //"tcp://broker.hivemq.com";  

 
//String sender, String msgId, String payload, String dest
//private String helloMsg  = CommUtilsOrig.buildDispatch("sender", "cmd", "hello","receiver").toString();

public MqttDemoRequestReply() {
	doRequestReply();
}

public void doRequestReply() {
	simulateCalled( "called1" );
 	simulateCalled( "called2" );
	CommUtils.delay(500);
	simulateCaller( "caller" );
	CommUtils.delay(5000);
	CommUtils.outcyan(  "doJob ENDS"  );
	System.exit(0);
}



 /*
 * -------------------------------------------------
 * REQUEST - RESPONSE
 * -------------------------------------------------
 */

//topicIn-topicOut

public void simulateCalled( String name ) {
	new Thread() {
		public void run() {
		try {
 			CommUtilsOrig.outgreen(name + " | STARTS " + brokerAddr );
 			//Interaction mqtt = MqttConnection.create(brokerAddr, name+"-demoOut-demoIn");
 			Interaction mqtt = new MqttInteraction(name, brokerAddr, name+"In", "neverUsedOut");
			String inputMNsg = mqtt.receiveMsg();  //Si blocca sulla coda popolata da 
			CommUtils.outblue(name + " | RECEIVED:"+ inputMNsg );
//Elaborate and send answer			
 			IApplMessage msgInput = new ApplMessage(inputMNsg);
 			//CommUtils.outgreen(name + " | input=" + msgInput  );
			
 			String caller = msgInput.msgSender();
			String reqId  = msgInput.msgId();
			String myReply = CommUtils.buildReply(name ,  reqId, "ANSWER", caller  ).toString();
			String content = "time('" + java.time.LocalTime.now() + "')";
 			
			String answer  = myReply.replace("ANSWER", content );  
			CommUtilsOrig.outgreen( name + " | replies: "+ answer + " " + mqtt);
			
			mqtt.reply( new ApplMessage(answer) );  //se no send su topicOut			
 		} catch (Exception e) {
 			CommUtilsOrig.outred(name + " | Error:" + e.getMessage());
	 	}
		}//run 
	}.start(); 
}

public void simulateCaller(String caller) {
	//Caller part 	
	try {
		 
		CommUtilsOrig.outgreen(caller + " | STARTS "  );
		Interaction mqtt = MqttConnection.create(brokerAddr, caller+"-neverUsedIn-called1In" );
		String req1      = CommUtils.buildRequest(caller,  "query","getTime",  "called1").toString();
		String answer1   = mqtt.request(req1);	 //blocking
		CommUtils.outblack(caller + " RECEIVED answer1:"+ answer1 );

//ANOTHER	
 		Interaction mqtt2 = MqttConnection.create(brokerAddr, "xxx-neverUsedIn-called2In" );
		String req2 = CommUtils.buildRequest(caller,  "query","getTime",  "called2").toString();
 		String answer2 = mqtt2.request(req2);	   //blocking
 		CommUtils.outblack(caller + " RECEIVED answer2:"+ answer2 );
 	} catch (Exception e) {
		e.printStackTrace();
	}
}
 
	public static void main(String[] args) throws Exception  {
		 new MqttDemoRequestReply();	
 	}

}
