package usingSupport;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.mqtt.MqttSupport;
import unibo.basicomm23.utils.CommUtils;

/*
 * Usa MqttSupport.publish  sulla topic receiverIn
 * I messaggi sono String che rappresentano event, dispatch e request
 * 
 * Per ricevere la risposta alla request, il sender dovrebbe fare
 * MqttSupport.subscribe  sulla topic ????
 */

public class Sender {
	private final String MqttBroker = "tcp://localhost:1883";// "tcp://broker.hivemq.com"; //
	private MqttSupport mqttConn = new MqttSupport();
	private String name;
	private String receiverIn = "receiverIn";
	private int dt = 700;

	public Sender(String name) {
		this.name = name;
		mqttConn.connectToBroker(name, MqttBroker);
		CommUtils.outcyan(name + " | CREATED");
		CommUtils.delay(1000); // also the receiver should be created or started as first
		doJob();
	}

	public void doJob() {
		try {
			IApplMessage msgEvent    = CommUtils.buildEvent("sender", "alarm", "alarm(fire)");
			IApplMessage msgDispatch = CommUtils.buildDispatch("sender", "cmd", "cmd(move)", "receiver");
			IApplMessage msgRequest   = CommUtils.buildRequest("sender", "info", "info(move)", "receiver");
			CommUtils.outblue(name + "  | SENDS event " + msgEvent);
			mqttConn.publish(receiverIn, msgEvent.toString());
			CommUtils.delay(dt);
			CommUtils.outblue(name + "  | SENDS dispatch " + msgDispatch);
			mqttConn.publish(receiverIn, msgDispatch.toString());
			CommUtils.delay(dt);
			CommUtils.outblue(name + "  | SENDS request " + msgRequest);
			mqttConn.publish(receiverIn, msgRequest.toString());
			CommUtils.delay(dt);
			CommUtils.outblue(name + "  | ENDS ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// doJob
}
