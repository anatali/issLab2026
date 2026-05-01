/**
 * TestMovesUsingWs
 ===============================================================
 * Technology-dependent application
 * TODO. eliminate the communication details from this level
 ===============================================================
 */

package it.unibo.virtualRobot2026.clients;
import java.util.Observable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.Connection;
import unibo.basicomm23.ws.WsConnection;

//import javax.websocket.*;



 
public class TestMovesUsingWs26 implements IObserver{
//    private Session userSession      = null;
//    private  JSONParser simpleparser = new JSONParser();
	 private  String turnrightcmd  = "{\"robotmove\":\"turnRight\"    , \"time\": \"300\"}";
	 private  String turnleftcmd  = "{\"robotmove\":\"turnLeft\"     , \"time\": \"300\"}";
	 private  String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"1200\"}";
	 private  String backwardcmd  = "{\"robotmove\":\"moveBackward\" , \"time\": \"1300\"}";
	 private  String haltcmd      = "{\"robotmove\":\"alarm\" , \"time\": \"10\"}";

	 private  String forwardlongcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"3000\"}";
	 private int count = 0;

	 long startTime ;
	 
	 private Interaction wsconn;
	 
    public TestMovesUsingWs26(String addr) {
            CommUtils.outblue("TestMovesUsingWs |  CREATING ..." + addr);            
            try {
				wsconn = WsConnection.create(addr, "eval", this);
				//((Connection) wsconn).setTrace(true);
			} catch (Exception e) {				 
				e.printStackTrace();
			}
    }

    protected void callWS(String msg )   {
        CommUtils.outyellow("TestMovesUsingWs | callWS " + msg);
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
BUSINESS LOGIC
*/
    public void doForward() {
		String forwardcmd   = "{\"robotmove\":\"moveForward\",\"time\": \"1000\"}";
		CommUtils.waitTheUser("doForward (WS): PUT ROBOT in HOME  and hit (forward 1000)");
		startTime = System.currentTimeMillis();
		callWS(  forwardcmd  );
		CommUtils.waitTheUser("Hit to terminate doForward");
		//Per vedere il msg di stato collision e endmove
	}
    
    public void  doCollision() {
    	CommUtils.waitTheUser("doCollision (WS): PUT ROBOT near a wall and hit (forward 3000)");
        //halt(); //To remove pending notallowed
        String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"3000\"}";
        startTime = System.currentTimeMillis();
        callWS(  forwardcmd  );
        CommUtils.waitTheUser("Hit to terminate doCollision");
        //Per vedere il msg di stato collision e endmove
    }

    public void doNotAllowed() {
        CommUtils.waitTheUser("doNotAllowed (WS): PUT ROBOT in HOME and hit (forward 1200 and turnLeft after 400)");
        String forwardcmd   = "{\"robotmove\":\"moveForward\", \"time\":\"1200\"}";
        startTime = System.currentTimeMillis();
        callWS(  forwardcmd  );
        CommUtils.outblue("doNotAllowed (WS): moveForward msg sent"  );
        CommUtils.delay(400);
        CommUtils.outblue("doNotAllowed (WS): Now call turnLeft"  );
        callWS(  turnleftcmd  );
        CommUtils.waitTheUser("doHalt (WS): Hit to terminate doNotAllowed");
    }

    public void doHalt() {
        CommUtils.waitTheUser("doHalt (WS): PUT ROBOT in HOME and hit (forward 3000 and alarm after 1000)");
        String forwardcmd   = "{\"robotmove\":\"moveForward\", \"time\":\"3000\"}";
        callWS(  forwardcmd  );
        CommUtils.outblue("doHalt (WS): moveForward msg sent"  );
        CommUtils.delay(1000);
        callWS(  haltcmd  );
        CommUtils.waitTheUser("doHalt (WS): Hit to terminate doHalt");
    }

    public void doBasicMoves() {
        callWS(  haltcmd ) ; //halt asynch non manda enmove
        CommUtils.delay(20);
        CommUtils.waitTheUser("hit to turn");
 	
		callWS(  turnleftcmd ) ;
		CommUtils.outblue("turnLeft msg sent"  );		
		CommUtils.delay(500);
		
		callWS(  turnrightcmd ) ;
		CommUtils.outblue("turnRight msg sent"  );
		CommUtils.delay(500);

	CommUtils.waitTheUser("hit to forward");
//		//Now the value of endmove depends on the position of the robot
		callWS(  forwardcmd  );
		CommUtils.outblue("moveForward msg sent"  );
		CommUtils.delay(1300);
    CommUtils.waitTheUser("hit to backwardcmd");
		callWS(  backwardcmd );
		CommUtils.outblue("moveBackward msg sent"  );
		CommUtils.delay(1300);
		 //Give time to receive msgs from WEnv
}
    
    /*
     * IObserver
     */

	@Override
	public void update(Observable o, Object arg) {
 		update(arg.toString() );
	}

	@Override
	public void update(String value) {
		CommUtils.outgreen("CallerServerInteraction | update elabora: " + value);
		
	}    
    
/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            TestMovesUsingWs26 appl = new TestMovesUsingWs26("localhost:8091");
//            appl.doForward();
              appl.doCollision();
//              appl.doNotAllowed();
//             appl.doHalt();
       		CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            CommUtils.outred("TestMovesUsingWs | main ERROR: " + ex.getMessage());
        }
    }



 
}

