/**
 * TestMovesUsingWs
 ===============================================================
 * Technology-dependent application
 * TODO. eliminate the communication details from this level
 ===============================================================
 */

package adapters;
import java.util.Observable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

//import javax.websocket.*;



 
public class TestMovesUsingVrBasicAdapter  {
    private VrBasicAdapter vr;
	 
    public TestMovesUsingVrBasicAdapter(String addr) {
        CommUtils.outblue("TestMovesUsingWs |  CREATING ..." + addr);  
        vr = VrBasicAdapter.create(addr);
    }

    public void doJob() throws Exception {
    	vr.halt();
    	int n = 0;
     	for( int i=1; i<=4; i++) {
     		boolean r = vr.step(350);
     		if( !r ) break;
     		else n++;
     	}
     	CommUtils.outblue("n=" + n +"/" +4);
     	vr.turnLeft();
     	n=0;
    	for( int i=1; i<=6; i++) {
     		boolean r = vr.step(350);
     		if( !r ) break;
     		else n++;
    	}
    	CommUtils.outblue("n=" + n +"/" +6);
    	//Another step => collsion
    	//vr.forward(350);
    	CommUtils.delay(2000); 
    }
    public void doJob1() throws Exception {
    	vr.halt();
    	vr.turnLeft();
//    	vr.forward(2500);
//    	CommUtils.delay(1000);    	
//    	vr.halt();
    	for( int i=1; i<=6; i++)
    	vr.step(350);
    	vr.turnRight();
    	for( int i=1; i<=4; i++)
    	vr.step(350);
    	CommUtils.delay(2000); 
    }    
/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            TestMovesUsingVrBasicAdapter appl = 
            		new TestMovesUsingVrBasicAdapter("localhost");
            appl.doJob();
        	CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            CommUtils.outred("TestMovesUsingWs | main ERROR: " + ex.getMessage());
        }
    }

}

