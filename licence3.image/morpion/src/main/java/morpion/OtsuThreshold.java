package morpion;

import java.util.concurrent.ExecutionException;

import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;

import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class OtsuThreshold <T extends RealType<T>> {

	@Parameter
	OpService os;

	@Parameter
	CommandService cs;
	
	private ImgPlus<T> image;
	
	public OtsuThreshold(ImgPlus<T> image) {
		this.image = image;
	}
	
	@SuppressWarnings("unchecked")
	public ImgPlus<UnsignedByteType> thresholding() {
		
		// Image output
		ImgPlus<UnsignedByteType> imageConv;
				
		// Dimensions holds the size of the input image in x and y
		long[] dimensions = new long[image.numDimensions()];
		image.dimensions(dimensions);
				
		// Creation of the resulting image with the same size as the input image.
		imageConv = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		imageConv.setName(image.getName() + "_Mask");
		
		Histogram1d<T> histogram = os.image().histogram(image);
		T threshold = os.threshold().otsu(histogram);
		try {
			imageConv = (ImgPlus<UnsignedByteType>) cs
					.run(ThresholdImage.class, false, "image", image, "threshold", (long) threshold.getRealDouble()).get()
					.getOutput("imageConv");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return imageConv;
	}
}
