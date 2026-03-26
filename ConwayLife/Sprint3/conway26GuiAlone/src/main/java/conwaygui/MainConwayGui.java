package conwaygui;
import conway.io.IoJavalin;
import unibo.basicomm23.utils.CommUtils;

public class MainConwayGui  {

   	
   	public MainConwayGui() {
	    CommUtils.outgreen("MainConway | STARTS " );  
  		new IoJavalin("guiserver");
   	}
  	
    public static void main(String[] args) {
	    
		var resource = MainConwayGui.class.getResource("/page");
		CommUtils.outgreen("DEBUG: La cartella /page si trova in: " + resource);

	    MainConwayGui app = new MainConwayGui();
	    CommUtils.outgreen("MainConway | ENDS " );  
    }

}