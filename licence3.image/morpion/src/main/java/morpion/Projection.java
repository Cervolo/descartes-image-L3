package morpion;


import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class Projection<T> {

	public static Img<IntType> project(ImgPlus<UnsignedByteType> img, boolean vertical) {
		
		// Récupération des dimensions de l'image d'entrée
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		
		// Création de l'image de sortie de dimension largeurx10 ou 10xhauteur
		long[] projDims = new long[] { vertical ? dims[0] : 10, vertical ? 10 : dims[1] };
		Img<IntType> imgProj = ArrayImgs.ints(projDims);
		
		RandomAccess<UnsignedByteType> imgCursor = img.randomAccess();
		RandomAccess<IntType> projCursor = imgProj.randomAccess();

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
}
