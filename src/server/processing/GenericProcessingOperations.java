package server.processing;

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
	private static final int AOI = 100;
	private static final int JIGGLE_ROOM = 5;
	
	private static int eastPixelError(BufferedImage sw, BufferedImage east, int xOverlap, int yJiggle)
	{
		int err = 0;
		int w = east.getWidth();
		int halfHeight = sw.getHeight() / 2;
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
		for (int northX = halfWidth - (AOI / 2) + xJiggle; northX < halfWidth + (AOI / 2) + xJiggle; northX++)
		{
			int swX = northX - xJiggle;
			for (int northY = northHeight - CHECKING_PIXEL_DIMS; northY <  northHeight; northY++)
			{
				int swY = northY - (northHeight - CHECKING_PIXEL_DIMS);
				err += (sw.getRGB(swX, swY) == north.getRGB(northX, northY) ? 0 : 1);
			}
		}
		
		return err;
	}
	
	
	public static LatLng getLatLngEastStitchDelta(BufferedImage southwestImage, BufferedImage eastboundImage)
	{
		int xOverlap = 50;
		int lowestErrArg = -1;
		int lowestError = Integer.MAX_VALUE;
		
		while (xOverlap > CHECKING_PIXEL_DIMS)
		{
			int err=  eastPixelError(southwestImage, eastboundImage, xOverlap, 0);
			if (err < lowestError) {
				lowestError = err;
				lowestErrArg = xOverlap;
			}
			xOverlap--;
		}
		
		xOverlap = lowestErrArg;
		
		lowestErrArg = -1;
		lowestError = Integer.MAX_VALUE;
		
		for (int jigg = -JIGGLE_ROOM; jigg <= JIGGLE_ROOM; jigg++)
		{
			int err=  eastPixelError(southwestImage, eastboundImage, xOverlap, jigg);
			if (err < lowestError) {
				lowestError = err;
				lowestErrArg = jigg;
			}
		}
		
		return new LatLng(xOverlap, lowestErrArg);
		
		
		//return null;
	}
	
	public static LatLng getLatLngNorthStitchDelta(BufferedImage southwestImage, BufferedImage northboundImage)
	{
		int yOverlap = 50;
		int lowestErrArg = -1;
		int lowestError = Integer.MAX_VALUE;
		
		while (yOverlap > CHECKING_PIXEL_DIMS)
		{
			int err=  northPixelError(southwestImage, northboundImage, yOverlap, 0);
			if (err < lowestError) {
				lowestError = err;
				lowestErrArg = yOverlap;
			}
			yOverlap--;
		}
		
		yOverlap = lowestErrArg;
		
		lowestErrArg = -1;
		lowestError = Integer.MAX_VALUE;
		
		for (int jigg = -JIGGLE_ROOM; jigg <= JIGGLE_ROOM; jigg++)
		{
			int err=  northPixelError(southwestImage, northboundImage, yOverlap, jigg);
			if (err < lowestError) {
				lowestError = err;
				lowestErrArg = jigg;
			}
		}
		
		return new LatLng(yOverlap, lowestErrArg);
		
		
		//return null;
	}
}
