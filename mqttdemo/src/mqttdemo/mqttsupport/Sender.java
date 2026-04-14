package mqttsupport;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.mqtt.MqttConnectionCallbackForReceive;
import unibo.basicomm23.utils.CommUtils;

public class Sender {
	private final String MqttBroker = "tcp://localhost:1883"; //"tcp://broker.hivemq.com"; //tcp://test.mosquitto.org:1883
	private MqttSupport  mqttsupport = new MqttSupport( ); 
    private  String name;
 	
	
	public Sender(String name) { 
		this.name = name;
		CommUtils.outblue(name + "  | STARTS"  );
		mqttsupport.connectToBroker(name,MqttBroker);
		CommUtils.outblue(name + "  | CREATED"  );
		
		mqttsupport.cleartopic("unibo/receiverIn");
		doJob();
	}
	
    /*
     * ---------------------------------------------------------------------
     * Il sender invia una request sulla topic "unibo/receiverIn"
     * dopo avere fatto una subscribe sulla topic answ_info_agentsender
     * sulla quale dovrebbe inviare la reply il reciver 
     * ---------------------------------------------------------------------
     */
	public void doJob() {
		try {
			IApplMessage msgRequest = CommUtils.buildRequest(name, "info", "info("+name+",move)", "agentreceiver" );
			CallbackForReceive forreceivhandler = new CallbackForReceive( name );
			
			String answTopic="answ_" + msgRequest.msgId()+"_"+ msgRequest.msgSender();
			CommUtils.outyellow("        " +name + " |  answTopic="  + answTopic);
			mqttsupport.subscribe(answTopic,forreceivhandler);
 
			mqttsupport.publish(  "unibo/receiverIn",msgRequest.toString(),1,true );  //last arg: retained

			String answer = forreceivhandler.receive();
			CommUtils.outblue(name + " answer | " + answer ); 
 			CommUtils.outblue(name + " | BYE "  ); 		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//doJob

 
	public static void main(String[] args) { 		 
		Sender agent1 = new Sender("agentsender");   
    
 	}

}

 