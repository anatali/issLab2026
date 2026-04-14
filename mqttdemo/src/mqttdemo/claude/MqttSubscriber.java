package claude;
//========================================
//ESEMPIO: Subscriber MQTT
//========================================

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSubscriber {
 
 private MqttClient client;
 private String broker;
 private String clientId;
 
 public MqttSubscriber(String broker, String clientId) throws MqttException {
     this.broker   = broker;
     this.clientId = clientId;
     this.client   = new MqttClient(broker, clientId, new MemoryPersistence());
 }
 
 public void connect() throws MqttException {
     MqttConnectOptions options = new MqttConnectOptions();
     options.setCleanSession(true);
     options.setAutomaticReconnect(true);
     
     // Imposta callback
     client.setCallback(new MqttCallback() {
         @Override
         public void connectionLost(Throwable cause) {
             System.out.println(clientId + " | Connessione persa: " + cause.getMessage());
         }
         
         @Override
         public void messageArrived(String topic, MqttMessage message) {
             System.out.println("\n=== Nuovo Messaggio === " + clientId);
             System.out.println("Topic: " + topic);
             System.out.println("Payload: " + new String(message.getPayload()));
             System.out.println("QoS: " + message.getQos());
             System.out.println("Retained: " + message.isRetained());
             System.out.println("=====================\n");
         }
         
         @Override
         public void deliveryComplete(IMqttDeliveryToken token) {
             // Non usato per subscriber
         }
     });
     
     System.out.println(clientId + " | Connessione a: " + broker);
     client.connect(options);
     System.out.println(clientId + " | Connesso!");
 }
 
 public void subscribe(String topic, int qos) throws MqttException {
     client.subscribe(topic, qos);
     System.out.println(clientId + " | Sottoscritto a: " + topic + " (QoS " + qos + ")");
 }
 
 public void subscribeMultiple(String[] topics, int[] qos) throws MqttException {
     client.subscribe(topics, qos);
     System.out.println(clientId + " | Sottoscritto a " + topics.length + " topics");
 }
 
 public void disconnect() throws MqttException {
     if (client != null && client.isConnected()) {
         client.disconnect();
         client.close();
         System.out.println(clientId + " | Disconnesso");
     }
 }
 
 public static void main(String[] args) {
     String broker   = "tcp://localhost:1883"; //"tcp://broker.hivemq.com:1883";
     String clientId = "Subscriber_" ; //+ System.currentTimeMillis();
     
     try {
         MqttSubscriber subscriber = new MqttSubscriber(broker, clientId);
         subscriber.connect();
         
         // Subscribe a topic singolo
//         subscriber.subscribe("test/temperature", 1);
         
         // Oppure subscribe a multipli topics
          String[] topics = {"test/temperature", "test/humidity", "test/status"};
          int[] qos = {1, 1, 0};
          subscriber.subscribeMultiple(topics, qos);
         
         System.out.println("In ascolto... Premi CTRL+C per uscire");
         
         // Mantieni il programma attivo
         while (true) {
             Thread.sleep(1000);
         }
         
     } catch (Exception e) {
         e.printStackTrace();
     }
 }
}
