package firefly;

import unibo.basicomm23.utils.CommUtils;

 
public class FFNum {  
     
    public static int ncellend   = 0;
    public static int ncellstart = 0;
    public static int ncellemit  = 0;
    
    public static void incend() {
    	ncellend++;
    	CommUtils.outblue("ncellend="+ncellend);
    }
    public static void incemit() {
    	ncellemit++;
    	//CommUtils.outblue("ncellemit="+ncellemit);
    }

    public static void incstart() {
    	ncellstart++;   	 
    }
 
} 
