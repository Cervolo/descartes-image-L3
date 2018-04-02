package morpion;

import java.util.HashMap;
import java.util.Map;
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
import net.imglib2.histogram.Histogram1d;

import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
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
		imgOut = new ImgPlus<UnsignedByteType>(ImagePlusAdapter.wrapByte(imgPL), imgIn.getName());
		
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
		
		// Extraction des coordonnées de la grille de jeu
		long[][] gridCoordV = UtilGrid.getGrid(imgProjH_bin);
			
		
		// Idem verticalement //
		
		// Projection
		Dataset imgProjV =  (Dataset) os.run("projection", imgIn, true); // supprimer Dataset pour affichage intermédiaire
		ImgPlus<IntType> imgProjVPL = (ImgPlus<IntType>) imgProjV.getImgPlus();
		
		// Calcul "dynamique" du seuil de binarisation et binarisation
		int thresholdV = UtilGrid.getThreshold(imgProjVPL);
		Threshold<T> tV = new Threshold<T>(imgProjVPL);
		ImgPlus<UnsignedByteType> imgProjV_bin = tV.binarisation(thresholdV); // binarisation
		ImageJFunctions.show(imgProjV_bin); // affichage pour debug
		
		// Extraction des coordonnées de la grille de jeu
		long[][] gridCoordH = UtilGrid.getGrid(imgProjV_bin);
		
		
		// Création des 9 cellules de la grille de jeu //

		long[] C1_1 = {0, 0};
		long[] C1_2 = {gridCoordH[0][0]-1, gridCoordV[0][1]-1};
		Cell C1 = new Cell(C1_1, C1_2, imgOut);
		
		long[] C2_1 = {gridCoordH[0][0]+1, 0};
		long[] C2_2 = {gridCoordH[1][0]-1, gridCoordV[0][1]-1};
		Cell C2 = new Cell(C2_1, C2_2, imgOut);
		
		long[] C3_1 = {gridCoordH[1][0]+1, 0};
		long[] C3_2 = {dims[0]-1, gridCoordV[0][1]-1};
		Cell C3 = new Cell(C3_1, C3_2, imgOut);
		
		long[] C4_1 = {0, gridCoordV[0][1]+1};
		long[] C4_2 = {gridCoordH[0][0]-1, gridCoordV[1][1]-1};
		Cell C4 = new Cell(C4_1, C4_2, imgOut);
		
		long[] C5_1 = {gridCoordH[0][0]+1, gridCoordV[0][1]+1};
		long[] C5_2 = {gridCoordH[1][0]-1, gridCoordV[1][1]-1};
		Cell C5 = new Cell(C5_1, C5_2, imgOut);
		
		long[] C6_1 = {gridCoordH[1][0]+1, gridCoordV[0][1]+1};
		long[] C6_2 = {dims[0]-1, gridCoordV[1][1]-1};
		Cell C6 = new Cell(C6_1, C6_2, imgOut);
		
		long[] C7_1 = {0, gridCoordV[1][1]+1};
		long[] C7_2 = {gridCoordH[0][0]-1, dims[1]-1};
		Cell C7 = new Cell(C7_1, C7_2, imgOut);
		
		long[] C8_1 = {gridCoordH[0][0]+1, gridCoordV[1][1]+1};
		long[] C8_2 = {gridCoordH[1][0]-1, dims[1]-1};
		Cell C8 = new Cell(C8_1, C8_2, imgOut);
		
		long[] C9_1 = {gridCoordH[1][0]+1, gridCoordV[1][1]+1};
		long[] C9_2 = {dims[0]-1, dims[1]-1};
		Cell C9 = new Cell(C9_1, C9_2, imgOut);
		
		// Affichage des coordonnées des cellules pour debug
		/*C1.printCell("C1");	C2.printCell("C2"); C3.printCell("C3");
		C4.printCell("C4"); C5.printCell("C5"); C6.printCell("C6");
		C7.printCell("C7"); C8.printCell("C8"); C9.printCell("C9");*/
		
		
		//* Effaçage de la grille *//
		
		//TODO
		
		
		
		//* Détermination du contenu des cases *//
		
		Cell[] tabCells = {C1, C2, C3, C4, C5, C6, C7, C8, C9};
		Map<Cell, Shape> mapCells = new HashMap<>();
		
		for(Cell cell : tabCells) {
			//cell.printCell("Cell"); // debug
			cell = cell.crop();
			//cell.printCell("Crop"); // debug
			//System.out.println(cell.isEmpty()); // debug
			if (cell.isEmpty())
				mapCells.put(cell, Shape.EMPTY);
			else
				mapCells.put(cell, cell.getShape());
		}


		//* Détermination du tour de jeu *//
		
		// TODO : compter nombre de croix et de cercle pr déterminer qui doit jouer ensuite, et si jeu fini
		
		
		//* Restitution des informations extraites de l'image *//
		
		// TODO
		
		}
}