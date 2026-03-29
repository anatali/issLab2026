package conway26appl;
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

public class OutInGuiInteraction implements IOutDev, IObserver{
	private String name = "outInGuiInteraction";  //LOW CASE
	private Interaction conn ;
	private GameController lifecontroller;
	
	public OutInGuiInteraction(){
		connectToServer();
 	}

    public void setController(GameController controller) {
    	CommUtils.outgreen(name + " | setController: " + controller);
    	lifecontroller = controller;
     	lifecontroller.onClear();
    	CommUtils.delay(300000);  //dura  
    	CommUtils.outgreen(name + " | BYE "  );
    }
    
	protected void connectToServer() {
		if( conn == null  )
		try {
			CommUtils.outgreen(name + " | connectToServer ..................... :");
			conn = WsConnection.create("localhost:8080", "eval",this);
	     	IApplMessage cmdmsg = CommUtils.buildDispatch("lifectrl", "setcontroller", "set(lifectrl,ws,0)", "guiserver"  );
	     	CommUtils.outblue("LifeGameInteraction | forward " + cmdmsg);
	     	conn.forward(cmdmsg);
	     	//Poi invio grid iniziale
	     	
	     	//lifecontroller.onClear();
	     	
		} catch (Exception e) {
 			e.printStackTrace();
		}		
	}
	@Override
	public void display(String msg) 	 {
		//CommUtils.outyellow(name + " | display " + msg);
		try {
			IApplMessage cmdmsg = CommUtils.buildDispatch("lifectrl", "eval", msg, "guiserver"  );
			conn.forward( cmdmsg );
		} catch (Exception e) {
 		}
	}

	//noy used here, since using canvas
	@Override
	public void displayCell(IGrid grid, int x, int y) {
	}

	@Override
	public void close() {
		CommUtils.outblue(name + " | close "  );
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
	
	@Override
	public void displayGrid(  IGrid grid ) {
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

/*
 * IObserver
 */
	@Override
	public void update(Observable o, Object arg) {
		//CommUtils.outblue(name + " | update2 "  );
		update(""+arg);
	}

	@Override
	public void update(String value) {
		//Evito di visualizzare la canvas rep
		if( ! value.contains("[[")) 
			CommUtils.outblue(name + " | update "  + value);	
		try {
		IApplMessage msg = new ApplMessage( value );
			if( msg.msgContent().contains("cell(")){
				Struct s = (Struct) Term.parse(msg.msgContent());
				lifecontroller.switchCellState( Integer.parseInt(""+s.getArg(0) ),Integer.parseInt(""+s.getArg(1))); 
			}
			else if( msg.msgContent().contains("clear")){
				lifecontroller.onClear();				
			}
			else if( msg.msgContent().contains("start")){
				lifecontroller.onStart();				
			}
			else if( msg.msgContent().contains("stop")){
				lifecontroller.onStop();				
			}
		}catch(Exception e) {
			
		}
	}

}

