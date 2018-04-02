package morpion;


import java.util.ArrayList;
import java.util.Arrays;

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
	 * @return The coordinates of the half grid (horizontal or vertical). 
	 */
	public static long[][] getGrid(ImgPlus<UnsignedByteType> img) {
		
		long[][] gridCoord = new long[2][2]; // retour : 2 couples de coordonnées
		
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		long maxDim = Math.max(dims[0], dims[1]);
		long middle = maxDim/2; // milieu de l'image
		boolean vertical = (maxDim==dims[1]) ? true : false; // orientation de l'image
		
		// Initialisation des coordonnées nulles en fonction de l'orientation
		if (vertical) {
			gridCoord[0][0] = 0;
			gridCoord[1][0] = 0;
		}
		else {
			gridCoord[0][1] = 0;
			gridCoord[1][1] = 0;
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
		
		// Pour debug
		/*System.out.println(moyenne1);
		System.out.println(moyenne2);
		System.out.println(gridCoord[0][0] + " " + gridCoord[0][1]);
		System.out.println(gridCoord[1][0] + " " + gridCoord[1][1]);*/
		
		return gridCoord;		
	}
}