package server.processing;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import generic.Config;
import generic.Tools;
import googlemaps.LatLng;
import server.APITools;

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


	/**
	 * Returns an image with the objects filled with a fill color
	 * @param orig
	 * @param fillColor
	 * @param seedPoints
	 * @return
	 */
	public static BufferedImage filledImage(BufferedImage orig, Color fillColor, LatLng southwest, LatLng northeast, LatLng... seedPoints)
	{
		BufferedImage copy = Tools.ImageDeepCopy(orig);
		for (LatLng latlng : seedPoints)
		{
			Point2D.Double point = APITools.getImagePointFromLatLng(latlng, southwest, northeast, copy.getWidth(), copy.getHeight());
			beginFilling(orig, copy, fillColor, point);
		}
		return copy;
	}
	
	private static int getMode(BufferedImage image, int seedX, int seedY)
	{
		HashMap<Integer, Integer> rgbToCount = new HashMap<Integer, Integer>();
		for (int x = seedX - Config.FILLCOLOR_MODE_RADIUS; x <= seedX + Config.FILLCOLOR_MODE_RADIUS; x++)
		{
			for (int y = seedY - Config.FILLCOLOR_MODE_RADIUS; y <= seedY + Config.FILLCOLOR_MODE_RADIUS; y++)
			{
				if (x < 0 || y < 0 || x > image.getWidth() - 1 || y > image.getHeight() - 1)
					continue;
				int rgb = image.getRGB(x, y);
				if (!rgbToCount.containsKey(rgb))
					rgbToCount.put(rgb, 1);
				else
					rgbToCount.put(rgb, rgbToCount.get(rgb) + 1);
			}
		}
		int maxC = 0;
		int maxRGB = -1;
		for (Integer k : rgbToCount.keySet())
		{
			if (rgbToCount.get(k) > maxC)
			{
				maxC = rgbToCount.get(k);
				maxRGB = k;
			}
		}
		return maxRGB;
	}
	
	private static void beginFilling(BufferedImage orig, BufferedImage target, Color fillColor, Point2D.Double seed)
	{
	   
		int fillRGB = fillColor.getRGB();
		boolean[][] visited = new boolean[orig.getHeight()][];
		for (int n = 0; n < orig.getHeight(); n++)
			visited[n] = new boolean[orig.getWidth()];
		
		int currX = (int)seed.getX();
		int currY = (int)seed.getY();
		int currRGB;
		do
		{
			currRGB = orig.getRGB(currX++, currY++);
		} while (currRGB == Config.MAPS_BACKGROUND_RGB || currRGB == Config.MAPS_GRASS_RGB || currRGB == Config.MAPS_NORMALPATH_RGB);
		currX--;
		currY--;
		
		target.setRGB(currX, currY, fillRGB);

		int seedRGB = getMode(orig, currX, currY);
		int neighborRGB;
		boolean tolerated = false;
		Stack<Integer> xStack = new Stack<Integer>();
		Stack<Integer> yStack = new Stack<Integer>();
		xStack.push(currX);
		yStack.push(currY);
		while (!xStack.isEmpty())
		{
			currX = xStack.pop();
			currY = yStack.pop();
			
		
			//Above
			tolerated = false;
			for (int y = currY + 1; y <= currY + 1 + Config.FILLCOLOR_PIXELDIM_TOLERANCE; y++)
			{
				if (y > target.getHeight() - 1) break;
				if (visited[currX][y]) break;
				neighborRGB = orig.getRGB(currX, y);
				if (neighborRGB == seedRGB)
				{
					tolerated = true;
					break;
				}
			}
			if (currY + 1 < target.getHeight()) visited[currX][currY + 1] = true;
			if (tolerated)
			{
				target.setRGB(currX, currY + 1, fillRGB);
				xStack.push(currX);
				yStack.push(currY + 1);
			}
			
			
			//Below
			tolerated = false;
			for (int y = currY - 1; y >= currY - 1 - Config.FILLCOLOR_PIXELDIM_TOLERANCE; y--)
			{
				if (y < 0) break;
				if (visited[currX][y]) break;
				neighborRGB = orig.getRGB(currX, y);
				if (neighborRGB == seedRGB)
				{
					tolerated = true;
					break;
				}
			}
			if (currY - 1 >= 0) visited[currX][currY - 1] = true;
			if (tolerated)
			{
				target.setRGB(currX, currY - 1, fillRGB);
				xStack.push(currX);
				yStack.push(currY - 1);
			}
			
			
			
			//Right
			tolerated = false;
			for (int x = currX + 1; x <= currX + 1 + Config.FILLCOLOR_PIXELDIM_TOLERANCE; x++)
			{
				if (x > target.getWidth() - 1) break;
				if (visited[x][currY]) break;
				neighborRGB = orig.getRGB(x, currY);
				if (neighborRGB == seedRGB)
				{
					tolerated = true;
					break;
				}
			}
			if (currX + 1 < target.getWidth()) visited[currX + 1][currY] = true;
			if (tolerated)
			{
				target.setRGB(currX + 1, currY, fillRGB);
				xStack.push(currX + 1);
				yStack.push(currY);
			}
			
			
			//Left
			tolerated = false;
			for (int x = currX - 1; x >= currX - 1 - Config.FILLCOLOR_PIXELDIM_TOLERANCE; x--)
			{
				if (x < 0) break;
				if (visited[x][currY]) break;
				neighborRGB = orig.getRGB(x, currY);
				if (neighborRGB == seedRGB)
				{
					tolerated = true;
					break;
				}
			}
			if (currX - 1 >= 0) visited[currX - 1][currY] = true;
			if (tolerated)
			{
				target.setRGB(currX - 1, currY, fillRGB);
				
				xStack.push(currX - 1);
				yStack.push(currY);
			}
			
		}
		
	}
}
