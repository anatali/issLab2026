package conway26appl.protoactor1;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.conway.domain.LifeInterface;
import main.java.conway.domain.ICell;
import main.java.conway.domain.IGrid;
import main.java.conway.domain.IOutDev;
import protoactor26.AbstractProtoactor26;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.utils.CommUtils;


/*
 * Come fa LifeControllerProtoactor a sapere il nome dell'owner?
 */
public class LifeController1Pattore extends AbstractProtoactor26 {  

    protected ScheduledExecutorService playExecutor = 
				Executors.newSingleThreadScheduledExecutor();
	protected Future<?> playTask;	
 	protected boolean running       = false;
    protected int epoch             = 0;
    protected int generationTime    = 500;
    protected  LifeInterface life   = null;
    protected  OutGuiDev outdev;
     protected IApplMessage connectToGuiServer, displayCmd;
	
	public LifeController1Pattore( String name, LifeInterface game,  ProtoActorContextInterface ctx, OutGuiDev outdev   ) {  
		super(name,ctx);
		this.life          = game; 
		this.outdev        = outdev;
		
		outdev.setController(name);
		CommUtils.outblue(name + " | displayGrid to " + outdev);
//		outdev.displayGrid(life.getGrid());
// 		connectToGuiServer = CommUtils.buildDispatch(name, "connect", "do", "outdev");
//		forward(connectToGuiServer); //outdev Potrebbe non esistere ancora ...
//		
 		displayGrid();
 	}
 	
	/*
	 * ----------------------------------------------------
	 * GESIONE DEI MESSAGGI in modo MSGDRIVEN
	 * ----------------------------------------------------
	 */
	@Override
    protected void elabEvent(IApplMessage ev ) {
		CommUtils.outblue(name + " | elabEvent:" + ev);
		String info =  ev.msgContent();
	}
 
	@Override
    protected IApplMessage elabRequest(IApplMessage req ) {
    	CommUtils.outblue(name + " | elabRequest:" + req);
    	if( ! req.msgSender().equals("caller1")) {
    		CommUtils.outred( name + " REJECTS " +  req.msgId() + " from " + req.msgSender()); 
    		IApplMessage reply = CommUtils.buildReply(name, "answerTo"+req.msgId(), "error(not owner)", req.msgSender());
    		return reply;
    	}
    	if( req.msgId().equals("nepoch")){            
            IApplMessage replyMsg = 
            CommUtils.buildReply(name,req.msgId(),""+ epoch,req.msgSender());
            return replyMsg;
          }else if (req.msgContent().startsWith("cell")) {  //arriva dalla pagina HTML
				String[] coords = req.msgContent().replace("cell(", "").replace(")","").split(",");   
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				switchCellState( x,y );
		        String gridRepFroCanvas = toJson( life.getGrid().repAsBoolArray() );   
		        IApplMessage replyMsg = CommUtils.buildReply(name,req.msgId(),gridRepFroCanvas,req.msgSender());
		        CommUtils.outgreen(name + " | replyMsg " + replyMsg);
		        return replyMsg;				 
		  }else if (req.msgContent().startsWith("clear")) {
		        String gridRepFroCanvas = toJson( life.getGrid().repAsBoolArray() );   
		        IApplMessage replyMsg = CommUtils.buildReply(name,req.msgId(),gridRepFroCanvas,req.msgSender());
		        CommUtils.outgreen(name + " | replyMsg " + replyMsg);
		        return replyMsg;				 			  
		  }else if (req.msgContent().startsWith("start")) {
				CommUtils.outblue(name + " | START");
				onStart();
		        CommUtils.delay(generationTime*2-300);
				String gridRepFroCanvas = toJson( life.getGrid().repAsBoolArray() );   
		        IApplMessage replyMsg = CommUtils.buildReply(name,req.msgId(),gridRepFroCanvas,req.msgSender());
		        CommUtils.outgreen(name + " | replyMsg to start "  );
		        return replyMsg;				 			  			  
		  }else if (req.msgContent().startsWith("stop")) {
			  onStop();
				String gridRepFroCanvas = toJson( life.getGrid().repAsBoolArray() );   
		        IApplMessage replyMsg = CommUtils.buildReply(name,req.msgId(),gridRepFroCanvas,req.msgSender());
		        CommUtils.delay(1000);
		        CommUtils.outgreen(name + " | replyMsg to stop "  );
		        return replyMsg;				 			  			  
		  }
          else{
            IApplMessage replyMsg = 
              CommUtils.buildReply(name,req.msgId(),"requestUnkown",req.msgSender());
            return replyMsg;
          }   	 
    }
	
	 

    protected String toJson(  boolean[][] gridrep ) {
        ObjectMapper mapper = new ObjectMapper();
        try {
          String jsonArrayGridRep = mapper.writeValueAsString(gridrep);
          return jsonArrayGridRep;
        } catch (Exception e) { 	
        	boolean[][] empty = new boolean[0][0];
            return ""+empty;  //Griglia vuota
        }		
      }
	

	/**
	 *
	 */
	@Override
    protected void elabDispatch(IApplMessage m ) {
    	CommUtils.outgreen(name + " | elabDispatch " + m);
    	//Controllo del sender
    	if( ! m.msgSender().equals("caller1")) {
    		CommUtils.outred( name + " REJECTS " +  m.msgId() + " from " + m.msgSender());
    		return;
    	}
    	String payload = m.msgContent();
		if (payload.startsWith("cell")) {  //arriva dalla pagina HTML
				String[] coords = payload.replace("cell(", "").replace(")","").split(",");   
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				switchCellState( x,y );
				displayGrid( );  //For canvas
		}
		else if (payload.equals("stop")) {
			CommUtils.outblue(name + " | stop form " + m.msgSender()  );
			onStop();
		}
		else if (payload.equals("start")) {
			CommUtils.outblue(name + " | START");
			onStart();
		}
		else if (payload.equals("clear")) {
			CommUtils.outblue(name + " | CLEAR");
			onClear();
		}
		else if (payload.equals("exit")) {
			CommUtils.outblue(name + " | cmd EXIT from " + m.msgSender()  );
//			try {
//				connToGui.close();
//			} catch (Exception e) {
// 				e.printStackTrace();
//			}
			outdev.close();
			//CommUtils.delay(1000);
		    System.exit(0);		 
		}
    }

	@Override
	protected void elabReply(IApplMessage req) {
		
	}

	@Override
	protected void proactiveJob() {
		
	}

	/*
	 * ----------------------------------------------------
	 * METODI DI ex GameController
	 * ----------------------------------------------------
	 */
 	
	protected void onStart() {
		//CommUtils.outblue(name + " | onStart running=" + running + " playTask=" + playTask );
		if( running ) return;   //start sent while running
		running = true;	
		
		if(playTask==null) {
			playTask = playExecutor.submit( () -> play() );	
			//simulateVacuumFluctuations(    );
		}
	}
	
	protected void onStop() {
		running = false;	
		if(playTask !=null ) {
			playTask.cancel(true);
			playTask = null;
		}
	}
	protected void onClear() {	
 		onStop();
 		CommUtils.delay(500);   //prima fermo e poi ...
		epoch = 0;
		life.resetGrids();
		displayGrid( ); //For canvas
	}

	protected  void switchCellState(int x, int y) { //synchronized??
		ICell c = life.getCell(x, y); 
		c.switchCellState( );   
	}
	

	protected void displayGrid(){
		outdev.displayGrid( life.getGrid() );
	}
//	/*
//	 * Per parlare con il protoactor outdev occoore un payload di tipo String (non null)
//	 */
//	protected void displayGrid( IGrid grid ) {
//		ObjectMapper mapper = new ObjectMapper();
//		boolean[][] grids = getGridReAsBoolArrayp(grid,grid.getRowsNum(), grid.getColsNum()) ; //new boolean[20][20];
//		try {
//			String jsonGrid   = mapper.writeValueAsString(grids);
// 			displayCmd        = CommUtils.buildDispatch(name, "display", jsonGrid, "outdev"  );
//			//connToGui.forward( cmdmsg );
// 			forward( displayCmd );
//		} catch (Exception e) {
// 			e.printStackTrace();
//		}
//	}
//	protected boolean[][] getGridReAsBoolArrayp(IGrid grid, int rows, int cols) {
//		//CommUtils.outcyan("              OutInGuiInteraction getGridReAsBoolArrayp " +  rows + " " + cols);
//		boolean[][] simplegrid = new boolean[rows][cols];
//		for (int i = 0; i < rows; i++) {
//			for (int j = 0; j < cols; j++) {
//				simplegrid[i][j] = grid.getCell(i, j).isAlive();
//			}
//		}
//		return simplegrid;
//	}

	/*
	 * ----------------------------------------------------
	 * PROACTIVE PART
	 * ----------------------------------------------------
	 */
	
    protected void play() {  
 		CommUtils.outmagenta( name + " |  play (lay) started ---------------- "); 
		while( running ) {
 			try {
				TimeUnit.MILLISECONDS.sleep(generationTime);
				life.nextGeneration();
				displayGrid(   );
 				CommUtils.outblue("---------Epoch ---- "+epoch++ );
 				//manageStable(life.getGrid());
			} catch (InterruptedException e) {
				CommUtils.outred(name + " | play sleep interrupted");
			}					
		}//while
     }

	/*
	 * ----------------------------------------------------
	 * OPTIONAL PART
	 * ----------------------------------------------------
	 */

	protected int nstable = 0;

	protected void manageStable(   ) {
//		if( (life.gridEmpty() || life.gridStable()) && nstable++ > 15) {
//    		running = false;
//    		String reason = gridEmpty ? "empty" : "stable";
//    		String outInfo = "lfctrl: GAME ENDS after " + epoch + 
//    				" Epochs since:"+ reason;
//    		CommUtils.outyellow(outInfo);
//    		nstable = 0;
//    		onStop();
//		}
	}

}
