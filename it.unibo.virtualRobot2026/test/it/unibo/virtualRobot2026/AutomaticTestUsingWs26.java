/**
 * TestMovesUsingWs
 ===============================================================
 * Technology-dependent application
 * TODO. eliminate the communication details from this level
 ===============================================================
 */

package it.unibo.virtualRobot2026;
import static org.junit.Assert.fail;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;
 



 
public class AutomaticTestUsingWs26 implements IObserver{

	private  JSONParser simpleparser = new JSONParser();
	
	 private  String turnrightcmd  = "{\"robotmove\":\"turnRight\"    , \"time\": \"300\"}";
	 private  String turnleftcmd  = "{\"robotmove\":\"turnLeft\"     , \"time\": \"300\"}";
	 private  String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"1200\"}";
	 private  String backwardcmd  = "{\"robotmove\":\"moveBackward\" , \"time\": \"1300\"}";
	 private  String haltcmd      = "{\"robotmove\":\"alarm\" , \"time\": \"10\"}";

	 private  String forwardlongcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"3000\"}";
//	 private int count = 0;

	 long startTime ;
	 private BlockingQueue<JSONObject> blockingQueue = new LinkedBlockingDeque<>();
	 
	 private Interaction wsconn;
	 
	    @Before
	    public void init(){
	         CommUtils.outmagenta("AutomaticTestUsingWs26 INIT");
	         try {
					wsconn = WsConnection.create("localhost:8091", "eval", this);
					//((Connection) wsconn).setTrace(true);
				} catch (Exception e) {				 
					e.printStackTrace();
				}
	    }

    protected void callWS(String msg )   {
        CommUtils.outyellow("AutomaticTestUsingWs26 | callWS " + msg);
        if( ! msg.contains("alarm")) startTime = System.currentTimeMillis() ;
        try {
			wsconn.forward(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    protected void halt(){
        callWS( haltcmd );
        CommUtils.delay(30);
    }
    
    /*
     * IObserver
     */
    
    /*
     * Observe the WS and put the observed info in the blockingQueue
     */

	@Override
	public void update(Observable o, Object arg) {
 		update(arg.toString() );
	}

	@Override
	public void update(String message) {
		//CommUtils.outgreen("CallerServerInteraction | update elabora: " + message);
		try {
	        JSONObject jsonObj = (JSONObject) simpleparser.parse(message);
	        CommUtils.outmagenta("TestMovesUsingWs | update jsonObj:" + jsonObj);
	        blockingQueue.add( jsonObj );
		}catch (Exception e) {
            CommUtils.outred("update error on:" + message + " | " +e.getMessage());
        }	
	}    
    
	
    @Test
    public void doForwardBqckwardWithHit() {
    	halt();
        String forwardcmd   = "{\"robotmove\":\"moveForward\",\"time\": \"1000\"}";
        startTime = System.currentTimeMillis();
         try {
            callWS(  forwardcmd  );
            JSONObject result0 = blockingQueue.take();
            assert( result0.get("endmove").equals("true") && result0.get("move").equals("moveForward")) ;
            
            callWS(  backwardcmd  );  //hits !
            JSONObject result1 = blockingQueue.take(); 
            CommUtils.outblue("moveBackward result1=" + result1);
            assert( result1.get("collision").toString().contains("moveBackward")) ;
            
            //after 300 there is endmove
            JSONObject result2 = blockingQueue.take(); 
            CommUtils.outblue("moveBackward result2=" + result2);
            assert( result2.get("endmove").toString().contains("false")) ;
          
            
        } catch (Exception e) {
        	CommUtils.outred("doForwardBqckwardWithHit error   | " +e.getMessage());
            fail("doForwardBqckwardWithHit");
        }
    }




 
}

