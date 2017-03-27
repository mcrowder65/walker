package server.processing;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import generic.Tools;
import googlemaps.LatLng;

public class GenericProcessingOperations {

	public static BufferedImage Mask(BufferedImage original, BufferedImage mask)
	{
		BufferedImage copy = Tools.ImageDeepCopy(original);
		return copy;
	}
	private static final int CHECKING_PIXEL_DIMS = 5;
	private static final int JIGGLE_ROOM = 5;
	
	private static int eastPixelError(BufferedImage sw, BufferedImage east, int xOverlap, int yJiggle)
	{
		int err = 0;
		int w = east.getWidth();
		int halfHeight = sw.getHeight() / 2;
		int AOI = sw.getHeight() - (JIGGLE_ROOM * 2);
		
		for (int eastY = halfHeight - (AOI / 2) + yJiggle; eastY < halfHeight + (AOI / 2) + yJiggle; eastY++)
		{
			int swY = eastY - yJiggle;
			for (int eastX = 0; eastX <  CHECKING_PIXEL_DIMS; eastX++)
			{
				int swX = eastX + (w - xOverlap);
				err += (sw.getRGB(swX, swY) == east.getRGB(eastX, eastY) ? 0 : 1);
			}
		}

		return err;
	}
	private static int northPixelError(BufferedImage sw, BufferedImage north, int yOverlap, int xJiggle)
	{
		int err = 0;
		int halfWidth = sw.getWidth() / 2;
		int northHeight = north.getHeight();
		int AOI = sw.getWidth() - (JIGGLE_ROOM * 2);
		
		for (int northX = halfWidth - (AOI / 2) + xJiggle; northX < halfWidth + (AOI / 2) + xJiggle; northX++)
		{
			int swX = northX - xJiggle;
			for (int northY = northHeight - CHECKING_PIXEL_DIMS; northY <  northHeight; northY++)
			{
				int swY = northY - (northHeight - CHECKING_PIXEL_DIMS);
				err += (Tools.colorIsCloseEnough(sw.getRGB(swX, swY), north.getRGB(northX, northY), 2) ? 0 : 1);
			}
		}
		
		return err;
	}
	private static int northeastPixelError(BufferedImage south, BufferedImage west, BufferedImage ne, int xOverlap, int yOverlap)
	{
		return 0;
	}
	
	
	public static Point getEastStitchDelta(BufferedImage southwestImage, BufferedImage eastboundImage)
	{
		int xOverlap = 100;
		int lowestErrJiggle = 0;
		int lowestErrOffset = 0;
		int lowestError = Integer.MAX_VALUE;
		
		while (xOverlap > CHECKING_PIXEL_DIMS)
		{
			for (int jigg = -JIGGLE_ROOM; jigg <= JIGGLE_ROOM; jigg++)
			{			
				int err=  eastPixelError(southwestImage, eastboundImage, xOverlap, jigg);
				if (err < lowestError) {
					lowestError = err;
					lowestErrOffset = xOverlap;
					lowestErrJiggle = jigg;
				}
			}
			xOverlap--;
		}
		
		System.out.println("Optimal delta found as x=" + lowestErrOffset +", y=" + lowestErrJiggle + " with err " + lowestError);

		return new Point(lowestErrOffset, lowestErrJiggle);
		
	}
	
	public static Point getNorthStitchDelta(BufferedImage southwestImage, BufferedImage northboundImage)
	{
		int yOverlap = 130;
		int lowestErrJiggle = 0;
		int lowestErrOffset = 0;
		int lowestError = Integer.MAX_VALUE;
		
		while (yOverlap > CHECKING_PIXEL_DIMS)
		{
			for (int jigg = -JIGGLE_ROOM; jigg <= JIGGLE_ROOM; jigg++)
			{			
				int err=  northPixelError(southwestImage, northboundImage, yOverlap, jigg);
				if (err < lowestError) {
					lowestError = err;
					lowestErrOffset = yOverlap;
					lowestErrJiggle = jigg;
				}
			}
			yOverlap--;
		}
		

		System.out.println("Optimal delta found as x=" + lowestErrJiggle +", y=" + lowestErrOffset + " with err " + lowestError);
		return new Point(0, lowestErrOffset);
	}

	public static Point getNorthEastStitchDelta(BufferedImage southernImage, BufferedImage westernImage, BufferedImage northeastImage)
	{
		int xOverlap = 100;
		int yOverlap = 100;
		int lowestErrXOverlap = 0;
		int lowestErrYOverlap = 0;
		
		int lowestError = Integer.MAX_VALUE;
		while (yOverlap > CHECKING_PIXEL_DIMS)
		{
			while (xOverlap > CHECKING_PIXEL_DIMS)
			{
				
				
				
				xOverlap--;
			}
			yOverlap--;
		}
		return new Point(lowestErrXOverlap, lowestErrYOverlap);
	}
}
