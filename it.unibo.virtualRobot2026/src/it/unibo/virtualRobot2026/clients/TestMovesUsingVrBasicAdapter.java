package it.unibo.virtualRobot2026.clients;

import adapters.Rbot26VBasicAdapter;
import unibo.basicomm23.utils.CommUtils;

public class TestMovesUsingVrBasicAdapter {
	private Rbot26VBasicAdapter vr;
	
	
	public TestMovesUsingVrBasicAdapter() {
		vr = Rbot26VBasicAdapter.create("localhost");
		vr.setTrace(true);
	}
 
	public void doJob() throws Exception {
		vr.halt();
		CommUtils.outblue("forward");
          //vr.forward( 2000 );
          vr.backward( 3000 );
//        appl.doCollision();
//        appl.doNotAllowed();
//       appl.doHalt();
 		CommUtils.delay(2000);
		
	}
	
    public static void main(String[] args) throws Exception {
     	CommUtils.aboutThreads("Before start - ");
     	new TestMovesUsingVrBasicAdapter().doJob(); 
     	CommUtils.aboutThreads("At end - ");
     	
    }
}
