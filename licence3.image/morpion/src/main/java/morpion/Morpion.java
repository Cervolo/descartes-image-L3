package morpion;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageConverter;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops.Convert;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, name = "morpion", menuPath = "Plugins>Morpion Analysis")
public class Morpion<T extends RealType<T>> implements Command {

	@Parameter
	CommandService cs;
	
	@Parameter
	OpService ops;
	
	@Parameter(persist = false)
	ImagePlus image; // pour grayscale
	//ImgPlus<T> image; // pour threshold

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> imgOut;

	@Override
	public void run() {
		
		// Pour convertir en niveaux de gris (8 bits)
		ImageConverter imgConv = new ImageConverter(image);
		imgConv.convertToGray8();
				
		// Pour faire une binarisation
		/*int threshold = 127;		
		Threshold<T> t = new Threshold<>(image);
		imgOut = t.binarisation(threshold);*/
	}

}
