package main.java.conway.devices;

import main.java.conway.domain.Cell;
import main.java.conway.domain.Grid;
import main.java.conway.domain.IOutDev;
import unibo.basicomm23.utils.CommUtils;

public class MockOutdev implements IOutDev{

	@Override
	public void display(String msg) {
		CommUtils.outblue(msg);
		
	}

	@Override
	public void displayCell(Cell cell, Grid grid) {
		//CommUtils.outcyan("x="+cell.getX() + "y="+cell.getY() +  cell.getState());
	}
	
	@Override
	public void close() {
		
	}

	@Override
	public void displayGrid(Grid grid) {
 		//grid.printGrid();
	}

}
