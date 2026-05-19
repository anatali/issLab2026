 

 
	/*
	 *  0 : UNKNOWN
	 *  1 : free
	 *  X : obstacle 
	 *  r : robot
	 */
package location;
	import java.io.Serializable;
	import location.RoomMap.cellvalue;
	import location.RoomMap.Direction;
 
	/**
	* Interfaccia IRoomMap
	* Definisce i metodi per l'interazione con una mappa bidimensionale.
	* 
	* Estende Serializable per garantire che ogni implementazione possa 
	* essere trasmessa via rete o salvata in formato binario.
	*/
	public interface IRoomMap extends Serializable {
	// Gestione Robot
	void setRobotAtHome();
	void setRobotPos(int x, int y);
	void setRobotPos(int x, int y, Direction dir, cellvalue v);
	Direction getDir();
	void setDir(Direction dir);
	// Movimento Logico
	void doStep();
	void turnLeft();
	void turnRight();
	// Gestione Celle
	void setCell(int x, int y, cellvalue v);
	void setObstacle(int x, int y);
	void setFree(int x, int y);
	void clear();
	boolean typeOfCell(int x, int y, cellvalue v);
	// Dimensioni
	int getNr();
	int getNc();
	// Export e Persistenza
	String toProlog();
	void saveRoomMap(String fname) throws Exception;
	void showMap();	
}
