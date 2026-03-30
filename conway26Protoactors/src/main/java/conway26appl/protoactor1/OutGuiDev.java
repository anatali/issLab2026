package conway26appl.protoactor1;
import java.util.Observable;
import com.fasterxml.jackson.databind.ObjectMapper;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import main.java.conway.domain.GameController;
import main.java.conway.domain.IGrid;
import main.java.conway.domain.IOutDev;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

public class OutGuiDev  {
	private String name = "outguidev";  //LOW CASE
	private Interaction connToGui ;
	private IApplMessage endmsg = CommUtils.buildDispatch("lifectrl", "endremoteclient", "end", "guiserver"  );
 	
	public OutGuiDev(){
		connectToServer();
 	}

    public void setController(String name) {
    	CommUtils.outgreen(name + " | setController: "  );
     	IApplMessage cmdmsg = CommUtils.buildDispatch(name, "setcontroller", "set(lifectrl,ws,'localhost:8070')", "guiserver"  );
     	CommUtils.outblue("LifeGameInteraction | forward " + cmdmsg);
     	try {
			connToGui.forward(cmdmsg);
		} catch (Exception e) {
			CommUtils.outred(name + " | setController ERROR " + e.getMessage() );
		}
//    	CommUtils.delay(300000);  //dura  
//    	CommUtils.outgreen(name + " | BYE "  );
    }
    
	protected void connectToServer() {
		if( connToGui == null  )
		try {
			CommUtils.outgreen(name + " | connectToServer ..................... :");
			connToGui = WsConnection.create("localhost:8080", "eval",null);
//	     	IApplMessage cmdmsg = CommUtils.buildDispatch("lifectrl", "setcontroller", "set(lifectrl,ws,'localhost:8070')", "guiserver"  );
//	     	CommUtils.outblue("LifeGameInteraction | forward " + cmdmsg);
//	     	conn.forward(cmdmsg);
	     	//Poi invio grid iniziale
	     	
	     	//lifecontroller.onClear();
	     	
		} catch (Exception e) {
 			e.printStackTrace();
		}		
	}
	
	public void display(String msg) 	 {
		CommUtils.outyellow(name + " | display " + msg);
		try {
			IApplMessage cmdmsg = CommUtils.buildDispatch("lifectrl", "eval", msg, "guiserver"  );
			connToGui.forward( cmdmsg );
		} catch (Exception e) {
 		}
	}


	
	public void close() {
		CommUtils.outblue(name + " | close => send " + endmsg );
		try {
			connToGui.forward(endmsg);
		} catch (Exception e) {
 			e.printStackTrace();
		}
	}

	protected boolean[][] getGridReAsBoolArrayp(IGrid grid, int rows, int cols) {
		//CommUtils.outcyan("              OutInGuiInteraction getGridReAsBoolArrayp " +  rows + " " + cols);
		boolean[][] simplegrid = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				simplegrid[i][j] = grid.getCell(i, j).isAlive();
			}
		}
		return simplegrid;
	}
	
	
	public void displayGrid(  IGrid grid ) {
		CommUtils.outyellow(name + " | displayGrid "  );
//		USING CANVAS
			ObjectMapper mapper = new ObjectMapper();
			boolean[][] grids = getGridReAsBoolArrayp(grid,grid.getRowsNum(), grid.getColsNum()) ; //new boolean[20][20];
			try {
				String jsonGrid = mapper.writeValueAsString(grids);
				//CommUtils.outcyan("              OutInGuiInteraction displayGrid jsonGrid " + jsonGrid );
				display(jsonGrid);
			} catch (Exception e) {
	 			e.printStackTrace();
			}
	}


}

