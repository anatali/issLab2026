package claude;
//========================================
//ESEMPIO: Publisher MQTT
//========================================

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublisher {
 
 private MqttClient client;
 private String broker;
 private String clientId;
 
 public MqttPublisher(String broker, String clientId) throws MqttException {
     this.broker = broker;
     this.clientId = clientId;
     this.client = new MqttClient(broker, clientId, new MemoryPersistence());
 }
 
 public void connect() throws MqttException {
     MqttConnectOptions options = new MqttConnectOptions();
     options.setCleanSession(true);
     options.setAutomaticReconnect(true);
     
     System.out.println(clientId + " | Connessione a: " + broker);
     client.connect(options);
     System.out.println(clientId + " | Connesso!");
 }
 
 public void publish(String topic, String payload, int qos) throws MqttException {
     MqttMessage message = new MqttMessage(payload.getBytes());
     message.setQos(qos);
     //message.setRetained(true);
     client.publish(topic, message);
     System.out.println(clientId + " | Pubblicato su " + topic + ": " + payload);
 }
 
 public void disconnect() throws MqttException {
     if (client != null && client.isConnected()) {
         client.disconnect();
         client.close();
         System.out.println("Disconnesso");
     }
 }
 
 public static void main(String[] args) {
     String broker   = "tcp://localhost:1883"; //"tcp://broker.hivemq.com:1883";
     String clientId = "Publisher_" ; //+ System.currentTimeMillis();
     
     try {
         MqttPublisher publisher = new MqttPublisher(broker, clientId);
         publisher.connect();
         
         // Pubblica alcuni messaggi
         publisher.publish("test/temperature", "25.5", 1);
         Thread.sleep(1000);
         publisher.publish("test/humidity", "60", 1);
         Thread.sleep(1000);
         publisher.publish("test/status", "online", 0);
         
         publisher.disconnect();
         
     } catch (Exception e) {
         e.printStackTrace();
     }
 }
}

