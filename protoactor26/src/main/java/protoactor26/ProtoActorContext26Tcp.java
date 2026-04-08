package protoactor26;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;

public class ProtoActorContext26Tcp implements ProtoActorContextInterface {
	private String name;
	private int port;
	private Map<String, AbstractProtoactor26> protoactors = new ConcurrentHashMap<>();

	public ProtoActorContext26Tcp(String name, int port) {
		this.name = name;
		this.port = port;
		configureTheSystem();
	}

	@Override
	public void register(AbstractProtoactor26 pactor) {
		CommUtils.outmagenta(name + " ProtoActor26Tcp | registered " + pactor.name + " in " + name);
		protoactors.put(pactor.name, pactor);
	}

	/*
	 * ---------------------------------------------------- CNFIGURAZIONE DEL SERVER
	 * ----------------------------------------------------
	 */
	protected void configureTheSystem() {
		new Thread() {
			public void run() {
				try{
					ServerSocket serverSocket = new ServerSocket(port);
 					CommUtils.outyellow(name + " in ascolto sulla porta " + port);
					while (true) {
						CommUtils.outmagenta(name + " accept connessione "  );
//						try {
							Socket clientSocket = serverSocket.accept();
 							CommUtils.outmagenta(name + " nuova connessione " + clientSocket);
							new Thread(() -> handleConnection(clientSocket)).start();

							/*
							 * Usando clientSocket.getOutputStream(), i dati vengono incapsulati nei
							 * pacchetti TCP che il sistema operativo sa già come instradare verso il client
							 * che ha originato la connessione.
							 * 
							 */
//						} catch (Exception e) {
//							System.out.println(name + " Error: " + e.getMessage());
//						}
					} // while accept
				} catch (Exception e1) {
					System.out.println(name + " Error: " + e1.getMessage());
				}
			}
		}.start();
		CommUtils.outyellow(name + " | ProtoActor26Tcp configureTheSystem done ");

	}// configureTheSystem

	protected void handleConnection(Socket clientSocket) {
		try {
			InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8);
			BufferedReader in = new BufferedReader(reader);

			clientSocket.setTcpNoDelay(true);
			clientSocket.setSoLinger(true, 2);

			// 2. Prepariamo la scrittura (Risposta)
			// Il 'true' abilita l'auto-flush

			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
 			while(true) {
				//System.out.println(name + " INIZIO LETTURA su conn TCP");
				String input = in.readLine();
				//CommUtils.outyellow(name + " input=" + input);
				IApplMessage inMsg = new ApplMessage(input);
				CommUtils.outgreen(name + " | receives " + inMsg.msgId() + " as " + inMsg.msgType());
				handle(clientSocket,out,inMsg) ;
				//System.out.println(name + " FINE LETTURAAAAAAAAAAA");
			}
		} catch (Exception e1) {
			System.out.println(name + " Error: " + e1.getMessage());
		}

	}

	protected void handle(Socket clientSocket, PrintWriter out, IApplMessage msg) {
		//CommUtils.outgreen(name + " | handle  " + msg.msgId());
		IApplMessage answer = elabMsg(msg);
		//CommUtils.outgreen(name + " | handle answer from elabMsg= " + answer);
		if (msg.isRequest() && answer != null) {
//			CommUtils.outgreen(name+" | repliy to:" + answer.msgReceiver() + " outoflush");
			out.println(answer.toString()); // + "\n" NO!!!!
//			try {
//				//Chiudi solo l'output per segnalare al client che hai finito 
//				clientSocket.shutdownOutput();  NON VA BENE
//			} catch (IOException e) {			e.printStackTrace();}
		} 
//		else if (msg.isDispatch()) {
//			//CommUtils.outgreen(name + " | handle dispatch no reply ");
//			return;
//		}
	}

	/*
	 * Individua il protoactor destinatario e gli fa accodare il task appropriato di
	 * elaborazione-messaggio
	 */
	@Override
	public IApplMessage elabMsg(IApplMessage am) {
		//CommUtils.outyellow(name + " | ProtoActor26Tcp elabMsg : " + am.msgId() + " for " + am.msgReceiver());
		String dest = am.msgReceiver();
		AbstractProtoactor26 pactor = protoactors.get(am.msgReceiver());
		//CommUtils.outyellow(name + " | ProtoActor26Tcp finds : " + pactor.name);
		if (pactor != null) {
			IApplMessage answer = pactor.execMsg(am);
			//if( am.isRequest() ) CommUtils.outyellow(name + " elabMsg answer=" + answer);
			return answer;
		} else { // ADDED April 4
			CommUtils.outred(name + " invio a dest remoto NOT POSSIBLE " + am.msgReceiver());
			// return sendToRemoteConnectedContext( am );
			return null;
		}
	}

	/* Utility */
	@Override
	public void emitInfo(IApplMessage event) {
		CommUtils.outcyan(name + "			send to all registered: " + event);
	}

}
