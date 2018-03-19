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
import net.imagej.display.DefaultImageDisplay;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Convert;
import net.imagej.ops.convert.ConvertImages;
import net.imglib2.RandomAccess;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

//import net.imglib2.img.display.imagej.ImageJFunctions;


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
	ImgPlus<T> imgIn; // pour threshold

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> imgOut;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		// Récupération des dimensions de l'image
		long[] dims = new long[imgIn.numDimensions()];
		imgIn.dimensions(dims);
		
		
		//* Traitements préalables sur l'image *//
		
		// Pour convertir en niveaux de gris (8 bits)
		/*ImageConverter imgConv = new ImageConverter(image);
		imgConv.convertToGray8();  */
		//Ops.Convert.Uint8(image);
		//imgOut = (ImgPlus<UnsignedByteType>) os.convert().uint8(image);
		
		// Egalisation de l'histogramme
		imgOut = (ImgPlus<UnsignedByteType>) os.run("equalizeHistogram", imgIn, 256);
		
		
		
		//* Détermination de la grille de jeu *//
		
		// Horizontalement //
		ImgPlus<UnsignedByteType> imgProjH = UtilGrid.project(imgOut, false); // projection
		//ImageJFunctions.show(imgProjH); // TODO : affichage pour controle
		int thresholdH = UtilGrid.getThreshold(imgProjH); // récupération du seuil de binarisation (3ieme quartile des intensités)
		Threshold<T> tH = new Threshold<T>(imgProjH);
		imgProjH = tH.binarisation(thresholdH); // binarisation
		// TODO : identification des centres des 2 nuages de points
		
		// Idem verticalement //
		ImgPlus<UnsignedByteType> imgProjV = UtilGrid.project(imgOut, true);
		int thresholdV = UtilGrid.getThreshold(imgProjV);
		Threshold<T> tV = new Threshold<T>(imgProjV);
		imgProjV = tV.binarisation(thresholdV);
		

		
		// Binarisation de l'image de départ par Otsu
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
		
	
		
	}

}
