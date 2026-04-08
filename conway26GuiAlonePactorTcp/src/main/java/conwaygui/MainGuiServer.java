package conwaygui;

import protoactor26.ProtoActorContext26Tcp;
import protoactor26.ProtoActorContextInterface;
import unibo.basicomm23.utils.CommUtils;

public class MainGuiServer {
public static final boolean workingForPolling = false;

	public static void main(String[] args) {
		ProtoActorContextInterface context = new ProtoActorContext26Tcp("guiservercontext", 8050);
		CommUtils.outmagenta("------------------------------------------------");
		if(workingForPolling) CommUtils.outmagenta("guiserver working in POLLING mode ");
		else CommUtils.outmagenta("guiserver sending input via  MQTT ");
		CommUtils.outmagenta("------------------------------------------------");
		new GuiServer("guiserver", context);
	}
 
}