/*
iomap.js
*/
const ncell = 20;  //WARNING change in mapstyle.css

const mapContainer = document.getElementById("map");  
mapContainer.innerHTML = '';
    
 	
function createMapRep(){
 for( let i=0; i<ncell; i++ ){
     for( let j=0; j<ncell; j++ ){
        const cellElement = document.createElement("div");
        cellElement.classList.add("cell");
        cellElement.classList.add("dead");
        cellElement.id = `cell(${i},${j})`;
        cellElement.addEventListener('click', function() {
            sendCmdToServer(cellElement.id);  //cell(i,j) 
            //alert(""+cellElement.id );
        });
        mapContainer.appendChild(cellElement);
        //console.log("created cell", cellElement.id);
}//for
}//for
}//createMapRep


    function updateCellColor(newX, newY,color) {
		console.log("updateCellColor cell-" + newX + "-" + newY + " to color " + color );
        var cellxy = document.getElementById(`cell(${newX},${newY})`);
	   console.log("updating  "  + cellxy.classList ); 
      if( cellxy.classList.contains("live") ){
        cellxy.classList.remove("live");
		//if (color == undefined) {cellxy.classList.add("dead");} //just to check
      }
      else if( cellxy.classList.contains("dead") ){
        cellxy.classList.remove("dead");
		//if (color == undefined) {cellxy.classList.add("live");} //just to check
      }
      if (color == 1) {
        cellxy.classList.add("live");
      }else if (color == 0) {
        cellxy.classList.add("dead");
      }else //undefined
	  cellxy.classList.add("dead");

    }
    
console.log("iomap.js loaded " + map);    
createMapRep()
