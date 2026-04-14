package usingSupport;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.mqtt.MqttSupport;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;

/*
 * Usa MqttSupport.subscribe implementando MqttCallback
 * La ricezione di un messaggio sulla topic receiverIn
 * avviene nel metodo di callback messageArrived
 * mentre il receiver opera entro un Thread
 * 
 * Si noti il problema della richiesta
 */
public class ReceiverWithCallback implements MqttCallback{
	private final String MqttBroker = "tcp://localhost:1883";//"tcp://broker.hivemq.com"; //
	private MqttSupport mqttSupport = new MqttSupport();
	private String name;
	private boolean goon = true;
	private String receiverIn = "receiverIn";
	
	public ReceiverWithCallback(String name) {
		this.name = name;
		mqttSupport.connectToBroker(name,MqttBroker );
		mqttSupport.subscribe ( receiverIn, this );
		CommUtils.outmagenta(name + " | CREATED"  );
	}
	
	public void doJob() {
		new Thread() {
			public void run() {
				while( goon ) {
					CommUtils.outyellow(name + " | working ... Messages are perceived in messageArrived called by the support"  );	
					CommUtils.delay(250);
				}
				CommUtils.outcyan(name + " |  ENDS"  );		
				System.exit(0);
		}
		}.start();
	}

	@Override
	public void connectionLost(Throwable cause) {
		CommUtils.outred(name + " | connectionLost " + cause.getMessage() );
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		CommUtils.outmagenta(name + " | messageArrived " + message + " on " + topic);	
		if( message.toString().equals("END") ) {
			goon = false;
			return;
		}
		try {
			IApplMessage msg =  new ApplMessage(  message.toString() ); //potrebbe dare Exception
//			CommUtils.outgreen(name + " |  msgid   = " + msgInput.msgId() );
//			CommUtils.outgreen(name + " |  msgtype = " + msgInput.msgType() );
//			CommUtils.outgreen(name + " |  emitter = " + msgInput.msgSender());
//			CommUtils.outgreen(name + " |  dest    = " + msgInput.msgReceiver());
//			CommUtils.outgreen(name + " |  content = " + msgInput.msgContent());

			if( msg.isRequest() ) {  	
				CommUtils.outred(name + " | vorrei rispondere alla richiesta, ma come? "  );
			}		 
			
		}catch( Exception e) {
			CommUtils.outred(name + "  message unknown "  );
		}


	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		CommUtils.outyellow(name + " | deliveryComplete " + token.getMessageId() );		
	}
}
