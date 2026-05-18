/**
 * TuneSteptime.java
 * 
 * Esperimenti per definire il valore di stepTime in modo che VirtualRobo26
 * si muova per uno spazio uguale alla sua dimensione 
 * (diametro R del cerchio in cui è inscrivibile)
 * 
 * USA: RobotObj26  (robot DRR come POJO)
 * 
 * TODO: Analizzare logs/vrusage26.log
 */

package calibrate;
import robots.RobotObj26;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IObserverMsg;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.LogUtils;
 
  
public class TuneSteptime implements IObserverMsg {
    private RobotObj26 robot;
    private int stepTime    = 345;   //sonar at 0.19
    private String logFName = "vrusage26.log"; //see logback.xml
    private LogUtils log    = new LogUtils("tune");
	 
    public TuneSteptime(String addr) {
        CommUtils.outblue("TestMovesUsingWs |  CREATING ..." + System.getProperty("user.dir"));  
        robot = RobotObj26.create(addr,this,logFName);
        log.clearlog(logFName);
     }
     
    public void tuneStepTime() throws Exception {
    	/*
    	 * Pongo il sonar sulla scena (position=0.20) 
    	 * in modo che risulti adiacente al DDR  
    	 */
//    	askUser(); //Intergasco con l'utente umano
        log.info( "start tuning" );
        robot.halt();     		//elimino mosse precedenti in corso
    	robot.turnLeft(); 		//rotazione con comando SINCRONO (300 msec percecpito 320)
//       	vr.turnRight(); 	//rotazione con comando SINCRONO (300 msec percecpito 320)
//       	vr.turnLeft(); 		//rotazione con comando SINCRONO (300 msec percecpito 320)
    	stepTime = 345;     //valore di tentativo
    	robot.step(stepTime);
    	/*
    	 * Verifico che il DDR si sia spostato all'inizio del sonar
    	 * Se no, modifico il valore di stepTime e riprovo
    	 * 
    	 * Osservo i messaggi che RobotObj26 invia ad update,
    	 * tra cui il tempo calcolato dello step
    	 * msg(vrinfo,event,vradpt,none,vrinfo(step,365),0)
    	 * 
    	 * VEDO che ci sono sempre CIRCA 20 msec in più
    	 * -------------------------------------------------------------
    	 * WARNING: non posso usare per la calibrazione il fatto 
    	 * che si manifesti (metodo update) il messaggio 
    	 * msg(sonardata,event,vradpt,ANY,sonar(4),0)
		 * perchè ciò accade prima del passo completo
		 * 
		 * -------------------------------------------------------------
		 * SI OSSERVI il risultato di CommUtils.aboutThreads
    	 */
    }
    
    protected void askUser() {
    	CommUtils.waitTheUser("PUT ROBOT in HOME and hit");
    }
    
  
	@Override
	public synchronized void update(IApplMessage info) {
     	CommUtils.outmagenta("update |  info=" + info  );
    	log.append( info.toString() );
  	}   
/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            TuneSteptime appl = new TuneSteptime("localhost");
            appl.tuneStepTime();
            //appl.doJob();
        	CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            CommUtils.outred("TestMovesUsingWs | main ERROR: " + ex.getMessage());
        }
    }

}

