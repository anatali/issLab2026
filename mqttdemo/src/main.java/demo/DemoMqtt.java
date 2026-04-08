package demo;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import unibo.basicomm23.utils.CommUtils;

public class DemoMqtt {
	
	public void doJob() throws Exception {
		// Connessione Punto-a-Punto al Broker
		//MqttClient client = new MqttClient("tcp://broker.hivemq.com:1883", "ClientID");
		MqttClient client = new MqttClient("tcp://localhost:1883", "ClientID");
		client.connect();
		CommUtils.outblue("client connected");
		//Subscribe
		
		client.subscribe("unibo/mqttdemo", (topic, msg) -> {
			CommUtils.outmagenta("Ricevuto in modo asincrono: " + new String(msg.getPayload()));
		});
		CommUtils.outblue("client subscribed");
		
		//Publish
		client.publish("unibo/mqttdemo", new MqttMessage("4.0".getBytes()));	
		CommUtils.delay(1000); //give time to receive ...
		CommUtils.outblue("client BYE");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception { 		 
		DemoMqtt appl = new DemoMqtt( );   
		appl.doJob();
    
 	}

}
