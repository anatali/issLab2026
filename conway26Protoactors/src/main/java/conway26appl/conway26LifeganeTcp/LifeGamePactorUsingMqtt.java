package conway26LifeganeTcp;

import main.java.conway.domain.LifeInterface;
import protoactor26.AbstractProtoactor26;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.conway.domain.ICell;
import main.java.conway.domain.IGrid;
import main.java.conway.domain.Life;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.mqtt.MqttSupport;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.basicomm23.utils.CommUtils;

/*
 * PREMESSA:  
 */

public class LifeGamePactorUsingMqtt extends AbstractProtoactor26 {
	protected LifeInterface life = null;
	protected ScheduledExecutorService playExecutor = Executors.newSingleThreadScheduledExecutor();
	protected Interaction connToServer;
	protected String MqttBroker         = "tcp://localhost:1883"; //"tcp://broker.hivemq.com";  
	protected MqttSupport mqttsupport   =  new MqttSupport();
	
	/*
	 * 
	 */
	protected MqttCallback mqttCallback =  new MqttCallback() {  
        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("Connessione persa: " + cause.getMessage());
        }
        
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println("  " + name + " | Messaggio ricevuto via MQTT:");
            System.out.println("  Topic: " + topic);
            System.out.println("  Payload: " + new String(message.getPayload()));
            System.out.println("  Retained: " + message.isRetained());
            System.out.println("  QoS: " + message.getQos()); 
            
            IApplMessage applMessage = new ApplMessage( new String(message.getPayload()) );
            elab( applMessage );
         }
        
        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("Consegna completata");
        }
    };
    
    /*
     * CONSTRUCTOR
     */
	public LifeGamePactorUsingMqtt(String name, ProtoActorContextInterface ctx) {
		super(name, ctx);
		life = new Life(20, 20); // ncell in iomap.js
		connToServer = ConnectionFactory.createClientSupport(ProtocolType.tcp, "localhost", "8050");
		CommUtils.outblue(name + " | connToServer " +connToServer );
		
		mqttsupport.connectToBroker( name, MqttBroker );
		mqttsupport.subscribe(name+"In", mqttCallback );
		
		displayGrid();
	}
   
	/*
	 * Metodi di elaborazione messaggi in arrivo al server da parte di ....
	 */

	@Override
	protected void elabDispatch(IApplMessage m) {
		CommUtils.outgreen(name + " | elabDispatch " + m);
		elab(m);
	}

	@Override
	protected void elabEvent(IApplMessage inputCmd) {
		CommUtils.outblue(name + " | elabEvent:" + inputCmd);
	}
	
	@Override
	protected IApplMessage elabRequest(IApplMessage req) {
		CommUtils.outblue(name + " | elabRequest:" + req);
		elab(req);
		String gridRepFroCanvas = toJson(life.getGrid().repAsBoolArray());
		IApplMessage replyMsg = CommUtils.buildReply(name, req.msgId(), gridRepFroCanvas, req.msgSender());
		CommUtils.outgreen(name + " | replyMsg to " + replyMsg.msgReceiver());
		return replyMsg;
	}
	
	protected void elab( IApplMessage m) {
		String payload = m.msgContent();
		if (payload.startsWith("cell")) {
			String[] coords = payload.replace("cell(", "").replace(")", "").split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			switchCellState(x, y);
			displayGrid(); // For canvas			
		}else if (payload.startsWith("start")) {
			onStart();
		}else if (payload.startsWith("stop")) {
			onStop();
		}else if (payload.startsWith("clear")) {
			onClear();
		}else if (payload.startsWith("exit")) {
			 System.exit(0);
		}
	}

	protected void onStart() {
		if (running)
			return; // start sent while running
		running = true;

		if (playTask == null) {
			playTask = playExecutor.submit(() -> play());
			// simulateVacuumFluctuations( );
		}
	}

	protected void onStop() {
		running = false;
		if (playTask != null) {
			playTask.cancel(true);
			playTask = null;
		}
	}

	protected void onClear() {
		onStop();
		CommUtils.delay(500); // prima fermo e poi ...
		epoch = 0;
		life.resetGrids();
		CommUtils.outgreen(name + " | onClearrrrrrrrrrrrrrrrr "  );
		displayGrid(); // For canvas
	}

	
	protected void switchCellState(int x, int y) { // synchronized??
		ICell c = life.getCell(x, y);
		c.switchCellState();
	}

	protected String toJson(boolean[][] gridrep) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonArrayGridRep = mapper.writeValueAsString(gridrep);
			return jsonArrayGridRep;
		} catch (Exception e) {
			boolean[][] empty = new boolean[0][0];
			return "" + empty; // Griglia vuota
		}
	}

	@Override
	protected void elabReply(IApplMessage m) {
		CommUtils.outblue(name + " | elabReply from " + m.msgSender());

	}



	@Override
	protected void proactiveJob() {
//		ScheduledExecutorService proactiveJobExecutor = Executors.newSingleThreadScheduledExecutor();
//		Future<?> playTask = proactiveJobExecutor.submit(() -> proactiveTask());
	}


	/*
	 * PARTE PROATTIVA Conway
	 */
	protected Future<?> playTask;
	protected boolean running = false;
	protected int epoch = 0;
	protected int generationTime = 500;

	protected void play() {
		CommUtils.outmagenta(name + " |  play  started ---------------- ");
		while (running) {
			try {
				TimeUnit.MILLISECONDS.sleep(generationTime);
				life.nextGeneration();
				displayGrid();
				CommUtils.outblue("---------Epoch ---- " + epoch++);
				// manageStable(life.getGrid());
			} catch (InterruptedException e) {
				CommUtils.outred(name + " | play sleep interrupted");
			}
		} // while
	}

	/*
	 * Utilities to display the grid
	 */
	protected void displayGrid() {
		// CommUtils.outcyan( name + " | displayGrid");
		displayGrid(life.getGrid());
	}

	protected void displayGrid(IGrid grid) {
		ObjectMapper mapper = new ObjectMapper();
		boolean[][] grids = getGridReAsBoolArrayp(grid, grid.getRowsNum(), grid.getColsNum()); // new boolean[20][20];
		try {
			String jsonGrid = mapper.writeValueAsString(grids);
			IApplMessage displayCmd = CommUtils.buildDispatch(name, "display", jsonGrid, "guiserver");
			CommUtils.outcyan(name + " | forward " + displayCmd.msgId());
			if(connToServer!=null) connToServer.forward(displayCmd);
		} catch (Exception e) {
			CommUtils.outred(name + " ERROR " + e.getMessage());
		}
	}

	protected boolean[][] getGridReAsBoolArrayp(IGrid grid, int rows, int cols) {
		// CommUtils.outcyan(" OutInGuiInteraction getGridReAsBoolArrayp " + rows + " "
		// + cols);
		boolean[][] simplegrid = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				simplegrid[i][j] = grid.getCell(i, j).isAlive();
			}
		}
		return simplegrid;
	}

}
