/*
outarea.js
*/
const list = document.getElementById("msgslist")

    function addItem(item){
		//var list = document.getElementById("msgslist")	
	    const li = document.createElement('li');
        li.appendChild(document.createTextNode(item))
		list.appendChild(li);			 
    }
	
	function clearOutArea(){
		const lista = document.getElementById('msgslist');  
		lista.innerHTML = ''; // Rimuovi tutti i nodi figli
	}
