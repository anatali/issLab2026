package main.java.javalin; 
import org.json.simple.JSONObject;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
 

public class SistemaSJavalinBetterApplMsgs extends SistemaSJavalinBetter{


	 /*
	 * ----------------------------------------------------
	 * PARTE HTTP  
	 * ----------------------------------------------------
	 */
	 
	    protected double readInputHTTP(JSONObject b) throws NumberFormatException{
	    	CommUtils.outcyan(	"readInputHTTP b=" + b  );
	        String xs = ""+b.get("msgContent");
	        CommUtils.outred(	"readInputHTTP message=" + xs  );
	        double x  = Double.parseDouble(xs);
	        CommUtils.outblue("x="+x  );
	        return x;
    }
    
 
    
/*
 * ----------------------------------------------------
 * PARTE WS 
  * ----------------------------------------------------
 */
    
    protected String curMsgSender = null;
    protected String curMsgid     = null;
    protected IApplMessage reply = null;
    
    protected double readInputWS(String message) {
    	try {
    		CommUtils.outred(	"readInputWS message=" + message  );
			IApplMessage m = ApplMessage.cvtJson(message);
			CommUtils.outred(	"readInputWS message=" + m  );
			double x = Double.parseDouble(m.msgContent());
			curMsgid     = m.msgId();
			curMsgSender = m.msgSender();
			return x;
		}catch (Exception e) {
			CommUtils.outred(	"message not Json. Supposing ApplMessage"  ); //msg(eval,request,client,server,4.0,0)
		    double x = Double.parseDouble(message);   
		    return x;
		}
    }


	
    public static void main(String[] args) {
    	new SistemaSJavalinBetterApplMsgs().configureTheSystem();
     }
}//SistemaSJavalinBetter


 
