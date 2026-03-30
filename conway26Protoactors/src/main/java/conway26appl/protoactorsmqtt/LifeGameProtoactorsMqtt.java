package conway26appl.protoactorsmqtt;
import main.java.conway.domain.LifeInterface;
import protoactor26.ProtoActorContext26;
import protoactor26.ProtoActorContext26Mqtt;
import main.java.conway.domain.Life;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.utils.CommUtils;

/*
 * PREMESSA: lanciare conwayGuiPageServer.MainConwayGui
 */

public class LifeGameProtoactorsMqtt  {  
	 private String name;
	 
    public LifeGameProtoactorsMqtt( String name ) throws Exception {
    	this.name = name;
      	setUpWithPactorMqtt();
      }
      
      protected void setUpWithPactorMqtt() {
         CommUtils.outred("setUpWithPactorMqtt");
    	 ProtoActorContextInterface context = new ProtoActorContext26Mqtt("ctxmqtt", "ctxmqttIn", "guiservermqttIn");
         LifeInterface life                 = new Life( 20,20 );            //ncell in iomap.js    
         CommUtils.outblue("creating LifeControllerProtoactor");
   	     new LifeControllerProtoactorMqtt( name,life,context  ) ;   
    }

	   
    public static void main(String[] args) throws Exception {
    	System.out.println("LifeGameProtoactors Java.version="+ System.getProperty("java.version"));
    	new LifeGameProtoactorsMqtt("lifectrl" );
     }
}

