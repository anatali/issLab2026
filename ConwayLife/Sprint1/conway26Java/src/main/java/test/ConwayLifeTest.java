package main.java.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import main.java.conway.domain.Life;
import main.java.conway.domain.LifeInterface;


public class ConwayLifeTest {

	@Before
	public void setup() {
		System.out.println("ConwayLifeTest | setup");	}

	@After
	public void down() {
		System.out.println("ConwayLifeTest | down");
	}

 	
	//@Test
	public void testRule() {
		System.out.println("testRule");
	}
	
	@Test
	public void testOscillaFromFile() throws Exception {
	    // Carico un Blinker (periodo 2)
	    System.out.println( "testOscillaFromFile ---------------------" );
	    boolean[][] initial = PatternLoader.loadFromResource("src/test/resources/blinker.txt", 5, 5);
	    
 	    Life liferules = new Life(initial);
	    
	    System.out.println( liferules.gridRep() );
	    System.out.println( "________________________ testOscillaFromFile " );    
//
	    liferules.nextGeneration(); // Generazione 1 (cambia stato)
	    System.out.println( liferules.gridRep() );
	    System.out.println( "________________________ testOscillaFromFile " );
	    liferules.nextGeneration(); // Generazione 2 (deve tornare all'originale)
	    System.out.println( liferules.gridRep() );
	    System.out.println( "________________________ testOscillaFromFile " );
//
 	    assertArrayEquals("L'oscillatore deve tornare allo stato iniziale dopo 2 passi", 
 	                      initial, liferules.getGrid());
	}
	
	@Test
	public void testOscilla() {
		System.out.println("testOscilla ---------"  );
		LifeInterface liferules = new Life(5, 5);
		// Configurazione orizzontale
	    liferules.setCell(2, 1, true); 
	    liferules.setCell(2, 2, true);
	    liferules.setCell(2, 3, true);
	    System.out.println("testOscilla | Stato Iniziale:\n" + liferules.gridRep());

	    liferules.nextGeneration();
	    System.out.println("testOscilla | after 1 gen:\n" + liferules.gridRep());
	    // Verifica che sia diventato verticale
	    assertTrue(liferules.isAlive(1, 2)); 
	    assertTrue(liferules.isAlive(2, 2));
	    assertTrue(liferules.isAlive(3, 2));
	    assertFalse(liferules.isAlive(2, 1));

	    liferules.nextGeneration();
	    System.out.println("testOscilla | after 2 gen :\n" + liferules.gridRep());
	    // Verifica che sia tornato orizzontale (Periodo 2)
	    assertTrue(liferules.isAlive(2, 1));
	    assertTrue(liferules.isAlive(2, 2));
	    assertTrue(liferules.isAlive(2, 3));
	}	

	
/*
L'Approccio "Test-First" (TDD): 

Si scrive il test prima che la classe esista. Il test fallirà (non compila nemmeno). 
Questo la costringe a definire l'interfaccia (i nomi dei metodi, i parametri) 
dal punto di vista dell'utilizzatore e non dell'implementatore.

Scrivere il test per il "Blinker" costringe a decidere subito: 
"Come passo la griglia? Con una matrice di booleani? Con un array di oggetti Cell? 
Come leggo il risultato?".

- Definire l'interfaccia per prima, ha creato la Teoria. 
- I test diventeranno la Verifica della Teoria 
- La classe implementativa sarà la Pratica.

L'approccio di testare le regole di base su configurazioni specifiche (i cosiddetti 
"test unitari su pattern noti") rappresenta la fondamenta del testing 
per il Gioco della Vita. 

Tuttavi la sfida nel software scientifico o simulativo è garantire che il sistema si comporti 
correttamente anche su scale più ampie o in casi limite.	 

#. "Pattern Canonici" Invece di inventare configurazioni, 
   utilizziamo le classi di oggetti definite da Conway.
#. "Property-Based Testing" : si testano le leggi del mondo di gioco.

  - Il test del "Mondo Vuoto"
  - Il test della "Morte per Solitudine"
  - Invarianza per Traslazione
  - comportamento delle celle sui bordi della matrice


Data-Driven Testing: 

- i test non devono essere necessariamente "hard-coded".
- La logica di evoluzione è separata dalla definizione dello stato iniziale.
- Fare sfide per trovare configurazioni che "rompono" le implementazioni altrui 
  (ad esempio configurazioni molto grandi che testano i limiti di memoria).

Nota tecnica

Se nel costruttore si passa l'array direttamente 
(this.grid = initialGrid), si sta passando un riferimento. 
Se il test modifica la matrice, modifica anche l'interno della classe, 
rendendo i test inaffidabili.

una "copia profonda" (deep copy) della matrice è un'ottima lezione collaterale 
su come Java gestisce la memoria.

Occorrono due matrici (gridA e gridB) 

per evitare di dover creare una nuova matrice ad ogni generazione.
Lo scambio temp = gridA; gridA = gridB; gridB = temp; avviene in tempo costante

Matrici sparse

In una configurazione tipica, il 90% della griglia è "morta" (vuota). 
Invece di memorizzare il vuoto, memorizziamo solo le celle vive.

Memorizziamo solo dove c'è vita. Se un punto non è nella mappa, è morto.
Map<Point, Boolean> liveCells = new HashMap<>();

Coordinate illimitate: Un punto può avere coordinate (1000000, 5000000). 
La mappa conterrà solo quella coordinata. Non abbiamo dovuto allocare i milioni di "vuoto" 
che la separano dall'origine.

Crescita dinamica: 

Se una cella nasce a una distanza enorme, la mappa si limita ad aggiungere una voce. 
La "griglia" si espande virtualmente seguendo la vita, senza confini predefiniti

l'interfaccia protegge il comportamento, indipendentemente dalla struttura dati scelta.

Come si calcola in questo caso la nextGeneration con una Map?
 
- Si prendono tutte le celle vive presenti nella mappa.
- Si crea un set di "candidate": sono le celle vive più tutti i loro vicini 
  (perché solo lì può nascere nuova vita).
- Per ogni candidata, si contano i vicini vivi guardando nella mappa.
- Se la regola dice che sopravvive o nasce, la si inserisce nella nextMap.

*/
	
	
}
