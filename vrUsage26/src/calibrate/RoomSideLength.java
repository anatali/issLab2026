/**
 * RoomSideLength.java
 * 
 * Misura i lati della stanza in unità robotiche
 * 
 * USA: RobotObj26  (robot DRR come POJO)
 * 
 * TODO: Analizzare logs/vrusage26.log
 */

package calibrate;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import robots.RobotObj26;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserverMsg;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.LogUtils;
 
   
public class RoomSideLength implements IObserverMsg {
    private RobotObj26 robot;
    private int stepTime    = 345;   //sonar at 0.19
    private int stepOkTime  = 0;  	//set by update when step fails
    private String logFName = "vrusage26.log"; //see logback.xml
    private LogUtils log    = new LogUtils("sidelength");
  
    RoomSideLength(String addr) {
        CommUtils.outblue("TestMovesUsingWs |  CREATING ..." + System.getProperty("user.dir"));  
        robot = RobotObj26.create(addr,this,logFName);
        log.clearlog("logs/"+logFName);   
     }
      
    protected void askUser() {
    	CommUtils.waitTheUser("PUT ROBOT in HOME and hit");
    }
    
    public void doJob() throws Exception {
    	measureHorizontalSide();
    }
    
    public int measureHorizontalSide() throws Exception {
    	int n    = 0;
    	int maxn = 8;  //penso che 8 step vadano oltre 
    	//askUser();
    	CommUtils.outblue("measureHorizontalSide ... "  );
    	log.append( "measureHorizontalSide "  );

    	robot.halt();
      	robot.turnLeft();
     	for( int i=1; i<=maxn; i++) {   
     		boolean r = robot.step(stepTime);
     		if( !r ) { 
     			CommUtils.outcyan("measureHorizontalSide | step failed after:" + stepOkTime + " vs stepTime=" + stepTime);    		    
     		   	if( backtogrid(stepOkTime) ) n++;
    			break;
     		}
     		else n++;
     		CommUtils.delay(500);  //avoid to go too fast
    	}
    	CommUtils.outblue("..... n=" + n +"/" +maxn);
    	return n;
    }
        
    protected boolean backtogrid(int stepOkTime) throws Exception {
     		robot.halt();
     		int dt = stepOkTime-7; //7 is an internal sensing delay
    		if( dt < 0 ) throw new Exception( "Time values inconsistents");  //NON DOVREBBE MAI ESSERE
    		if( dt > 300 ) return true;;
    		CommUtils.outred("RIPOSIZIONO: backward dt=" +dt);
     		log.append( "back "+dt );
    		robot.backward(dt);
    		return false;   	
    }
 
	@Override
	public  void update(IApplMessage info) {
    	//CommUtils.aboutThreads("handleWEnvInfo ");
    	CommUtils.outmagenta("RoomSideLength update |  info=" + info  );
    	log.append( info.toString() );
    	if( info.msgId().equals("stepFail")) {
    		String payload = info.msgContent();
    		Struct pt      = (Struct) Term.parse(payload);
    		stepOkTime     = Integer.parseInt( pt.getArg(0).toString() );
    	}
 	}   
/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            RoomSideLength appl = new RoomSideLength("localhost");
            appl.doJob();
        	CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            CommUtils.outred("TestMovesUsingWs | main ERROR: " + ex.getMessage());
        }
    } 

}

