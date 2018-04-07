package morpion;

import java.util.Arrays;

import org.scijava.ItemIO;
//import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * @author Laura Xénard
 *
 */
@Plugin(type = Op.class, name = "medianFilter")
public class MedianFilter extends AbstractOp {

	@Parameter
	ImgPlus<UnsignedByteType> img;
	
	@Parameter(persist = false)
	int size = 3;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> outImg;
	
	@Override
	public void run() {
		
		// Récupération des dimensions de l'image
		long[] dimensions = new long[img.numDimensions()];
		img.dimensions(dimensions);

		// Création de l'image de sortie aux dimensions de l'image d'entrée
		outImg = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		outImg.setName(img.getName() + "_Filtered");

		// Curseurs pour parcourir les 2 images
		RandomAccess<UnsignedByteType> imgCursor = img.randomAccess();
		RandomAccess<UnsignedByteType> outCursor = outImg.randomAccess();
		
		// Parcours pixel par pixel des images
		long[] position = new long[dimensions.length];
		int intensity;

		for (int i = 0; i < dimensions[0]; i++) {
			position[0] = i;
			for (int j = 0; j < dimensions[1]; j++) {
				position[1] = j;
				outCursor.setPosition(position);
				imgCursor.setPosition(position);
								
				// Cas des coins
				if (position[0]==0 && position[1]==0)
					intensity = computeMedian(imgCursor, position, Position.TOP, Position.LEFT);
				else if (position[0]==dimensions[0]-1 && position[1]==dimensions[1]-1)
					intensity = computeMedian(imgCursor, position, Position.BOTTOM, Position.RIGHT);
				else if (position[0]==dimensions[0]-1 && position[1]==0)
					intensity = computeMedian(imgCursor, position, Position.TOP, Position.RIGHT);
				else if (position[0]==0 && position[1]==dimensions[1]-1)
					intensity = computeMedian(imgCursor, position, Position.BOTTOM, Position.LEFT);
				
				// Cas de la bordure
				else if (position[0]==0)
					intensity = computeMedian(imgCursor, position, Position.LEFT);
				else if (position[0]==dimensions[0]-1)
					intensity = computeMedian(imgCursor, position, Position.RIGHT);
				else if (position[1]==0)
					intensity = computeMedian(imgCursor, position, Position.TOP);
				else if (position[1]==dimensions[1]-1)
					intensity = computeMedian(imgCursor, position, Position.BOTTOM);
				
				// Cas général
				else
					intensity = computeMedian(imgCursor, position);
				
				outCursor.get().set(intensity); // affectation au pixel correspondant dans l'image de sortie
			}
		}
	}
	
	
	/** 
	 * Compute the median intensity of a pixel of an image.
	 * @param imgCursor A random access cursor to process the pixels' image.
	 * @param positionImage The position of the core pixel of the median filter. 
	 * @return The median of the pixels' intensities.
	 */
	public int computeMedian(RandomAccess<UnsignedByteType> imgCursor, long[] positionImage) {

		int[] intensities = new int[size*size];
		long[] positionTemplate = new long[2];
		int k = 0;
		int decalage = size/2;

		for (long i = positionImage[0]-decalage ; i <= positionImage[0]+decalage ;  i++) {
			positionTemplate[0] = i;
			for (long j = positionImage[1]-decalage ; j <= positionImage[1]+decalage ;  j++) {
				positionTemplate[1] = j;
				imgCursor.setPosition(positionTemplate);
				intensities[k] = imgCursor.get().getInteger();
				k++;
			}
		}

		Arrays.sort(intensities);
		return intensities[size*size/2];
	}
	
	
	/**
	 * Compute the median intensity of a pixel from the border of an image.
	 * @param imgCursor A random access cursor to process the pixels' image.
	 * @param positionImage The position of the core pixel of the median filter.
	 * @param pos The border where the pixel is located.
	 * @return The median of the pixels' intensities.
	 */
	private int computeMedian(RandomAccess<UnsignedByteType> imgCursor, long[] positionImage, Position pos) {

		int[] intensities = new int[size*size];
		long[] positionTemplate = new long[2];
		int k = 0;
		int decalage = size/2;

		if (pos==Position.TOP) {
			for (long i = positionImage[0]-decalage ; i <= positionImage[0]+decalage ;  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1] ; j <= positionImage[1]+decalage ;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;

					// Rajout de la ligne manquante par miroir
					if (j==positionImage[1]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}
		}
		else if (pos==Position.BOTTOM) {
			for (long i = positionImage[0]-decalage ; i <= positionImage[0]+decalage ;  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1]-decalage ; j <= positionImage[1] ;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;
					
					// Rajout de la ligne manquante par miroir
					if (j==positionImage[1]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}
		}
		else if (pos==Position.LEFT) {
			for (long i = positionImage[0] ; i <= positionImage[0]+decalage ;  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1]-decalage ; j <= positionImage[1]+decalage  ;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;
					
					// Rajout de la colonne manquante par miroir
					if (i==positionImage[0]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}
		}
		else if (pos==Position.RIGHT) {
			for (long i = positionImage[0]-decalage ; i <= positionImage[0] ;  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1]-decalage ; j <= positionImage[1]+decalage ;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;
					
					// Rajout de la colonne manquante par miroir
					if (i==positionImage[0]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}
		}
		
		Arrays.sort(intensities);
		return intensities[size*size/2];
	}
	
	
	/**
	 * Compute the median intensity of a pixel from the corner of an image.
	 * @param imgCursor A random access cursor to process the pixels' image.
	 * @param positionImage The position of the core pixel of the median filter.
	 * @param pos The corner where the pixel is located.
	 * @return The median of the pixels' intensities.
	 */
	private int computeMedian(RandomAccess<UnsignedByteType> imgCursor, long[] positionImage, Position pos1, Position pos2) {

		int[] intensities = new int[size*size];
		long[] positionTemplate = new long[2];
		int k = 0;
		int decalage = size/2;

		if (pos1==Position.TOP && pos2==Position.LEFT) {
			for (long i = positionImage[0]; i <= positionImage[0]+decalage ;  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1] ; j <= positionImage[1]+decalage ;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;

					// Rajout des pixels manquants par miroir
					if (j==positionImage[1]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (i==positionImage[0]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (j==positionImage[1]+decalage && i==positionImage[0]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}			
		}
		else if (pos1==Position.BOTTOM && pos2==Position.RIGHT) {
			for (long i = positionImage[0]-decalage; i <= positionImage[0] ;  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1]-decalage ; j <= positionImage[1] ;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;

					// Rajout des pixels manquants par miroir
					if (j==positionImage[1]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (i==positionImage[0]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (j==positionImage[1]-decalage && i==positionImage[0]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}			
		}
		else if (pos1==Position.TOP && pos2==Position.RIGHT) {
			for (long i = positionImage[0]-decalage; i <= positionImage[0];  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1]; j <= positionImage[1]+decalage;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;

					// Rajout des pixels manquants par miroir
					if (j==positionImage[1]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (i==positionImage[0]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (j==positionImage[1]+decalage && i==positionImage[0]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}			
		}
		else if (pos1==Position.BOTTOM && pos2==Position.LEFT) {
			for (long i = positionImage[0]; i <= positionImage[0]+decalage;  i++) {
				positionTemplate[0] = i;
				for (long j = positionImage[1]-decalage ; j <= positionImage[1] ;  j++) {
					positionTemplate[1] = j;
					imgCursor.setPosition(positionTemplate);
					intensities[k] = imgCursor.get().getInteger();
					k++;

					// Rajout des pixels manquants par miroir
					if (j==positionImage[1]-decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (i==positionImage[0]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
					if (j==positionImage[1]-decalage && i==positionImage[0]+decalage) {
						intensities[k] = imgCursor.get().getInteger();
						k++;
					}
				}
			}			
		}
		
		Arrays.sort(intensities);
		return intensities[size*size/2];
	}
}

