package projection;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 4 >Correlate images")
public class Crosscorrelation implements Command {

	@Parameter
	DatasetService ds;

	@Parameter
	Dataset img;

	@Parameter
	Dataset template;

	@Parameter(type = ItemIO.OUTPUT)
	Dataset correlation;

	@Override
	public void run() {
		correlation = computeCrossCorrelation(img, template);
	}

	private Dataset computeCrossCorrelation(Dataset img, Dataset template) {

		RandomAccess<? extends RealType<?>> imgCursor = img.randomAccess();
		RandomAccess<? extends RealType<?>> templateCursor = template.randomAccess();
		long[] imgSize = new long[img.numDimensions()];
		img.dimensions(imgSize);
		long[] templateSize = new long[template.numDimensions()];
		template.dimensions(templateSize);

		Img<DoubleType> res = ArrayImgs.doubles(imgSize);
		RandomAccess<DoubleType> resCursor = res.randomAccess();

		long[] position = new long[2];
		for (int i = 0; i < imgSize[0]; i++) {
			position[0] = i;
			for (int j = 0; j < imgSize[1]; j++) {
				position[1] = j;
				imgCursor.setPosition(position);
				resCursor.setPosition(position);
				resCursor.get().set(correlate(imgCursor, imgSize, templateCursor, templateSize));
			}
		}

		return ds.create(res);
	}

	public double correlate(RandomAccess<? extends RealType<?>> img, long[] imgSize,
			RandomAccess<? extends RealType<?>> template, long[] templateSize) {
		long[] initialPositionImage = new long[2];
		img.localize(initialPositionImage);

		long[] positionTemplate = new long[] { 0, 0 };
		long[] positionImage = new long[2];

		// Completez le code
        double sum = 0;
        double div = templateSize[0] * templateSize[1];
        
        // 1. pour chaque colonne du template
        for (long i = 0; i < templateSize[0]; i++) {
        	
            // 2. pour chaque ligne du template
            positionTemplate[0] = i;
            positionImage[0] = initialPositionImage[0] + i;
            for (long j = 0; j < templateSize[1]; j++) {
            	
                // 3. on multiplie l'intensite de l'image avec l'intensité du template a
                // la position de l'image donnee par i et j. Cette valeur doit etre
                // normalize par la taille de l'image (quantite de pixels). Puis on ajoute cette
                // multiplication a sum.
                positionTemplate[1] = j;
                positionImage[1] = initialPositionImage[1] + j;

                if (positionImage[0] >= 0 && positionImage[1] >= 0 && positionImage[0] < imgSize[0]
                        && positionImage[1] < imgSize[1]) {
                    template.setPosition(positionTemplate);
                    img.setPosition(positionImage);

                    sum += (template.get().getRealDouble() * img.get().getRealDouble()) / div;
                }
            }
        }

		return sum;
	}

}