package conway26LifeganeTcp;


import protoactor26.ProtoActorContext26Tcp;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.utils.CommUtils;

public class MainLifeGame {

	public static void main( String[] args) {
 	    CommUtils.outcyan("MainLifeGame starts");
	    ProtoActorContextInterface context = new ProtoActorContext26Tcp("lifegamecontext", 8045);
	    /* 
	     * -----------------------------------------------------------------------
 	     * WARNING!!!!: in project conway26GuiAlonePactorTcp
	     * set MainGuiServer.workingForPolling = true or false;
	     * -----------------------------------------------------------------------
	     */
	    //new LifeGamePactorPolling("lifegame",context); //MainGuiServer.workingForPolling = true;
	    new LifeGamePactorUsingMqtt("lifegame",context);  //MainGuiServer.workingForPolling = false;

	}
}
