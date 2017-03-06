package server.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;

import generic.Tools;

public class ColorOperations {

	public static  BufferedImage toGrayscale(BufferedImage orig)
	{
		BufferedImage copy = Tools.ImageDeepCopy(orig);
		int[] data = new int[3];
		float[] hsb = new float[3];
		for (int x = 0; x < copy.getWidth(); x++) {
			for (int y = 0; y < copy.getHeight(); y++)
			{
				int origRGB = copy.getRGB(x, y);
				Color color = new Color(origRGB);
				data[0] = color.getRed();
				data[1] = color.getGreen();
				data[2] = color.getBlue();
				Color.RGBtoHSB(data[0], data[1], data[2], hsb);
				int greyRGB = Color.HSBtoRGB(hsb[0], 0, hsb[2]);
				copy.setRGB(x, y, greyRGB);
			}
		}
		return copy;
	}
	
	public static BufferedImage threshold(BufferedImage orig, int thresh)
	{
		BufferedImage copy = Tools.ImageDeepCopy(orig);
		
		int whiteRGB = Color.WHITE.getRGB();
		int blackRGB = Color.BLACK.getRGB();
		
		for (int row = 0; row < copy.getHeight(); row++)
		{
			for (int col = 0; col < copy.getWidth(); col++)
			{
				int origRGB = copy.getRGB(col, row);
				Color color = new Color(origRGB);
				assert color.getRed() == color.getBlue();
				int rgb = color.getRed();
				if (rgb >= thresh)
					copy.setRGB(col, row, whiteRGB);
				else
					copy.setRGB(col, row, blackRGB);
			}
		}
		return copy;
	}
}
