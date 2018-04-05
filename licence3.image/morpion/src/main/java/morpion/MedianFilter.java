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
		
		// Agrandir l'image avec une bordure de 0
		
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
		int decalage = size/2;
		for (int i = decalage; i < dimensions[0]-decalage; i++) {
			position[0] = i;
			for (int j = decalage; j < dimensions[1]-decalage; j++) {
				position[1] = j;
				outCursor.setPosition(position);
				imgCursor.setPosition(position);				
				intensity = computeMedian(imgCursor, position); // calcul de la médiane des intensités
				outCursor.get().set(intensity); // affectation au pixel correspondant dans l'image de sortie
			}
		}
	}
	
	
	/**
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
}

