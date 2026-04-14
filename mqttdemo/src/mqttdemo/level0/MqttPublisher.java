package level0;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import unibo.basicomm23.utils.CommUtils;

/*
 * Usa org.eclipse.paho.client.mqttv3.MqttClient per trasmettere
 * informazioni sulla topic xxx
 */
public class MqttPublisher {
	private MqttClient client;
	private String clientName = "asender";
	private String topic      = "unibo/demo26";	
	private String brokerAddr = "tcp://localhost:1883"; //"wss://test.mosquitto.org:8081"; 
 	
	public MqttPublisher() {
	}

    public void doJob()    {
        try {
        	client = new MqttClient(brokerAddr, clientName, new MemoryPersistence());
    		client.connect();
  			CommUtils.outblue(clientName + " | CONNECTED");
  			for( int i=1; i<4; i++) {
  				String msg = "hello from publisher " + i ;
	  			sendMessageMqtt(msg);
  			}
  			System.exit(0);
        }catch(Exception e){
          CommUtils.outred(clientName + " | ERROR " + e.getMessage() );
        }
      }
    
    protected  void sendMessageMqtt( String m  )
            throws MqttSecurityException, MqttException {
      System.out.println(clientName+ " | sendMessageMqttd " + m);
	  MqttMessage mqttmsg = new MqttMessage();
	  mqttmsg.setQos(2);		//QoS LEVEL 2!!! 
	  mqttmsg.setPayload(m.getBytes());
	  client.publish(topic, mqttmsg);
	  //CommUtils.outblue("client HAS published " + mqttmsg);
	}    
    
    public static void main( String[] args)   {
        new MqttPublisher().doJob();

      }    
    
}
