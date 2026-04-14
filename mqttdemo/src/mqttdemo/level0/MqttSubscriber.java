package level0;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import unibo.basicomm23.utils.CommUtils;


/*
 * Usa org.eclipse.paho.client.mqttv3.MqttClient per ricevere
 * informazioni dalla topic xxx
 */
public class MqttSubscriber {
	
    String brokerUrl = "tcp://localhost:1883"; // URL del broker Mosquitto
    String clientId  = "asubscriber"; // ID univoco per il client
    String topic     = "unibo/demo26"; // Topic da sottoscrivere
	

    public void doJob() {
        try {
            // Creazione del client MQTT
            MqttClient client = new MqttClient(brokerUrl, clientId);
 
            // Configurazione del callback per la gestione dei messaggi
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    CommUtils.outblue("Connessione persa: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                	System.out.println(   "messageArrived | Payload: " + message.getPayload()); //byte[]
                   CommUtils.outmagenta("messageArrived | " + message); //converted in String
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Non necessario per i subscriber, usato solo per i publisher
                }
            }); //setCallback
            
            // Connessione al broker
            client.connect();
            CommUtils.outcyan(clientId + " | Connesso al broker: " + brokerUrl);

            // Sottoscrizione al topic
            client.subscribe(topic);
            CommUtils.outcyan(clientId + " | Sottoscritto al topic: " + topic);
            
            //Riceve ciò che invia ...
            //client.publish(topic, new MqttMessage("Hello from receiver".getBytes()));

        } catch (MqttException e) {
        	CommUtils.outred("ERROR " + e.getMessage() );
        }
    	
    }
	public static void main(String[] args) {
        new MqttSubscriber().doJob();	
    }

}
