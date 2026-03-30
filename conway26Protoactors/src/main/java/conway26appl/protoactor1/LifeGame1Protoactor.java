package conway26appl.protoactor1;
import unibo.basicomm23.utils.CommUtils;
import protoactor26.ProtoActorContext26;
import protoactor26.ProtoActorContextInterface;
import main.java.conway.domain.*;
/*
 * PREMESSA: lanciare conwayGuiAlone
 */

public class LifeGame1Protoactor  {  
	 private String name;
	 
    public LifeGame1Protoactor( String name, int port) throws Exception {
    	this.name = name;
     	setUpWithPactor(port);
      }
      
     protected void setUpWithPactor(int port) {
    	 ProtoActorContextInterface ctx  = new ProtoActorContext26("ctx8070",port);
         LifeInterface life              = new Life( 20,20 );            //ncell in iomap.js        
         OutGuiDev outdev                = new OutGuiDev();    
  	     new LifeController1Pattore( name,life,ctx,outdev ) ;   
   }
   
    public static void main(String[] args) throws Exception {
    	System.out.println("LifeGame1Protoactor Java.version="+ System.getProperty("java.version"));
    	new LifeGame1Protoactor("lifectrl",8070);
     }
}

