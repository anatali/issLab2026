package usingSupport;

import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.mqtt.MqttSupport;

/*

 */
public class ReceiverWithListener  {
	private final String MqttBroker = "tcp://localhost:1883";//"tcp://broker.hivemq.com"; //
	private MqttSupport mqttSupport = new MqttSupport();
 	private String receiverIn = "receiverIn";
	private String name;
	
	public ReceiverWithListener(String name) {
		this.name = name;
		mqttSupport.connectToBroker(name,MqttBroker );
		mqttSupport.subscribe ( receiverIn, (topic, mqttmsg) -> {
			//Lambda is of type org.eclipse.paho.client.mqttv3.IMqttMessageListener
			String msg            = new String( mqttmsg.getPayload() );
			IApplMessage applMesg = new ApplMessage(msg);
			CommUtils.outmagenta(name + " | Riceve via listener: " + msg );
			if( applMesg.isRequest() ) {
				CommUtils.outred(name + " | WARNING: unable to handle requests " + applMesg);
				System.exit(0);
			}
		});
		CommUtils.outmagenta(name + " | CREATED"  );
	}
	
 

 
}
