package adapters;


import org.json.simple.JSONObject;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.basicomm23.ws.WsConnection;

/*
 * ------------------------------------------------------------------------
 * Supporto per un Actor (owner) che deve comunicare con il VirtualRobot23
 * 
 * Apre una connessione wsconn su WebSocket  con il VirtualRobot23
 * ed opera come  OBSERVER su questa connessione
 * 
 * TRASFORMA le informazioni ricevute su wsconn in eventi
 *      sonardata : sonar(D)
 * o in messaggi verso owner
 *      vrinfo : vrinfo(A,B)  //MOVE,ENDMOVE | elapsed,collision | obstacle,unknown
 
 * Risponde alla request step, sulla base della specifica:
 * 
 *   Request step       : step(TIME)   
 *   Reply stepdone     : stepdone(V)                 for step
 *   Reply stepfailed   : stepfailed(DURATION, CAUSE) for step
 * 
 * ------------------------------------------------------------------------
*/
public class VrBasicAdapter extends ApplAbstractObserver implements IVrobotLLMoves {
    protected String vitualRobotIp = "localhost";
    protected Interaction conn;
    protected int elapsed             = 0;     //modified by update
    protected String asynchMoveResult = null;  //for observer part
    protected int threadCount = 1;
    protected String toApplMsg   ;
    protected boolean tracing         = false;
    protected boolean doingStepSynch  = false;
    protected boolean doingStepAsynch = false;

    //Factory method
    public static VrBasicAdapter create( String vitualRobotIp ) {
    	return new VrBasicAdapter( vitualRobotIp  );
    }
    
    //Constructor
    public VrBasicAdapter(String vitualRobotIp) {
    	connect(vitualRobotIp );
    }
    
    protected void connect(String vitualRobotIp) {
    	this.vitualRobotIp = vitualRobotIp;
         this.conn = 
        	ConnectionFactory.createClientSupport(ProtocolType.ws,vitualRobotIp+":8091","");
        //SET itself as ath observer over the WSconnection
        ((WsConnection) conn).addObserver(this);
             toApplMsg = "msg(vrinfo, event, support, none, CONTENT, 0)";
            //.replace("RECEIVER","alien");       	
       CommUtils.outyellow("     VRADPT | CREATED in " + Thread.currentThread().getName());
    }
    
    public void setTrace(boolean v){
        tracing = v;
    }
    public Interaction getConn() {
        return conn;
    }
 
    @Override
    public void turnLeft() throws Exception {
        requestSynch(VrobotMsgs.turnleftcmd);
    }

    @Override
    public void turnRight() throws Exception {
        requestSynch(VrobotMsgs.turnrightcmd);
    }

    @Override
    public void forward(int time) throws Exception {
    	if( tracing ) 
    		CommUtils.outgreen("     VRADPT | forward " + time);
        startTimer();
        String forwardMsg = VrobotMsgs.forwardcmd.replace("TIME", "" + time);
        if( tracing ) 
        	CommUtils.outgreen("     VRADPT | forwardMsg= " + forwardMsg);
        conn.forward( forwardMsg );
    }

    @Override
    public void backward(int time) throws Exception {
        startTimer();
        conn.forward(VrobotMsgs.backwardcmd.replace("TIME", "" + time));
    }

    @Override
    public void halt()   {
    	try {
	        conn.forward(VrobotMsgs.haltcmd);
	        CommUtils.delay(50); //wait for halt completion since halt on ws does not send answer
		}catch(Exception e) {
			CommUtils.outred("halt error (strange....)" + e.getMessage() );
		}
    }
    
    @Override
    public void domove(String move)  throws Exception {
    	switch( move ) {
    		case "w": forward( 450 ); break;
       		case "s": backward( 450 );break;
   	}
    }

/* 
 * ----------------------------------------
 * Observer part   
 * ----------------------------------------
*/
    
    protected int sonarDataNum = 0;
    
    protected void handleSonar(JSONObject jsonObj) {
        if (jsonObj.get("sonarName") != null) { //defensive
         	if( tracing ) 
         		CommUtils.outred("     VRADPT | handleSonar " + jsonObj);
             long d = (long) jsonObj.get("distance") ;
             if( d < 0 ) d = -d;
     		//CommUtils.outred("     VRADPT | handleSonarrrrrrrrrrr d=" + d);
            IApplMessage sonarEvent = CommUtils.buildEvent( "vradpt","sonardata","sonar(" + d + ")");
            emitInfo(sonarEvent);
          }
    }
    
    protected void handleMoveok(String move) {
    	elapsed = getDuration();
        if( tracing )              
        CommUtils.outcyan("     VRADPT | handleMoveok:" + move + " elapsed=" + elapsed );               
       if( ( move.equals("turnLeft") || move.equals("turnRight")) ){
            activateWaiting( move,"true" );
            return;
        }
       if( ! doingStepSynch ) {   //DISPATCH
            String wenvInfo = toApplMsg.replace("wenvinfo","vrinfo") 
                    .replace("CONTENT", "vrinfo(" + move + ", elapsed)");
            IApplMessage msg = new ApplMessage(wenvInfo);
            //sendInfo(msg);
            emitInfo(msg);
       }else {  //move is a forwardcmd for step synch
            activateWaiting(move,"true" );
       }        
    }
    
    protected void handleMoveko(String move) {
    	elapsed = getDuration();
        if( tracing )              
        	CommUtils.outblue("     VRADPT | handleMoveKO:" + move + " elapsed=" + elapsed );               
    	if (move.contains("collision")) {
            if(  ! doingStepSynch ) {  
                 String wenvInfo = "msg(vrinfo, event, support, none, CONTENT, 0)"
                         .replace("wenvinfo", "vrinfo")  
                         .replace("CONTENT","vrinfo(" + elapsed + ", collision )");
                 IApplMessage msg = new ApplMessage(wenvInfo);   
                 //sendInfo(msg); 
                 //CommUtils.outblack("     VRADPT | handleMoveKO msg:" + msg);
                 emitInfo(msg);
            } else {
            	//CommUtils.outred("     VRADPT | handleMoveKO:" + move + " elapsed=" + elapsed );     
                IApplMessage collisionEvent = CommUtils.buildEvent("vrll24","obstacle",move );
                emitInfo(collisionEvent);
            }
            activateWaiting(move,"false"  );
        }    	
    }
    
    protected void handleCollision( String cause) {
    	halt(); //interrompe la move che provocato la collision
        //CommUtils.outred( "     VRADPT | handleCollision:"   );               
        IApplMessage collisionEvent = CommUtils.buildEvent(
                "vrll24","vrinfo","vrinfo(obstacle_XXX,collision)".replace("XXX", cause) );
        //CommUtils.outred("     VRADPT | emit " + collisionEvent);
        emitInfo(collisionEvent);
    }
    
    protected boolean checkMoveResult(JSONObject jsonObj) {
    	//CommUtils.outyellow("checkMoveResult: " + jsonObj);
        boolean moveresult= jsonObj.get("endmove").toString().contains("true");
        return moveresult;   	
    }
    
    @Override
    public void update(String info) {
         try {            
            JSONObject jsonObj = CommUtils.parseForJson(info);
            
            //if( tracing )              
            CommUtils.outgreen(
                "     VRADPT | update:"  
                        + " jsonObj=" + jsonObj + " doingStep=" + doingStepSynch
                        + " " + Thread.currentThread().getName());    //Grizzly            
            if (jsonObj == null) {
            	CommUtils.outred("     VRADPT | update ERROR Json:" + info);
                return;
            }            
            if (jsonObj.get("endmove") != null) {
            	
            	String move        = jsonObj.get("move").toString();
                boolean moveresult = checkMoveResult(jsonObj);
                if (moveresult) {
                	handleMoveok(  move );
                    return;
                } 
                 else {
                	handleMoveko(  move );
                	 return;
                }
              
            }//endmove!=null
            if (jsonObj.get("collision") != null) {
            	String cause = jsonObj.get("target").toString();
            	handleCollision(cause);
               return;
            }          	 
            if (info.contains("_notallowed")) {
                CommUtils.outred("     VRADPT | update WARNING!!! _notallowed unexpected in " + info);
                halt();
                return;
            }
            if (jsonObj.get("sonarName") != null) {
            	//if( sonarDataNum++ == 0 )
            	handleSonar(jsonObj);  //potrebbe entrare in loop 
                return;
            } 
        } catch (Exception e) {
            CommUtils.outred("     VRADPT | update ERROR:" + e.getMessage());
        }
    }

    /*
     * --------------------------------------------
     * Timer part
     * --------------------------------------------
     */
    private Long timeStart = 0L;

    public void startTimer() {
        elapsed = 0;
        timeStart = System.currentTimeMillis();
    }

    public int getDuration() {
        long duration = (System.currentTimeMillis() - timeStart);
        return (int) duration;
    }

/*
 * --------------------------------------------
 * The synch Step moves
 * --------------------------------------------
 */

    @Override
    public boolean step(long time) throws Exception {
        doingStepSynch = true;
        String cmd    = VrobotMsgs.forwardcmd.replace("TIME", "" + time);
        String result = requestSynch(cmd);
        doingStepSynch = false; 
        return result.contains("true");
    }

 

    protected String requestSynch(String msg) throws Exception {
        asynchMoveResult = null;
        //Invio fire-and.forget e attendo modifica di  moveResult da update
        startTimer();
        if( tracing ) 
        	CommUtils.outyellow("     VRADPT | requestSynch " + msg);
        conn.forward(msg);
        String result = waitForResult();
        if( tracing ) 
        		CommUtils.outyellow("     VRADPT | requestSynch result=" + result);
        return result;  //lo dovrebbe sbloccare il modello qak
    }
    

    protected String waitForResult() throws Exception {
        synchronized (this) {
            while (asynchMoveResult == null) {
                wait();
            }
            return asynchMoveResult;
        }
    }
    protected void activateWaiting(String move, String endmove){
        if( tracing ) CommUtils.outmagenta("     VRADPT | activateWaiting ... " + endmove);
        synchronized (this) {  //sblocca request sincrona per checkRobotAtHome
            asynchMoveResult = endmove;
            notifyAll();
        }
    }

    protected void emitInfo(IApplMessage info) {
    	//if( tracing ) 
    		CommUtils.outmagenta("     VRADPT  | emitInfo " + info );
     }
    
    protected void sendInfo(IApplMessage msg) {
    	 //MsgUtil.sendMsg(msg,owner,null); //null:continuation
    }
    
    /*
     * A main just to test ...
     */
    public static void main(String[] args) throws Exception {
        CommUtils.aboutThreads("Before start - ");
        VrBasicAdapter appl = VrBasicAdapter.create("localhost" );
        CommUtils.aboutThreads("At end - ");
    }
}

