package server.processing;

import java.awt.image.BufferedImage;

import generic.Tools;

public class GenericProcessingOperations {

	public static BufferedImage Mask(BufferedImage original, BufferedImage mask)
	{
		BufferedImage copy = Tools.ImageDeepCopy(original);
		return copy;
	}
}
