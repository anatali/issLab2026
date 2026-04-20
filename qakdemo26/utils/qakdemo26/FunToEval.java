package qakdemo26;

import unibo.basicomm23.utils.CommUtils;

public class FunToEval {
	
	public  String eval(double x) {
    	CommUtils.outblue( "FunToEval | eval: " + x);
        if (x > 4.0) {
            CommUtils.outmagenta( "FunToEval | Simulo ritardo per x=" + x);
            CommUtils.delay(3000);
          }
        double R = Math.sin(x) + Math.cos( Math.sqrt(3)*x);
    	return "" + Math.round(R * 100.0) / 100.0;
    }

}
