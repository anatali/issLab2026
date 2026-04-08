package conway26LifeganeTcp;

import main.java.conway.domain.LifeInterface;
import protoactor26.AbstractProtoactor26;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.conway.domain.ICell;
import main.java.conway.domain.IGrid;
import main.java.conway.domain.Life;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.basicomm23.utils.CommUtils;

/*
 * PREMESSA:  
 */

public class LifeGamePactorPolling extends AbstractProtoactor26 {
	protected LifeInterface life = null;
	protected ScheduledExecutorService playExecutor = Executors.newSingleThreadScheduledExecutor();
	protected Interaction connToServer;
	protected IApplMessage readCmd = CommUtils.buildRequest(name, "readGuiCmd", "do", "guiserver");
	protected boolean goon = true;

	public LifeGamePactorPolling(String name, ProtoActorContextInterface ctx) {
		super(name, ctx);
		life = new Life(20, 20); // ncell in iomap.js
		connToServer = ConnectionFactory.createClientSupport(ProtocolType.tcp, "localhost", "8050");
		CommUtils.outblue(name + " | connToServer " + connToServer);
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
	protected IApplMessage elabRequest(IApplMessage req) {
		CommUtils.outblue(name + " | elabRequest:" + req);
		elab(req);
		String gridRepFroCanvas = toJson(life.getGrid().repAsBoolArray());
		IApplMessage replyMsg = CommUtils.buildReply(name, req.msgId(), gridRepFroCanvas, req.msgSender());
		CommUtils.outgreen(name + " | replyMsg to " + replyMsg.msgReceiver());
		return replyMsg;
 	}

	@Override
	protected void elabReply(IApplMessage m) {
		CommUtils.outblue(name + " | elabReply from " + m.msgSender());
	}

	@Override
	protected void elabEvent(IApplMessage ev) {
		CommUtils.outblue(name + " | elabEvent:" + ev);
	}

	protected void elab(IApplMessage m) {
		String payload = m.msgContent();
		if (payload.startsWith("cell")) {
			String[] coords = payload.replace("cell(", "").replace(")", "").split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			switchCellState(x, y);
			displayGrid(); // For canvas
		} else if (payload.startsWith("start")) {
			onStart();
		} else if (payload.startsWith("stop")) {
			onStop();
		} else if (payload.startsWith("clear")) {
			onClear();
		} else if (payload.startsWith("exit")) {
			System.exit(0);
		}
	}

	protected void onStart() {
		// CommUtils.outblue(name + " | onStart running=" + running + " playTask=" +
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
		CommUtils.outgreen(name + " | onClearrrrrrrrrrrrrrrrr ");
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
	protected void proactiveJob() {
		ScheduledExecutorService proactiveJobExecutor = Executors.newSingleThreadScheduledExecutor();
		Future<?> playTask = proactiveJobExecutor.submit(() -> proactiveTask());
	}

	/*
	 * PARTE PROATTIVA demo di LifeGamePactor
	 * 
	 */
 
	protected void proactiveTask() {
		CommUtils.delay(2000); // wait for setup
		CommUtils.outblue(name + " proactiveTask displayGrid");
		displayGrid();
		CommUtils.delay(2000);
		/*
		 * Call readCmd in polling
		 */
		while (goon) {
			boolean done = taskRead();
			if (!done)
				CommUtils.delay(800); // CommUtils.delay(500) does not work
		}
		CommUtils.outmagenta(name + " |  proactiveTask  BYE");
		System.exit(0);
	}

	protected boolean taskRead() {
		CommUtils.outgreen(name + " doing inputCmd " + readCmd);
		IApplMessage inputCmd;
		try {
			inputCmd = connToServer.request(readCmd); //bloccante? Si fino a inputCmd
			CommUtils.outmagenta(name + " taskRead inputCmd ...... " + inputCmd);
//			if (inputCmd.msgContent().startsWith("nocmd")) {
//				CommUtils.outblue(name + " elabInputCmd donothing since nocmd");
//				return false;
//			}
			elabInputCmd(inputCmd);
			return true;
		} catch (Exception e) {
			CommUtils.outred(name + " doRead error " + e.getMessage());
			goon = false;
			return true;
		}
	}// taskRead

	protected void elabInputCmd(IApplMessage inputCmd) {
		CommUtils.outmagenta(name + " inputCmd " + inputCmd);
		if (inputCmd.msgContent().startsWith("cell(")) {
			String[] coords = inputCmd.msgContent().replace("cell(", "").replace(")", "").split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			switchCellState(x, y);
			CommUtils.outblue(name + " elabInputCmd displayGrid");
			displayGrid();
		} else if (inputCmd.msgContent().equals("start"))
			onStart();
		else if (inputCmd.msgContent().equals("stop"))
			onStop();
		else if (inputCmd.msgContent().equals("clear"))
			onClear();
		else if (inputCmd.msgContent().equals("exit"))
			System.exit(0);
	}

	/*
	 * PARTE PROATTIVA Coneay
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
			if (connToServer != null)
				connToServer.forward(displayCmd);
		} catch (Exception e) {
			CommUtils.outred(name + " ERROR " + e.getMessage());
		}
	}

	protected boolean[][] getGridReAsBoolArrayp(IGrid grid, int rows, int cols) {
		//CommUtils.outcyan("  getGridReAsBoolArrayp " + rows + " " + cols);
		boolean[][] simplegrid = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				simplegrid[i][j] = grid.getCell(i, j).isAlive();
			}
		}
		return simplegrid;
	}

}
