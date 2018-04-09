package morpion;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * @author Laura Xénard
 *
 * @param <T>
 */

public class UtilGrid<T> {
	
	/**
	 * Compute a threshold that will help find the grid position from an image.
	 * @param img A projection (vertical or horizontal) of the image from which we want to compute the threshold.
	 * @return The value of the threshold.
	 */
	public static int getThreshold(ImgPlus<IntType> img) {
		
		// Récupération des dimensions de l'image d'entrée
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		long maxDim = Math.max(dims[0], dims[1]);
		boolean vertical = (maxDim==dims[1]) ? true : false;
		
		int[] intensityTab = new int[(int) maxDim]; // cast car impossible de créer un tab de taille long		
		
		RandomAccess<IntType> imgCursor = img.randomAccess();
		
		long[] posImg = new long[img.numDimensions()];
		posImg[vertical ? 0 : 1] = 0; // on fixe à 0 la coordonnée sur laquelle on ne se déplace pas
		
		// Remplissage du tableau par les intensités des pixels de l'image
		for (int i=0 ; i<intensityTab.length ; i++) {
			posImg[vertical ? 1 : 0] = i;
			imgCursor.setPosition(posImg);
			intensityTab[i] = imgCursor.get().getInteger();
		}
		
		// Tri du tableau et détermination de la valeur du threshod
		Arrays.sort(intensityTab); 
		int threshold = intensityTab[intensityTab.length*1/50]; 
		
		return threshold;
	}
	
	
	/**
	 * Find the half grid position in an image.
	 * @param img A projection (vertical or horizontal) of the image from which we want to extract the grid.
	 * @return The coordinates of the half grid (horizontal or vertical), and the coordinates of the extremities of the scatter plots. 
	 */
	public static long[][] getGrid(ImgPlus<UnsignedByteType> img) {
		
		long[][] gridCoord = new long[6][2]; // retour : 6 couples de coordonnées
		
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		long maxDim = Math.max(dims[0], dims[1]);
		long middle = maxDim/2; // milieu de l'image
		boolean vertical = (maxDim==dims[1]) ? true : false; // orientation de l'image
		
		// Initialisation des coordonnées nulles en fonction de l'orientation
		if (vertical) {
			for (int i=0; i<6 ; i++) 
				gridCoord[i][0] = 0;
		}
		else {
			for (int i=0; i<6 ; i++) 
				gridCoord[i][1] = 0;
		}
		
		// Pour stocker les coordonnées des pixels noirs
		ArrayList<Long> pixelTab1 = new ArrayList<>(); // premier nuage de points
		ArrayList<Long> pixelTab2 = new ArrayList<>(); // second nuage de points
		
		long[] posImg = new long[img.numDimensions()];
		posImg[vertical ? 0 : 1] = 0; // on fixe à 0 la coordonnée sur laquelle on ne se déplace pas
		
		RandomAccess<UnsignedByteType> imgCursor = img.randomAccess();
		
		// Répartition des pixels noirs dans 2 tableaux
		for (long i=0 ; i<maxDim ; i++) {
			posImg[vertical ? 1 : 0] = i;
			imgCursor.setPosition(posImg);
			int intensity = imgCursor.get().getInteger();						
			if (intensity==0) {
				if (i<middle)
					pixelTab1.add(i);
				else
					pixelTab2.add(i);
			}
		}
		
		// Calcul du centre du premier nuage de points
		long somme1 = 0;
		for(long coord : pixelTab1)
			somme1 += coord;
		long moyenne1 = somme1/pixelTab1.size();
		if (vertical)
			gridCoord[0][1] = moyenne1;
		else
			gridCoord[0][0] = moyenne1;
		
		// Calcul du centre du second nuage de points
		long somme2 = 0;
		for(long coord : pixelTab2)
			somme2 += coord;
		long moyenne2 = somme2/pixelTab2.size();
		if (vertical)
			gridCoord[1][1] = moyenne2;
		else
			gridCoord[1][0] = moyenne2;
		
		// Calcul des plages pour les nuages de points
		if (vertical) {
			gridCoord[2][1] = Collections.min(pixelTab1);
			gridCoord[3][1] = Collections.min(pixelTab2);
			gridCoord[4][1] = Collections.max(pixelTab1);
			gridCoord[5][1] = Collections.max(pixelTab2);
		}
		else {
			gridCoord[2][0] = Collections.min(pixelTab1);
			gridCoord[3][0] = Collections.min(pixelTab2);
			gridCoord[4][0] = Collections.max(pixelTab1);
			gridCoord[5][0] = Collections.max(pixelTab2);
		}
		
		// Pour debug
		/*System.out.println(moyenne1);
		System.out.println(moyenne2);
		System.out.println(gridCoord[0][0] + " " + gridCoord[0][1]);
		System.out.println(gridCoord[1][0] + " " + gridCoord[1][1]);
		System.out.println(gridCoord[2][0] + " " + gridCoord[2][1]);
		System.out.println(gridCoord[3][0] + " " + gridCoord[3][1]);
		System.out.println(gridCoord[4][0] + " " + gridCoord[4][1]);
		System.out.println(gridCoord[5][0] + " " + gridCoord[5][1]);*/
		
		return gridCoord;		
	}
	
	
	/**
	 * Convert to white the connected component of a black pixel.
	 * @param img The image to delete the grid from.
	 * @param pixel The pixel to set to white.
	 */
	public static void deleteGrid(ImgPlus<UnsignedByteType> img, long[] pixel) {
		long xpixel = pixel[0];
		long ypixel = pixel[1];
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		long[] posImg = new long[img.numDimensions()];
		int intensity;
		
		RandomAccess<UnsignedByteType> imgCursor = img.randomAccess();
		
		// Traitement du pixel courant
		posImg[0] = xpixel;
		posImg[1] = ypixel; 
		imgCursor.setPosition(posImg);
		intensity = imgCursor.get().getInteger();
		if (intensity==0)
			imgCursor.get().set(255);
		else
			return;
		
		/* Traitement des pixels connexes */
		
		// Pixel haut gauche (inutile car pixel obligatoirement déjà parcouru)
/*		if (xpixel>0 && ypixel>0) {
			posImg[0] = xpixel-1;
			posImg[1] = ypixel-1; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}
*/		
		// Pixel haut milieu
		if (ypixel>0) {
			posImg[0] = xpixel;
			posImg[1] = ypixel-1; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}	
		
		// Pixel haut droit
		if (xpixel<dims[0] && ypixel>0) {
			posImg[0] = xpixel+1;
			posImg[1] = ypixel-1; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}	
		
		// Pixel milieu gauche
		if (xpixel>0) {
			posImg[0] = xpixel-1;
			posImg[1] = ypixel; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}
	
		// Pixel milieu droit 
		if (xpixel<dims[0]) {
			posImg[0] = xpixel+1;
			posImg[1] = ypixel; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}
		
		// Pixel bas gauche
		if (xpixel>0 && ypixel<dims[1]) {
			posImg[0] = xpixel-1;
			posImg[1] = ypixel+1; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}
		
		// Pixel bas milieu
		if (ypixel<dims[1]) {
			posImg[0] = xpixel;
			posImg[1] = ypixel+1; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}

		// Pixel bas droit
		if (xpixel<dims[0] && ypixel<dims[1]) {
			posImg[0] = xpixel+1;
			posImg[1] = ypixel+1; 
			imgCursor.setPosition(posImg);
			intensity = imgCursor.get().getInteger();
			if (intensity==0)
				deleteGrid(img, posImg);			
		}	
			
	}
}
