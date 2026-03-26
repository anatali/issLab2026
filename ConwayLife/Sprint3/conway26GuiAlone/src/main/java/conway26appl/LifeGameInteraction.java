package conway26appl;
import unibo.basicomm23.interfaces.Interaction;
import main.java.conway.domain.GameController;
import main.java.conway.domain.IOutDev;
import main.java.conway.domain.LifeInterface;
import main.java.conway.domain.Life;
  
/*
 * PREMESSA: lanciare conwayGuiPageServer.MainConwayGui
 */

public class LifeGameInteraction  {  
	 
    public LifeGameInteraction( String name ) throws Exception {
    	setUp();
      }
    
     protected void setUp() throws Exception{
       LifeInterface life   = new Life( 20,20 );            //ncell in iomap.js        
 	   IOutDev iodevgui     = new OutInGuiInteraction(  );   		//dispositivo di output e anche di input 
 	   GameController  cc   = new LifeControllerAdhoc(life, iodevgui) ;   
  	   ((OutInGuiInteraction) iodevgui).setController(cc);          //iniezione del controller nella GUI
 	}
     
  
	   
    public static void main(String[] args) throws Exception {
    	System.out.println("LifeGameInteraction Java.version="+ System.getProperty("java.version"));
    	new LifeGameInteraction("lifectrl");
     }
}

