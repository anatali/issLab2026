package conway26appl.protoactor0;
import unibo.basicomm23.utils.CommUtils;
import protoactor26.ProtoActorContext26;
import protoactor26.ProtoActorContextInterface;
import main.java.conway.domain.*;
/*
 * PREMESSA: lanciare conwayGuiAlone
 */

public class LifeGame0Protoactors  {  
	 private String name;
	 
    public LifeGame0Protoactors( String name, int port) throws Exception {
    	this.name = name;
     	setUpWithPactor(port);
    }
      
     protected void setUpWithPactor(int port) {
    	 ProtoActorContextInterface ctx  = new ProtoActorContext26("ctx8070",port);
         LifeInterface life              = new Life( 20,20 );            //ncell in iomap.js        
  	     new OutInGuiPattore("outdev",ctx);   //per primo !!!
  	     new LifeController0Pattore( name,life,ctx ) ;   
    	 //No injection di OutInGuiProtoactor in  LifeControllerProtoactor
   }
   
    public static void main(String[] args) throws Exception {
    	System.out.println("LifeGameProtoactors Java.version="+ System.getProperty("java.version"));
    	new LifeGame0Protoactors("lifectrl",8070);
     }
}

