package location;

import location.RoomMap.cellvalue;

public interface IRoomMap {
	/*
	 *  0 : UNKNOWN
	 *  1 : obstacle
	 *  2 : free
	 *  3 : robot
	 */
	 int getNr();
	 int getNc();
	 void clear();
	 boolean typeOfCell( int x, int y, cellvalue v );
	 void setCell( int x, int y, cellvalue v );
	 void setCellClean( int x, int y  );
	 void setRobot( int x, int y  );
	 void setFree( int x, int y  );
	 void setObstacle( int x, int y  ) ;
	 String toProlog();
	 void showMap();
	 void setPos(int x, int y, cellvalue v);
}
