/**
 * BoundaryWithBasicMoves
 *
 * Percorre il perimetro usando forward di RobotObj26
 * 
 * Notare la copia di logback.xml per la esecuzione 
 * con RunAs in eclipse
 * 
 * Notare i threads
 */

package appls;
 
import robots.RobotObj26;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserverMsg;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.LogUtils;
  
public class BoundaryWithBasicMoves implements IObserverMsg {
    private RobotObj26 robot;
	private int n = 0;
    private IApplMessage weninfo;
    private String logFName = "vrusage26.log"; //see logback.xml
    private int longTime = 3500;
    private LogUtils log  = new LogUtils("bws");
 
    public BoundaryWithBasicMoves(String addr) {
        CommUtils.outblue("TestMovesUsingWs |  CREATING ..." + addr);  
        robot = RobotObj26.create(addr, this,logFName);
        //robot.setTrace(true);
        log.clearlog("logs/"+logFName);
        CommUtils.aboutThreads("main");
    }

    //Basato sul basso livello
    public void doJob() throws Exception {
    	askUser();
    	robot.halt();
    	while( n < 4 ) {
    		CommUtils.outblue("forward ..."  );	
    		robot.forward(longTime);    	
    	   	waitEndMove("moveForward_ko");
    		//CommUtils.delay(longTime+100);
		    robot.halt();
		    //CommUtils.outblue("resume (turning) " + weninfo + " n=" +n);		    
			robot.turnLeft();
			log.info("turned when n="+n);
		    CommUtils.outblue("Turned"  );		    
			n++;
    	}
     }
   
    protected void askUser() {
    	CommUtils.waitTheUser("PUT ROBOT in HOME and hit");
    }
    
    protected synchronized void waitEndMove(String value) {
    	weninfo = null;
    	while( weninfo == null )
    		try {
    			wait();
    			if( ! weninfo.msgContent().contains(value)) weninfo=null;
 			} catch ( Exception  e) {
 				e.printStackTrace();
			}
    }
    
  
	@Override
	public synchronized void update(IApplMessage info) {
    	//CommUtils.aboutThreads("update ");
    	CommUtils.outmagenta("handleWEnvInfo |  info=" + info + " longTime="+longTime);
    	if( info.msgId().equals("sonardata"))  return;
	    weninfo = info;
	    notify();  //non fa nulla se nessuno è in wait (quindi event ???)		
	}    
/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            BoundaryWithBasicMoves appl = new BoundaryWithBasicMoves( "localhost" );
            appl.doJob();
            CommUtils.delay(5000);
        	CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            CommUtils.outred("BoundaryUsingVrBasicAdapter | main ERROR: " + ex.getMessage());
        }
    }

 
 


}

