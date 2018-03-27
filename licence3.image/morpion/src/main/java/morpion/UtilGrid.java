package morpion;


import java.util.Arrays;

import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class UtilGrid<T> {
/*
	public static ImgPlus<UnsignedByteType> project(ImgPlus<UnsignedByteType> img, boolean vertical) {
		
		// Récupération des dimensions de l'image d'entrée
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		
		// Création de l'image de sortie de dimension largeurx10 ou 10xhauteur
		long[] projDims = new long[] { vertical ? dims[0] : 10, vertical ? 10 : dims[1] };
		ImgPlus<UnsignedByteType> imgProj = ImgPlus.wrap(ArrayImgs.unsignedBytes(projDims));
		
		RandomAccess<UnsignedByteType> imgCursor = img.randomAccess();
		RandomAccess<UnsignedByteType> projCursor = imgProj.randomAccess();

		long[] posImg = new long[img.numDimensions()];
		long[] posProj = new long[imgProj.numDimensions()];

		
		// 1. Pour chaque ligne/colonne
        for (int i = 0 ; i < img.dimension(vertical ? 0 : 1) ; i++) {
            posImg[vertical ? 0 : 1] = i;
            posProj[vertical ? 0 : 1] = i;
            
            // 2. On somme les intensités
            int sum = 0;
            for (int j = 0 ; j < img.dimension(vertical ? 1 : 0) ; j++) {
                posImg[vertical ? 1 : 0] = j;
                imgCursor.setPosition(posImg);
                sum += imgCursor.get().getRealDouble();
            }

            // 3. On affecte la somme au pixel(s) de l'image de sortie
            for (int j = 0 ; j < projDims[vertical ? 1 : 0] ; j++) {
                posProj[vertical ? 1 : 0] = j;
                projCursor.setPosition(posProj);
                projCursor.get().set(sum);
            }
        }
			
		return imgProj;
	}
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
}
