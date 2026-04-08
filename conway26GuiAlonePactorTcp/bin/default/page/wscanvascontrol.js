/*
wscanvascontrol.js
*/
// --- CONFIGURAZIONE --- file:///C:/Didattica2026/protobook/src/main/java/conway/io/LifeIOCanvas.html
const CELL_SIZE = 20;
const ROWS = 20;
const COLS = 20;

const canvas = document.getElementById("gridCanvas");
const ctx = canvas.getContext("2d");
const statusDiv = document.getElementById("status");

var pageId         = "unknown"

var opened = true;

var cmdMsgTemplate = "msg( eval, dispatch, SENDER, lifegame, CMD, 0 )"
var evMsgTemplate  = "msg( eval, event, SENDER, CMD, 0 )"
var reqMsgTemplate = "msg( do, request,  SENDER, lifegame, CMD, 0 )"

canvas.width  = COLS * CELL_SIZE;
canvas.height = ROWS * CELL_SIZE;

// --- WEBSOCKET LOGIC ---
// Se il server è sulla stessa macchina, usa localhost:8080
//const socketToGui = new WebSocket("ws://" + window.location.host + "/eval");
//alert("host="+window.location.host)
//const socketToGui = new WebSocket("ws://localhost:8080/eval");

//OPPURE (per pagina erogata da server stesso)
var socketToGui;
if( window.location.host =="" ) socketToGui = new WebSocket("ws://localhost:8080/eval");
else 	socketToGui = new WebSocket("ws://"+window.location.host+"/eval");
 


socketToGui.onopen = () => { 
    statusDiv.innerText = "STATO: CONNESSO";
    statusDiv.style.color = "#00ff00";
	
	
	//msg = evMsgTemplate.replace("CMD", "canvasready").replace("SENDER","caller1") //TODO caller
	//sendToServer(msg); NON invio event ma sempre dispatch al server
	opened = true;
	sendCmdToServer("canvasready" );
 };

socketToGui.onclose = () => {
    statusDiv.innerText = "STATO: CONNESSIONE PERDUTA (CODICE 1006?)";
    statusDiv.style.color = "#ff0000";
};

socketToGui.onmessage = (event) => {
	//console.log("pageglobal | onmessageeeeee:",event.data);
	if( event.data.startsWith("ID:")){
		console.log("pageglobal | onmessage ID:",event.data);
		pageId= event.data.split(":")[1];
		//addItem( "page ID="  + pageId ); 
	}
	else if( event.data.includes("[[")) {
		console.log("pageglobal | onmessage RICEVE [[" );
		//if( event.data.includes("true")) 
		//console.log(""+event.data)
		const grid = JSON.parse(event.data);
		requestAnimationFrame(() => draw(grid));
	}
	else  {
		console.log("pageglobal |  send ", event.data );
		sendCmdToServer(event.data);
    }
};
socketToGui.onclose =  function(event){	
 console.log("pageglobal | Chiusura connessione ", event);
    //addItem("pageglobal | Chiusura connessione eval");
    opened = false;
}


// --- DISEGNO (L'illusione visiva) ---
function draw(grid) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
	const currentRows = grid.length;
	const currentCols = grid[0].length;
	console.log(" " + currentRows + " " + currentCols)
    for (let r = 0; r < currentRows; r++) {
        for (let c = 0; c < currentCols; c++) {
			ctx.fillStyle = grid[r][c] ? "#ff0000" : "#00ff00";
			ctx.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
       }
    }
}

// --- INPUT (Interazione Alieno -> Server) ---
canvas.addEventListener("click", (event) => {
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    
    const col = Math.floor(x / CELL_SIZE);
    const row = Math.floor(y / CELL_SIZE);
    
    // Invia al server la coordinata da invertire PROPOSTO DA Gemini
    //const msg = { type: "TOGGLE", r: row, c: col };
    //socketToGui.send(JSON.stringify(msg));
	
	const msg = "cell("+row+","+col+")"
	
	sendCmdToServer(msg);
});

function sendAction(action) {
    socketToGui.send(JSON.stringify({ type: "COMMAND", val: action }));
}

function sendCmdToServer(cmd){ //la pagina invia  msg( eval, dispatch, SENDER, lifectrl, CMD, 0 )
	console.log("sendCmdToServer:" + cmd )
	msg = cmdMsgTemplate.replace("CMD", cmd).replace("SENDER",pageId) 
	//msg = reqMsgTemplate.replace("CMD", cmd).replace("SENDER",pageId)
	sendToServer( msg )		
}

function sendToServer(cmd) {
	 console.log("sendToServer:" + cmd + " opened=" + opened)
	 if( opened )  socketToGui.send(msg);
}