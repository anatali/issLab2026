package protoactor26;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

 

//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.mqtt.MqttInteraction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;

public class ProtoActorContext26Mqtt implements ProtoActorContextInterface{
	private String name;
	private String topicIn;
	private String topicOut;
	private Map<String, AbstractProtoactor26> protoactors = new ConcurrentHashMap<>();
	private String MqttBroker = "tcp://broker.hivemq.com";
	private MqttInteraction mqttConn;

	public ProtoActorContext26Mqtt( String name, String topicIn, String topicOut ) {
		this.name     = name;
		this.topicIn  = topicIn;
		this.topicOut = topicOut;
 		try {
			configureTheSystem();
		} catch (Exception e) {
 			e.printStackTrace();
		}		
	}
	@Override
	public void register( AbstractProtoactor26 pactor) {
		CommUtils.outmagenta(name + " ProtoActor26Mqtt | registered " + pactor.name + " in " + name );
		protoactors.put(pactor.name, pactor );
	}

	/*
	 * ----------------------------------------------------
	 * CNFIGURAZIONE DEL SERVER
	 * ----------------------------------------------------
	 */ 
	    protected void configureTheSystem() throws Exception {  
	    	//Connessione: input=ctx8070In  output=guiserverIn
	    	mqttConn = new MqttInteraction(name, MqttBroker, topicIn, topicOut );	//"ctxmqttIn", "guiservermqttIn"    	
	    	CommUtils.outyellow(name + " | ProtoActor26Mqtt configureTheSystem "  + mqttConn);
	    	doReceive();
	    	
 	    }
	    
	    protected void doReceive() throws Exception{
	    	new Thread() {
	    		public void run() {
	    			while( true ) {
	    			try {
	    				//CommUtils.outgreen(name + " | ProtoActor26Mqtt RECEIVING on " + mqttConn );
	    				//receiverOn = true;
	    				IApplMessage input = mqttConn.receive();
	    				CommUtils.outgreen(name + " | receives " +  input);

	    				IApplMessage answer = elabMsg( input  );
	                   	CommUtils.outyellow(name + " | ProtoActor26Mqtt reply " + answer);
	                   	if( input.isRequest() && answer != null ) {
	                   		CommUtils.outgreen(name + " | replies " +  answer);
	                   		mqttConn.reply(answer);
	                   	}
	    				
//	    				if( input != null && input.isRequest() ) {
//	    					IApplMessage msgReply = CommUtils.buildReply(name, input.msgId(), "answer("+name+",ok)", input.msgSender() );
//	    					CommUtils.outgreen(name + " | SENDS reply " +  msgReply);
//	    					mqttConn.reply(msgReply);
//	    					mqttConn.reply(msgReply);
//	    				}else if( input != null && input.isDispatch() ){
//	    					IApplMessage msgDispatch = CommUtils.buildDispatch(name, "cmd", "done("+name+",ok)", input.msgSender() );
//	    					CommUtils.outgreen(name + " | SENDS dispatch " +  msgDispatch);
//	    					mqttConn.forward(msgDispatch);
//	    				}else if( input != null && input.isEvent() ){
//	    					
//	    				}
	    			}catch(Exception e) {
	    				e.printStackTrace();
	    			}
	    		}//while
	    		}
	    	}.start();
	    }

        /*
         * Individua il protoactor destinatario e gli fa accodare 
         * il task appropriato di elaborazione-messaggio 
         */
	    @Override
        public IApplMessage elabMsg( IApplMessage am ) {
        	CommUtils.outyellow(name + " | ProtoActor26Mqtt elabMsg : " + am  ); 
        	String dest = am.msgReceiver();
    		AbstractProtoactor26 pactor=protoactors.get(am.msgReceiver());     
    		CommUtils.outyellow(name + " | ProtoActor26Mqtt finds : " + pactor  ); 
    		if( pactor != null ) {
    			IApplMessage answer = pactor.execMsg( am );
    			return answer;
    		}
    		else{ //ADDED MARCH 22
    			//dest non è un pactor locale => assumo sia remoto su una delle conn correnti
//    			allConns.forEach( conn -> {
//    				CommUtils.outyellow("invio a dest remoto:" + am);
//    				sendsafe(conn, am.toString()); 
//    			});
    			return am;   	
    		}
        }

/* Utility */
        @Override
        public void emitInfo(IApplMessage event) {
        	//CommUtils.outcyan("			emitInfo " + s);
    	}

//	    protected void mqttReceiver() {
//			new Thread() {
//				public void run() {
//					CommUtils.outgreen(name + " | mqttReceiver started ....."  );
//				}
//			}.start();    	
//	    }
/*	    
		protected void setMqttReceiver() {
			new Thread() {
				public void run() {
					CommUtils.outgreen(name + " | mqttReceiver started ....."  );
					      String broker = "tcp://broker.hivemq.com";  
					      try {
					          MqttClient client           = new MqttClient(broker, name);
					          MqttConnectOptions connOpts = new MqttConnectOptions();
					          connOpts.setCleanSession(true);
					          client.connect(connOpts);
					          CommUtils.outblue("connected to Broker");
					          // Subscribe 
					          String topic = "unibo/"+name+"/topic";  //"unibo/sistemaS/topic"
					          client.subscribe(topic, (t, msg) -> {
					            CommUtils.outmagenta(name + " | riceve sul topic " + t + ": " + new String(msg.getPayload()));
//					        	CompletableFuture<IApplMessage> responseFuture = new CompletableFuture<>();
//					        	IApplMessage am = new ApplMessage( new String(msg.getPayload()) );
// 					        	setinApplQueue(am,responseFuture);
//					        	responseFuture.thenAccept(res -> {
//					     	         CommUtils.outcyan("server invia risposta " + res );
//					     	         //String ss = res.toJsonString();
//					     	         String replyTopic = "reply/"+res.msgSender()+"/"+res.msgId();//"unibo/sistemaS/topic";
//					     	        CommUtils.outred("server invia risposta a: " + replyTopic );
//					     	         MqttMessage message = new MqttMessage(res.toString().getBytes());
//					     	         try {
//										client.publish(replyTopic, message);
//									} catch (MqttPersistenceException e) {
//										e.printStackTrace();
//									} catch (MqttException e) {
//										e.printStackTrace();
//									}
//					            });
//					        	
					          });
					           
					          // Disconnetti
					          //client.disconnect();
					        } catch ( Exception e) {
					          CommUtils.outred("Error: " + e.getMessage());
					        }        
								
				}			
			}.start();
		}
		*/
 }
