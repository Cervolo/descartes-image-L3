import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imglib2.Cursor;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

@Plugin(type = Op.class, name = "equalizeHistogram")
public class EqualizeHistogram<T extends RealType<T>> extends AbstractOp {

	@Parameter
	private ImgPlus<T> image;

	@Parameter(required = false)
	private int numBins = 256;

	@Parameter(type = ItemIO.OUTPUT)
	private ImgPlus<T> expanded;

	@Override
	public void run() {
		Histogram1d<T> histogram = ops().image().histogram(image, numBins);
		Histogram1d<T> accumulatedHistogram = new Histogram1d<>(histogram);
		Cursor<LongType> histoCursor = histogram.cursor();
		Cursor<LongType> accHistoCursor = accumulatedHistogram.cursor();
		LongType accumulated = new LongType(0);
		while (histoCursor.hasNext()) {
			histoCursor.fwd();
			accHistoCursor.fwd();
			accumulated.add(histoCursor.get());
			accHistoCursor.get().set(accumulated);
		}

		expanded = image.copy();
		Cursor<T> cursor = expanded.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			equalizeHistogram(cursor.get(), image.dimension(0) * image.dimension(1), 255, accumulatedHistogram);
		}
	}

	private void equalizeHistogram(T pixel, long numPixels, double maxValue, Histogram1d<T> accumulatedHistogram) {
		pixel.setReal((accumulatedHistogram.frequency(pixel) * maxValue / (double) numPixels));
	}

}
