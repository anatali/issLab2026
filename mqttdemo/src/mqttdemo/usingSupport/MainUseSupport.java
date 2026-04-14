package usingSupport;

public class MainUseSupport {

	public static void main(String[] args) { 
		//ReceiverWithCallback receiver = new ReceiverWithCallback("receiver");   
		ReceiverWithListener receiver = new ReceiverWithListener("receiver");
		Sender sender     = new Sender("sender");
	}
}
