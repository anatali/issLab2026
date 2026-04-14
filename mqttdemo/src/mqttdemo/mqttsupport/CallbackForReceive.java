package mqttsupport;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import unibo.basicomm23.utils.ColorsOut;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.CommUtilsOrig;
 

public class CallbackForReceive implements MqttCallback{
 	private BlockingQueue<String> callbackQueue = null;
 	private String name;

 	public CallbackForReceive( String name  ) {
 		this.name = name;
  		CommUtils.outgreen(name + " | CallbackForReceive CREATED a CallbackForReceive");
 		callbackQueue = new LinkedBlockingDeque<String>(10);
  	}
 	public CallbackForReceive( String name, BlockingQueue<String> blockingQueue  ) {
 		this.name = name;
 		CommUtils.outgreen(name + " | CallbackForReceive CREATED ");
 		this.callbackQueue = blockingQueue;
  	}

		@Override
		public void connectionLost(Throwable cause) {
			CommUtils.outred(name + " | CallbackForReceive connectionLost cause="+cause);
	 	}

		@Override
		public void messageArrived(String topic, MqttMessage message)   {
			try {
				CommUtils.outyellow( name + " | CallbackForReceive  messageArrived:" + message + " queue=" + callbackQueue.size());
				if( callbackQueue != null ) {
					callbackQueue.put( message.toString() );	
//					CommUtils.outyellow(name + " | inserted N=" + blockingQueue.size() + " " + message );
					
				}
			} catch (Exception e) {
				ColorsOut.outerr(name + " | CallbackForReceive messageArrived Error:"+e.getMessage());		
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			try {
//				CommUtils.outyellow(name + " | CallbackForReceive deliveryComplete token=" 
//			       + token.getMessage() + " client=" + token.getClient().getClientId() );
			} catch (Exception e) {
				CommUtilsOrig.outred(name + " |CallbackForReceive  deliveryComplete Error:"+e.getMessage());		
			}
	 	}
		
		public String receive()   {
			try {
				CommUtils.outyellow(name + " | CallbackForReceive receiving callbackQueue N=" + callbackQueue.size() );
				String mm =  callbackQueue.take();
				CommUtils.outyellow(name + " | CallbackForReceive receive N=" + callbackQueue.size() + " taken:" + mm );
				return mm;
			} catch (InterruptedException e) {
				return null;
			}
		}
		
}
