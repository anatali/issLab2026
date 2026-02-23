package main.java.conway.domain;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Life implements LifeInterface{
	private final int rows;
    private final int cols;
    
    // Due matrici distinte
    private boolean[][] gridA;
    private boolean[][] gridB;
    
 // Un riferimento che punta sempre alla griglia che contiene lo stato attuale
    private boolean[][] currentGrid;
    private boolean[][] nextGrid;
    
   public static LifeInterface CreateGameRules() {
	   return new Life(5, 5); 
	   // Dimensioni di default, possono essere 
	   //lette da un file di configurazione o passate come parametri
   }

    // Costruttore che accetta una griglia pre-configurata (utile per i test)
    public Life(boolean[][] initialGrid) {
    	this.rows = initialGrid.length;
        this.cols = initialGrid[0].length;
        
        // Inizializziamo entrambe le matrici
        this.gridA = new boolean[rows][cols];
        this.gridB = new boolean[rows][cols];
        
        this.gridA = deepCopyJava8(initialGrid);
        this.currentGrid = gridA;
        this.nextGrid    = gridB;   
    }

    // Costruttore che crea una griglia vuota di dimensioni specifiche
    public Life(int rows, int cols) {
    	this.rows = rows;
        this.cols = cols;
        this.gridA = new boolean[rows][cols];
        this.gridB = new boolean[rows][cols];
        this.currentGrid = gridA;
        this.nextGrid    = gridB;   
    }

    // Calcola la generazione successiva applicando le 4 regole di Conway
    public void nextGeneration() {
    	// Applichiamo le regole leggendo da currentGrid e scrivendo in nextGrid
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int neighbors = countNeighborsLive(r, c);
                boolean isAlive = currentGrid[r][c];
                //apply rules
                if (isAlive) {
                    nextGrid[r][c] = (neighbors == 2 || neighbors == 3);
                } else {
                    nextGrid[r][c] = (neighbors == 3);
                }
            }
        }

        // --- IL PING-PONG ---
        // Scambiamo i riferimenti: ciò che era 'next' diventa 'current'
        boolean[][] temp = currentGrid;
        currentGrid      = nextGrid;
        nextGrid         = temp;
        // Nota: non abbiamo creato nuovi oggetti, abbiamo solo spostato i puntatori
    }
    
    protected int countNeighborsLive(int row, int col) {
        int count = 0;
        if (row-1 >= 0) {
        	if( currentGrid[row-1][col] ) count++;
        }
        if (row-1 >= 0 && col-1 >= 0) {
        	if( currentGrid[row-1][col-1] ) count++;
        }
        if (row-1 >= 0 && col+1 < cols) {
        	if( currentGrid[row-1][col+1] ) count++;
        }
        if (col-1 >= 0) {
        	if( currentGrid[row][col-1] ) count++;
         }
        if (col+1 < cols) {
        	if( currentGrid[row][col+1] ) count++;
       }
        if (row+1 < rows) {
        	if( currentGrid[row+1][col] ) count++;
         }
        if (row+1 < rows && col-1 >= 0) {
        	if( currentGrid[row+1][col-1] ) count++;
        }
        if (row+1 < rows && col+1 < cols) {
        	if( currentGrid[row+1][col+1] ) count++;
       }
        //System.out.println("Cell (" + row + "," + col + ") has " + count + " live neighbors.");
        return count;
    }


    // Metodi di utilità per i test
    public boolean getCell(int r, int c) { return currentGrid[r][c]; }
    public void setCell(int r, int c, boolean state) { currentGrid[r][c] = state; }
    public boolean[][] getGrid() { return currentGrid; }

	@Override
	public boolean isAlive(int row, int col) {
		return currentGrid[row][col];
	}

	@Override
	public int getRows() {
 		return 0;
	}

	@Override
	public int getCols() {
 		return 0;
	}
	
	//Versione NAIVE
//	private boolean[][] deepCopy(boolean[][] original) {
//	    if (original == null) return null;
//
//	    boolean[][] result = new boolean[original.length][];
//	    for (int i = 0; i < original.length; i++) {
//	        // Creiamo una nuova riga e copiamo i valori della riga originale
//	        result[i] = original[i].clone(); 
//	        // Nota: clone() su un array di primitivi (boolean) è sicuro 
//	        // perché i primitivi vengono copiati per valore.
//	    }
//	    return result;
//	}
	

	private boolean[][] deepCopyJava8(boolean[][] original) {
	    return Arrays.stream(original)
	                 .map(boolean[]::clone)
	                 .toArray(boolean[][]::new);
	}
	
	public String gridRep( ) {
	    return Arrays.stream(currentGrid) // Stream di boolean[] (le righe)
	        .map(row -> {
	            // Trasformiamo ogni riga in una stringa di . e O
	            StringBuilder sb = new StringBuilder();
	            for (boolean cell : row) {
	                sb.append(cell ? "O " : ". ");
	            }
	            return sb.toString();
	        })
	        .collect(Collectors.joining("\n")); // Uniamo le righe con un a capo
	}
}
