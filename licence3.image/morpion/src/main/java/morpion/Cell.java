package morpion;

public class Cell {

	private long[] topLeftCorner = new long[2];
	private long[] bottomRightCorner = new long[2];
	
	public Cell(long[] topLeftCorner, long[] bottomRightCorner) {
		this.topLeftCorner = topLeftCorner;
		this.bottomRightCorner = bottomRightCorner;
	}
	
	public void printCell(String name){
		System.out.println("** " + name + " **");
		System.out.println("Coin haut : (" + topLeftCorner[0] + ", " + topLeftCorner[1] + ")");
		System.out.println("Coin bas : (" + bottomRightCorner[0] + ", " + bottomRightCorner[1] + ")\n");
	}
}
