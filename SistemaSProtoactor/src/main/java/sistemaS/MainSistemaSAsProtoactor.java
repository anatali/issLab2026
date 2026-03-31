package sistemaS;

import protoactor26.ProtoActorContext26;
 

public class MainSistemaSAsProtoactor {

    public static void main(String[] args) {
    	ProtoActorContext26 context  = new ProtoActorContext26("ctx8050",8050);
    	new SistemaSAsProtoactor("sistemaS",context);
    	new CallerAsProactor("callerOfS",context);
    	//new CallerAsProactor("caller2OfS",context);
     }
}
