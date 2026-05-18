package location;
import unibo.basicomm23.utils.CommUtils;

public class Room {
	//private int DR = 10; //in cm
	private int nr = 0;  //nr*DR < L < nr*(DR+1) lunghezza stanza
	private int nc = 0;	 //nc*DR < H < nc*(DR+1) larghezza stanza

	//Rappresentazione della stanza
	private int[][] room;
	
	//Il record Pos è implicitamente statico
	public record Pos(int x, int y) {} //Trasporto dati puro (DTO)
	
	public Room( int nr, int nc) {
		this.nr=nr;
		this.nc=nc;
		room = new int[nr][nc]; //inizializza a 0
	}
	
	public Pos getHome() {
		return new Pos(0,0);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( int i=0;i<nr;i++) {
			sb.append("|");
			for( int j=0;j<nc;j++) {
				sb.append(room[i][j]+" | ");
			}
			sb.append("\n");
		}
		return sb.toString();		
	}
	
	//Just to test ...
	 public static void main(String[] args) {
		Room r   = new Room(6,5);
		Pos Home = r.getHome();
		CommUtils.outblue("Home at "+ Home.x + "," + Home.y);
		CommUtils.outblue(r.toString());
	}
}
