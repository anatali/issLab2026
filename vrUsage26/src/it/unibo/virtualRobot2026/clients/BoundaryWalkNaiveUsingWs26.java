/**
 * BoundaryWalkNaiveUsingWs
 ===============================================================
 * Technology-dependent application
 * TODO. eliminate the communication details from this level
 ===============================================================
 */

package it.unibo.virtualRobot2026.clients;
import javax.websocket.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

import java.net.URI;
import java.util.Observable;

//See https://www.baeldung.com/java-websockets

@ClientEndpoint
public class BoundaryWalkNaiveUsingWs26 implements IObserver{
    private Session userSession      = null;
    private  JSONParser simpleparser = new JSONParser();
    private long startTime;
    private long totalDuration = 0;
    private int count=0;
	 private  String turnrightcmd  = "{\"robotmove\":\"turnRight\", \"time\": \"300\"}";
	 private  String turnleftcmd  = "{\"robotmove\":\"turnLeft\", \"time\": \"300\"}";
	 private  String forwardcmd   = "{\"robotmove\":\"moveForward\", \"time\": \"4000\"}";
	 private  String backwardcmd  = "{\"robotmove\":\"moveBackward\", \"time\": \"3000\"}";
	 private  String haltcmd      = "{\"robotmove\":\"alarm\", \"time\": \"10\"}";
	 private  String forwardlongcmd   = "{\"robotmove\":\"moveForward\" , \"time\":\"4000\"}";

	 private Interaction wsconn;	 
    public BoundaryWalkNaiveUsingWs26(String addr) {
            CommUtils.outblue("ClientNaiveUsingWs |  CREATING ..." + addr);
            try {
				wsconn = WsConnection.create(addr, "eval", this);
				//((Connection) wsconn).setTrace(true);
			} catch (Exception e) {				 
				e.printStackTrace();
			}
 
    }

 
    protected void halt(){
        callWS( haltcmd );CommUtils.delay(30);
    }
//    /*
//     * forwardlongcmd provoca sempre una collisione
//     * turnleftcmd ha sempre successo (dopo 300 msec)
//     */
//    @OnMessage
//    public void onMessage(String message)  {
//        try {
//            long duration = System.currentTimeMillis() - startTime;
//            totalDuration += duration;
//            CommUtils.outyellow("onMessage | message:" + message
//                    + " duration=" + duration + " totalDuration=" + totalDuration);
//            JSONObject jsonObj = (JSONObject) simpleparser.parse(message);
//            //CommUtils.outblue("ClientNaiveUsingWs | jsonObj:" + jsonObj);
//            if (jsonObj.get("endmove") != null ) {
//                boolean endmove = jsonObj.get("endmove").toString().contains("true");
//                String  move    = (String) jsonObj.get("move") ;
//                //CommUtils.outyellow("onMessage | " + move + " endmove=" + endmove + " duration="+duration + " count=" + count);
//                if( ! endmove ){ //forward failed since collisiom
//                    count++;
//                    callWS(  turnleftcmd  );//CommUtils.delay(350);
//                }else //turnLeft completed
//                if( count <= 4   ) callWS(  forwardlongcmd  );
//            } else if (jsonObj.get("collision") != null ) {
//                String move   = (String) jsonObj.get("collision");
//                String target = (String) jsonObj.get("target");
//                //CommUtils.outgreen("onMessage |  " + "collision move=" + move + " target=" + target + " duration="+duration + " count=" + count);
//                halt(); //Forza l'emissione di {"endmove":"false","move":"moveForward-collision"}
//            }
//            /*else if (jsonObj.get("sonarName") != null ) { //JUST to show ...
//                String sonarName = (String) jsonObj.get("sonarName") ;
//                String distance  = jsonObj.get("distance").toString();
//                CommUtils.outgreen("onMessage | JUST to show: sonarName=" + sonarName + " distance=" + distance);
//            }*/
//        } catch (Exception e) {
//        	CommUtils.outred("onMessage " + message + " ERROR:" +e.getMessage());
//        }
//    }

    protected void doJob(String message) {
	   try {
	      long duration = System.currentTimeMillis() - startTime;
	      totalDuration += duration;
	      CommUtils.outmagenta("doJob | message:" + message
	              + " duration=" + duration + " totalDuration=" + totalDuration);
	      JSONObject jsonObj = (JSONObject) simpleparser.parse(message);
	      //CommUtils.outblue("ClientNaiveUsingWs | jsonObj:" + jsonObj);
	      if (jsonObj.get("endmove") != null ) {
	          boolean endmove = jsonObj.get("endmove").toString().contains("true");
	          String  move    = (String) jsonObj.get("move") ;
	          //CommUtils.outyellow("onMessage | " + move + " endmove=" + endmove + " duration="+duration + " count=" + count);
	          if( ! endmove ){ //forward failed since collisiom
	              count++;
	              callWS(  turnleftcmd  );//CommUtils.delay(350);
	          }else //turnLeft completed
	          if( count <= 4   ) callWS(  forwardlongcmd  );
	      } else if (jsonObj.get("collision") != null ) {
	          String move   = (String) jsonObj.get("collision");
	          String target = (String) jsonObj.get("target");
	          //CommUtils.outgreen("onMessage |  " + "collision move=" + move + " target=" + target + " duration="+duration + " count=" + count);
	          halt(); //Forza l'emissione di {"endmove":"false","move":"moveForward-collision"}
	      }
	      /*else if (jsonObj.get("sonarName") != null ) { //JUST to show ...
	          String sonarName = (String) jsonObj.get("sonarName") ;
	          String distance  = jsonObj.get("distance").toString();
	          CommUtils.outgreen("onMessage | JUST to show: sonarName=" + sonarName + " distance=" + distance);
	      }*/
	  } catch (Exception e) {
	  	CommUtils.outred("onMessage " + message + " ERROR:" +e.getMessage());
	  }
	    	
    }
    protected void callWS(String msg )   {
        CommUtils.outblue("ClientNaiveUsingWs | callWS " + msg  );
        if( ! msg.contains("alarm")) startTime=System.currentTimeMillis();
        try {
			wsconn.forward(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
/*
BUSINESS LOGIC
*/
 	/*
	 * Dopo il primo comando, opera onMessage
	 */
	
	public void walkAtBoundary() {
        CommUtils.waitTheUser("walkAtBoundary PUT ROBOT in HOME and hit");
		count   = 1;
		//callWS( haltcmd );CommUtils.delay(30); //TO avoid notallowed
		callWS(  forwardlongcmd  );  //deve terminare con una collisione anche se dura di più
		//CommUtils.waitTheUser("HIT TO END");
		CommUtils.delay(20000);
 	}

    /*
     * IObserver
     */

	@Override
	public void update(Observable o, Object arg) {
 		update(arg.toString() );
	}

	@Override
	public void update(String message) {
		CommUtils.outgreen("CallerServerInteraction | update elabora: " + message);
		doJob( message );
	}    

/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            BoundaryWalkNaiveUsingWs26 appl = new BoundaryWalkNaiveUsingWs26("localhost:8091");
            //appl.doBasicMoves();
            //appl.doTestCollision();
            appl.walkAtBoundary();
      		CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            CommUtils.outred("ClientNaiveUsingWs | main ERROR: " + ex.getMessage());
        }
    }
}

