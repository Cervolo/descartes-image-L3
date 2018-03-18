package morpion;

import java.util.concurrent.ExecutionException;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageConverter;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Convert;
import net.imagej.ops.convert.ConvertImages;
import net.imglib2.RandomAccess;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, name = "morpion", menuPath = "Plugins>Morpion Analysis")
public class Morpion<T extends RealType<T>> implements Command {

	@Parameter
	OpService os;
	
	@Parameter
	ConvertService convs;
	
	@Parameter
	CommandService cs;
	
	@Parameter
	DatasetService ds;
	
	@Parameter(persist = false)
	//ImagePlus image; // pour grayscale
	ImgPlus<T> image; // pour threshold

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> imgOut;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		// Pour convertir en niveaux de gris (8 bits)
		/*ImageConverter imgConv = new ImageConverter(image);
		imgConv.convertToGray8();  */
		
		//Ops.Convert.Uint8(image);
		
		// Egalisation de l'histogramme
		imgOut = (ImgPlus<UnsignedByteType>) os.run("equalizeHistogram", image, 256);
		
		// Pour faire une binarisation "normale"
		/*int threshold = 127;		
		Threshold<T> t = new Threshold<>(image);
		imgOut = t.binarisation(threshold);*/
		
		// Binarisation : Otsu
		Histogram1d<UnsignedByteType> histogram = os.image().histogram(imgOut);
		UnsignedByteType threshold = os.threshold().otsu(histogram);
		try {
			imgOut = (ImgPlus<UnsignedByteType>) cs
					.run(ThresholdImage.class, false, "image", imgOut, "threshold", (long) threshold.getRealDouble()).get()
					.getOutput("imageConv");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		
		//* Détermination de la grille de jeu *//
		
		// Projection horizontale
		
		// Projection verticale
	}

}
