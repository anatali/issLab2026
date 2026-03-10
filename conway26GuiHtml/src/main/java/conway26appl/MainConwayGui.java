package conway26appl;
import conway.io.IoJavalin;
//import main.java.conway.devices.OutInWs;
//import main.java.conway.domain.*;
import unibo.basicomm23.utils.CommUtils;
public class MainConwayGui  {
   	private IoJavalin server = new IoJavalin();
  	
    public static void main(String[] args) {
	    System.out.println("MainConway | STARTS " );  
	    
		var resource = MainConwayGui.class.getResource("/");
		CommUtils.outgreen("DEBUG: La cartella /page si trova in: " + resource);

	    MainConwayGui app = new MainConwayGui();
	   // app.configureTheSystemWithHtmlWs(true);
	    System.out.println("MainConway | ENDS " );  
    }

}