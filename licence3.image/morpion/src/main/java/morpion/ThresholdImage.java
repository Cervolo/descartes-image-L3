package morpion;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class)
public class ThresholdImage<T extends RealType<T>> implements Command {

	@Parameter(persist = false)
	ImgPlus<T> image;

	@Parameter(required = false)
	int threshold = 127;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> imageConv;

	@Override
	public void run() {
		// dimensions holds the size of the input image in x and y
		long[] dimensions = new long[image.numDimensions()];
		image.dimensions(dimensions);
		// Creation of the resulting image with the same size as the input image.
		imageConv = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		imageConv.setName(image.getName() + "_Mask");

		// Two random cursor to visit all pixels in the input and output images.
		RandomAccess<T> cursorIn = image.randomAccess();
		RandomAccess<UnsignedByteType> cursorOut = imageConv.randomAccess();

		// Completez ce code en utilisant les deux curseurs, un pour lire les
		// intensites et l'autre pour creer l'image binaire

		long[] pos = new long[dimensions.length];

		// 1. Parcourir toutes les lignes de l'image
		for (int i = 0 ; i < dimensions[0] ; i++) {
			pos[0] = i;
			
			// 2. Parcourir toutes les colonnes de l'image
			for (int j = 0 ; j < dimensions[1] ; j++) {
				pos[1] = j;
				
				// 3. Affecter la position aux curseurs
				cursorIn.setPosition(pos);
				cursorOut.setPosition(pos);
				
				// 4. Obtenir les intensités de l'image à la position p
				T intensity = cursorIn.get();
				
				// 5. Affecter pixel de l'image de sortie
				if (intensity.getRealDouble() > threshold)
					cursorOut.get().set(255);
				else
					cursorOut.get().set(0);
			}
		}
	}
}
