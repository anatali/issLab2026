/*
wscontrol.js
*/
 	var pageId         = "unknown";
	var cmdMsgTemplate = "msg( eval, dispatch, SENDER, lifectrl, CMD, 0 )"
	var opened         = false
	var socketToGui;
	
	function sendCmdToServer(cmd) {
		 //console.log("sendCmdToServer:" + cmd )
		 msg = cmdMsgTemplate.replace("CMD", cmd).replace("SENDER",pageId)
		 //addItem("sendCmdToServer: " + msg + " opened=" + opened);
		 console.log("sendCmdToServer:" + msg )		 
		 //if( opened ) 
			socketToGui.send(msg);
	}
				
 function  initWS(){
 /*1*/	  
      console.log("initWS | window.location.host=" + window.location.host );
	  if( window.location.host =="" ){
		 socketToGui = new WebSocket("ws://localhost:8080/chat");
		 console.log("initWS | socketToGuiiii=" + socketToGui );
		 //socketToGui.send("hello world su chat");
	  }else{
		socketToGui = new WebSocket("ws://"+window.location.host+"/eval");
		//socketToGui.send("hello world su eval");
	  } 

 /*2*/socketToGui.onopen = () => {
     console.log("initWS | Connesso a eval");
	 addItem("initWS | Connesso a chat");
	 opened = true;
	 sendCmdToServer("ready" );
     }

 /*3*/socketToGui.onmessage = (event) => {
         console.log("initWS | onmessage:",event.data);
		 if( event.data.startsWith("ID:")){
			console.log("initWS | onmessage:",event.data);
			pageId= event.data.split(":")[1];
			addItem( "page ID="  + pageId ); 
		 }
		 else if( event.data.startsWith("cell(")){ //deve ricevere da caller
			 //addItem(event.data);
			 coords = event.data.replace("cell(", "").replace(")","").split(",");
			 //addItem(coords);
			 updateCellColor(coords[0],coords[1],coords[2] )  //In iomap.js
		 }else{/*
	         if( event.data == "PING") {
				socketToGui.send("PONG");
			 }*/
			 if( event.data != "PING") addItem( event.data );
		 }
	 }
	 socketToGui.onclose =  function(event){	
		 console.log("initWS | Chiusura connessione ", event);
         addItem("initWS | Chiusura connessione ");
         opened = false;
	 }
 }//initWS

 //addItem("Welcome to conwaygui ....");  
  initWS()
   
 
