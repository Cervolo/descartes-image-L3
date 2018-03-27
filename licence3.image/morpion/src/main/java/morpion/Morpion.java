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
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.histogram.Histogram1d;

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
	Dataset imgIn; // pour threshold

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> imgOut;
	//ImgPlus<UnsignedByteType> imgProjH; // pour affichage intermédiare pr debug
	//Dataset imgProjV; // pour affichage intermédiare pr debug
	

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		// Récupération des dimensions de l'image
		long[] dims = new long[imgIn.numDimensions()];
		imgIn.dimensions(dims);
		
		
		//* Traitements préalables sur l'image *//
		
		// Pour convertir en niveaux de gris (8 bits)
		ImagePlus imgPL = convs.convert(imgIn, ImagePlus.class);
		ImageConverter converter = new ImageConverter(imgPL);
		converter.convertToGray8();
		ImgPlus<UnsignedByteType> imgOut = new ImgPlus<UnsignedByteType>(ImagePlusAdapter.wrapByte(imgPL), imgIn.getName());
		
		// Egalisation de l'histogramme
		imgOut = (ImgPlus<UnsignedByteType>) os.run("equalizeHistogram", imgOut, 256); 
				
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
		
				
		//* Détermination de la grille de jeu *//
		
		// Horizontalement //
		
		// Projection
		Dataset imgProjH =  (Dataset) os.run("projection", imgIn, false); // supprimer Dataset pour affichage intermédiaire
		ImgPlus<IntType> imgProjHPL = (ImgPlus<IntType>) imgProjH.getImgPlus();	
		
		// Calcul "dynamique" du seuil de binarisation et binarisation
		int thresholdH = UtilGrid.getThreshold(imgProjHPL); // récupération du seuil de binarisation
		Threshold<T> tH = new Threshold<T>(imgProjHPL);
		ImgPlus<UnsignedByteType> imgProjH_bin = tH.binarisation(thresholdH); // binarisation
		ImageJFunctions.show(imgProjH_bin); // affichage pour debug
		
		// Version utilisant le module
		/*ImgPlus<UnsignedByteType> imgProjH_bin = (ImgPlus<UnsignedByteType>) os.run("binarisation", imgProjHPL, thresholdH); 
		ImageJFunctions.show(imgProjH_bin);*/
		
		
		// Idem verticalement //
		
		// Projection
		Dataset imgProjV =  (Dataset) os.run("projection", imgIn, true); // supprimer Dataset pour affichage intermédiaire
		ImgPlus<IntType> imgProjVPL = (ImgPlus<IntType>) imgProjV.getImgPlus();
		
		// Calcul "dynamique" du seuil de binarisation et binarisation
		int thresholdV = UtilGrid.getThreshold(imgProjVPL);
		Threshold<T> tV = new Threshold<T>(imgProjVPL);
		ImgPlus<UnsignedByteType> imgProjV_bin = tV.binarisation(thresholdV); // binarisation
		ImageJFunctions.show(imgProjV_bin); // affichage pour debug
		
		// TODO : identification des centres des 2 nuages de points
		

	}

}