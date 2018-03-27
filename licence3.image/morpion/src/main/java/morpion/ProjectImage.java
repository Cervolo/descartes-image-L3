package morpion;

import org.scijava.ItemIO;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;


@Plugin(type = Op.class, name = "projection")
public class ProjectImage<T extends RealType<T>> extends AbstractOp {

	@Parameter
	DatasetService ds;

	@Parameter
	OpService ops;
	
	@Parameter
	ConvertService convs;

	@Parameter
	Dataset img;

	@Parameter
	boolean horizontal; // true = horizontal projection,
											// false = vertical projection

	@Parameter(type = ItemIO.OUTPUT)
	Dataset proj;

	@Override
	public void run() {

		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);

		// la taille du resultat est largeur x 10 ou 10 x hauteur
		long[] projDims = new long[] { horizontal ? dims[0] : 10, horizontal ? 10 : dims[1] };
		Img<IntType> res = ArrayImgs.ints(projDims);

		RandomAccess<RealType<?>> imgCursor = img.randomAccess();
		RandomAccess<IntType> projCursor = res.randomAccess();

		long[] posImg = new long[img.numDimensions()];
		long[] posProj = new long[res.numDimensions()];

		
		// 1. Pour Chaque ligne
        for (int i = 0; i < img.dimension(horizontal ? 0 : 1); i++) {
            posImg[horizontal ? 0 : 1] = i;
            posProj[horizontal ? 0 : 1] = i;
            // 2. On somme les intensités de toutes les intensités de la colonne
            int sum = 0;
            for (int j = 0; j < img.dimension(horizontal ? 1 : 0); j++) {
                posImg[horizontal ? 1 : 0] = j;
                imgCursor.setPosition(posImg);
                sum += imgCursor.get().getRealDouble();
            }

            // 3. On affecte la somme au pixel(s) de l'image de resultat
            for (int j = 0; j < projDims[horizontal ? 1 : 0]; j++) {
                posProj[horizontal ? 1 : 0] = j;
                projCursor.setPosition(posProj);
                projCursor.get().set(sum);
            }
        }

		proj = ds.create(res);
	}

}
