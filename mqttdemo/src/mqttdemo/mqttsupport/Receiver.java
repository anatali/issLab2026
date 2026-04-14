package mqttsupport;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;

public class Receiver  {
    private String name ;
	private final String MqttBroker = "tcp://localhost:1883";//"tcp://broker.hivemq.com"; 
	private MqttSupport mqttsupport =  new MqttSupport();

	/*
     * ----------------------------------------------------------------------------
     * Il receiver fa una subscribe sulla topic "unibo/receiverIn"
     * specificando un obj di tipo MqttCallback come handler.
     * 
     * Il metodo messageArrived di MqttCallback  trasforma la String
     * ricevuta in un IApplMessage m che viene elaborato da elabMessage.
     * 
     * elabMessage invia la risposta sulla topic 
     * "answ_" + m.msgId()+"_"+ m.msgSender()
	 * -----------------------------------------------------------------------------
	 * */

	public Receiver(String name) {
		CommUtils.outmagenta("        " + name + "  | STARTS"  );
		this.name = name;
	    mqttsupport.connectToBroker( name, MqttBroker );
		CommUtils.outmagenta("        " + name + "  | connected"  );

		MqttCallback mqttCallback =  new MqttCallback() {  //called by the library
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connessione persa: " + cause.getMessage());
            }
            
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println(name + " | Messaggio ricevuto:");
                System.out.println("  Topic: " + topic);
                System.out.println("  Payload: " + new String(message.getPayload()));
                System.out.println("  QoS: " + message.getQos());
                IApplMessage applMessage = new ApplMessage( new String(message.getPayload()) );
                
                elabMessage( applMessage );
            }
            
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Consegna completata");
            }
        };
        
        CommUtils.outmagenta("        " + name + "  | subscribes"  );
		mqttsupport.subscribe("unibo/receiverIn", mqttCallback );
			
		  

	}
    
	protected void elabMessage(IApplMessage message) {
		try {
			
		    if( message.isEvent() ) {
		    	CommUtils.outblue("        " +name + " | elab event " + message ); 
		    }
		    else if (message.isRequest()) {
				CommUtils.outblue("        " +name + " |  elab request " + message); 
				//reply id deve essere quello della richiesta 
				IApplMessage replyMsg = 
						CommUtils.buildReply(name, message.msgId(), 
								"answer_to_"+message.msgSender()+"_"+message.msgId(), message.msgSender());
				CommUtils.outblue("        " +name + " |  sending " + replyMsg); 
				
				String replyTopic = "answ_" + message.msgId()+"_"+ message.msgSender() ;
				CommUtils.outblue("        " +name + " |  replyTopic="  + replyTopic);
				mqttsupport.publish(replyTopic, replyMsg.toString() );
				
				//Dopo la risposta emette un evebto di allarme che dovrebbe essere percepito dal sender
//				CommUtils.outblue("        " +name + " |  forward event to test ... " + msgEvent);
//				mqttConn.forward( msgEvent );  //invia su senderIn
			}
			else if( message.isDispatch()  ) {
				CommUtils.outblue("        " +name + " |  elab dispatch "  + message);
			}
		} catch (Exception e) {
			CommUtils.outblue("        " +name + " | elab bare string " + message );
		}
	}
	
 

	public static void main(String[] args) { 		 
		new Receiver("agentreceiver");   
    
 	}
 
 	
}


 