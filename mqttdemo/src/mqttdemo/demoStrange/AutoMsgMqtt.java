package demoStrange;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import unibo.basicomm23.utils.CommUtils;

public class AutoMsgMqtt {
	private String clientID = "aclient";
	
	public void doJob() throws Exception {
		// Connessione Punto-a-Punto al Broker
		//MqttClient client = new MqttClient("tcp://broker.hivemq.com:1883", "ClientID");
		MqttClient client = new MqttClient("tcp://localhost:1883", clientID);
		client.connect();
		CommUtils.outblue(clientID + " | connected");
		//Subscribe
		
		client.subscribe("unibo/mqttdemo", (topic, msg) -> {
			CommUtils.outmagenta(clientID + " | Riceve: " + new String(msg.getPayload()));
		});
		CommUtils.outblue(clientID + " | subscribed");
		
		//Publish
		String msg = "4.0 from " + clientID;
		CommUtils.outgreen( clientID + " | trasmette");
		client.publish("unibo/mqttdemo", new MqttMessage( msg.getBytes()));	
		CommUtils.delay(1000); //give time to receive ...
		client.disconnect();
		CommUtils.outblue( clientID + " | BYE");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception { 		 
		AutoMsgMqtt appl = new AutoMsgMqtt( );   
		appl.doJob();
    
 	}

}
