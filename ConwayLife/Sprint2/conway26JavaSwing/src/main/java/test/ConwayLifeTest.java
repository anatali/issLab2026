package main.java.test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import main.java.conway.MainConwayLifeJava;
import main.java.conway.devices.ConwayLifeGridClaude;
import main.java.conway.devices.MockOutdev;
import main.java.conway.domain.GameController;
import main.java.conway.domain.IOutDev;
import main.java.conway.domain.Life;
import main.java.conway.domain.LifeController;
import main.java.conway.domain.LifeInterface;
import unibo.basicomm23.utils.CommUtils;


public class ConwayLifeTest {
	private static final int nRows=20;
	private static final int nCols=20;
	private LifeInterface lifemodel; 

	@Before
	public void setup() {
		System.out.println("ConwayLifeTest | setup");	
		lifemodel = Life.CreateLife(nRows,nCols);
	}

	@After
	public void down() {
		System.out.println("ConwayLifeTest | down");
	}

 
	@Test
	public void testAppl() {
		LifeInterface gameModel       = new Life(nRows, nCols);
		IOutDev outputDevice          = new ConwayLifeGridClaude();
		GameController lifeController = new LifeController(gameModel, outputDevice);
		int genTime                   = lifeController.getGenTime();
        lifeController.switchCellState(2, 1);
        lifeController.switchCellState(2, 2);
        lifeController.switchCellState(2, 3);
        lifeController.onStart();
        //gameModel.nextGeneration();
        int nstep = 4;
        int delay = genTime * nstep;
        lifeController.onStart();
        CommUtils.delay(delay);
        lifeController.onStop();
        assertTrue( lifeController.numEpoch() == (nstep-1) );
        lifeController.onClear();
        outputDevice.close();
        assertTrue( lifeController.numEpoch() == 0 );
        
//        MainConwayLifeJava app = new MainConwayLifeJava();
//        app.configureTheSystemWitMockOutdev();
	}
	
	
//	@Test
//	public void testOscilla() {
//		System.out.println("testOscilla ---------"  );
//		// Configurazione orizzontale
//	    lifemodel.setCell(2, 1, true); 
//	    lifemodel.setCell(2, 2, true);
//	    lifemodel.setCell(2, 3, true);
//	    System.out.println("testOscilla | Stato Iniziale:\n" + lifemodel.getGrid());
//
//	    lifemodel.nextGeneration();
//	    System.out.println("testOscilla | after 1 gen:\n" + lifemodel.getGrid());
//	    // Verifica che sia diventato verticale
//	    assertTrue(lifemodel.isAlive(1, 2)); 
//	    assertTrue(lifemodel.isAlive(2, 2));
//	    assertTrue(lifemodel.isAlive(3, 2));
//	    assertFalse(lifemodel.isAlive(2, 1));
//
//	    lifemodel.nextGeneration();
//	    System.out.println("testOscilla | after 2 gen :\n" + lifemodel.getGrid());
//	    // Verifica che sia tornato orizzontale (Periodo 2)
//	    assertTrue(lifemodel.isAlive(2, 1));
//	    assertTrue(lifemodel.isAlive(2, 2));
//	    assertTrue(lifemodel.isAlive(2, 3));
//	}	

	
}
