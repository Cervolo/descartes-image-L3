package morpion;

import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * @author Laura Xénard
 *
 */
public class Cell {

	private long[] topLeftCorner = new long[2];
	private long[] bottomRightCorner = new long[2];
	private ImgPlus<UnsignedByteType> image;
	
	public Cell(long[] topLeftCorner, long[] bottomRightCorner, ImgPlus<UnsignedByteType> img) {
		this.topLeftCorner = topLeftCorner;
		this.bottomRightCorner = bottomRightCorner;
		this.image = img;
	}
	
	
	/**
	 * Test if the cell is empty (i.e. has only white pixels).
	 * @return True if the cell is empty, false otherwise.
	 */
	public boolean isEmpty() {
		
		int intensity;
		RandomAccess<UnsignedByteType> imgCursor = image.randomAccess();
		long[] posImg = new long[2];
		
		// Parcours de tous les pixels de la case
		for (long i=topLeftCorner[0] ; i<=bottomRightCorner[0] ; i++) {
			posImg[0] = i;
			for (long j=topLeftCorner[1] ; j<=bottomRightCorner[1] ; j++) {
				posImg[1] = j;
				imgCursor.setPosition(posImg);
				intensity = imgCursor.get().getInteger();
				if (intensity==0) // si pixel noir
					return false;					
			}		
		}	
		
		return true;
	}	
	
	
	/**
	 * 
	 * @return The kind of shape found inside the cell.
	 */
	public Shape getShape() {
		
		//TODO
		
		return Shape.CROSS;
	}
	
	
	/**
	 * Crop the empty outer border of a cell.
	 * @return A cell with no empty outer row or columns.
	 */
	public Cell crop() {
		
		int intensity;
		RandomAccess<UnsignedByteType> imgCursor = image.randomAccess();
		long[] posImg = new long[2];
		
		long xmin = bottomRightCorner[0]; // initialisation à la valeur max
		long xmax = topLeftCorner[0]; // initialisation à la valeur min
		long ymin = bottomRightCorner[1];
		long ymax = topLeftCorner[1];
		
		// Parcours de tous les pixels de la case
		for (long i=topLeftCorner[0] ; i<=bottomRightCorner[0] ; i++) {
			posImg[0] = i;
			for (long j=topLeftCorner[1] ; j<=bottomRightCorner[1] ; j++) {
				posImg[1] = j;
				imgCursor.setPosition(posImg);
				intensity = imgCursor.get().getInteger();
				if (intensity==0) {// si pixel noir, recalcul des mins et max
					if (i>xmax)
						xmax = i;
					if (i<xmin)
						xmin = i;
					if (j>ymax)
						ymax = j;
					if (j<ymin)
						ymin = j;
				}
			}		
		}	
		
		long[] tlc = {xmin, ymin};
		long[] brc = {xmax, ymax};
		return new Cell(tlc ,brc , this.getImage());
	}
	
	
	/**
	 * Getter for image attribute.
	 * @return The value of the image attribute of the cell.
	 */
	public ImgPlus<UnsignedByteType> getImage() {
		return this.image;
	}
	
	
	/**
	 * Print the coordinates of the top left and bottom right corner of the cell.
	 * @param name Name of the cell
	 */
	public void printCell(String name){
		System.out.println("** " + name + " **");
		System.out.println("Coin haut : (" + topLeftCorner[0] + ", " + topLeftCorner[1] + ")");
		System.out.println("Coin bas : (" + bottomRightCorner[0] + ", " + bottomRightCorner[1] + ")\n");
	}
}
