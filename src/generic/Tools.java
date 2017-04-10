package generic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


import googlemaps.LatLng;
import javafx.animation.Interpolator;
import server.firebase.Firebase;

public class Tools {

	private static String readAllLines(BufferedReader in) {
		String s = "";
		String line = null;
		boolean first = true;
		try {
			while ((line = in.readLine()) != null) {
				if (first) {
					first = false;
					s += line;
				} else
					s += "\n" + line;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getHTTPString(String urlStr) {

		try {

			URL url = new URL(urlStr);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			String result = null;
			if (conn.getResponseCode() < 400) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				result = readAllLines(in);
				in.close();
			} else {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
				result = readAllLines(in);
				in.close();
			}
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Point2D.Double getCenter(Point2D.Double pointA, Point2D.Double pointB) {
		return new Point2D.Double((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2);
	}

	public static LatLng getCenter(LatLng pointA, LatLng pointB) {
		return new LatLng((pointA.latitude + pointB.latitude) / 2, (pointA.longitude + pointB.longitude) / 2);
	}

	public static String pointToString(Point2D.Double p, boolean spaced) {
		if (spaced)
			return p.x + ", " + p.y;
		else
			return p.x + "," + p.y;
	}
	public static String latlngsToString(char delimiter, LatLng... ps)
	{
		StringBuilder strBld = new StringBuilder();
		for (int n = 0; n < ps.length; n++)
		{
			if (n == 0)
				strBld.append(ps[n].toUrlValue());
			else
				strBld.append(delimiter + ps[n].toUrlValue());
		}
		return strBld.toString();
	}
	public static String nodesToString(char delimiter, List<Node> nodes)
	{
		StringBuilder strBld = new StringBuilder();
		for (int n = 0; n < nodes.size(); n++)
		{
			if (n == 0)
				strBld.append(nodes.get(n).getPosition().toUrlValue());
			else
				strBld.append(delimiter + nodes.get(n).getPosition().toUrlValue());
		}
		return strBld.toString();
	}
	public static String nodesToString(char delimiter, Node[][] nodes, int col)
	{
		StringBuilder strBld = new StringBuilder();
		int n = 0;
		while (true)
		{
			
			if (n == 0)
				strBld.append(nodes[col][n].getPosition().toUrlValue());
			else
			{
				int boundVal = Math.min(n, nodes[col].length - 1);
				strBld.append(delimiter + nodes[col][boundVal].getPosition().toUrlValue());
				
			}
			
			
			
			if (n == nodes[col].length - 1)
				break;
			else
				n = Math.min(n + 1 + Config.ELEVATION_GRADIENT_SKIP , nodes[col].length - 1);
		}
		return strBld.toString();
	}
	public static void Interpolate(double[][] vals, int col, int startRow, int endRow)
	{
		double startVal = vals[col][startRow];
		double endVal = vals[col][endRow];
		double step = (endVal - startVal) / (endRow - startRow);
		
		for (int n = startRow + 1; n < endRow; n++)
		{
			double interp = startVal + step * (n - startRow);
			vals[col][n] = interp;
		}
	}
	public static void InterpolateFullColumns(double[][] vals, int startCol, int endCol)
	{
		//Hopefully there's no need for any fancy interpolation, this just does the columns after doing the rows already
		assert vals[startCol].length == vals[endCol].length;
		for (int row = 0; row < vals[startCol].length; row++)
		{
			double startVal = vals[startCol][row];
			double endVal = vals[endCol][row];
			double step = (endVal - startVal) / (endCol - startCol);
			
			for (int n = startCol + 1; n < endCol; n++)
			{
				double interp = startVal + step * (n - startCol);
				vals[n][row] = interp;
			}
			
		}
		
	}
	
	
	public static void WriteImage(BufferedImage img, String path) {
		File file = new File(path);

		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			System.out.println("Could not write image.");
			e.printStackTrace();
		}

	}
	public static BufferedImage ReadImage(String path) {
		File file = new File(path);

		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("Could not write image.");
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage ClipLogo(BufferedImage img) {

		return img.getSubimage(0, 0, img.getWidth(), img.getHeight() - Config.GOOGLE_LOGO_HEIGHT);

	}
	
	
	private static float[] myRGB = new float[3];
	public static void setImageRGB(BufferedImage img, int x, int y, Color color) {
		img.setRGB(x, y, color.getRGB());
	
	}
	public static BufferedImage ImageDeepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null).getSubimage(0, 0, bi.getWidth(), bi.getHeight());
	}
	public static BufferedImage convertICCToRGB(BufferedImage img)
	{
		ICC_Profile ip = ICC_Profile.getInstance( ColorSpace.CS_sRGB );
		ICC_ColorSpace ics = new ICC_ColorSpace( ip );
		ColorConvertOp cco = new ColorConvertOp( ics, null );
		BufferedImage result = cco.filter( img, null );
		return result;
	}
	

	public static void DrawOnImage(BufferedImage imageToDrawOn, BufferedImage source, int startX, int startY)
	{
		
		Graphics g = imageToDrawOn.createGraphics();
		g.drawImage(source, startX, startY, null);
		
		
//		int sourceX = 0;
//		int sourceY = flipY ? source.getHeight() - 1 : 0;
//		for (int x = startX; x < imageToDrawOn.getWidth() && sourceX < source.getWidth(); x++, sourceX++)
//		{
//			if (flipY)
//			{
//				for (int y = startY; y > -1 && sourceY > -1; y--, sourceY--)
//				{
//					int rgb = source.getRGB(sourceX, sourceY);
//					imageToDrawOn.setRGB(x, y, rgb);
//					int cmpRGB = imageToDrawOn.getRGB(x, y);
//					assert rgb == cmpRGB;
//				}
//			} else {
//				for (int y = startY; y < imageToDrawOn.getHeight() && sourceY < source.getHeight(); y++, sourceY++)
//				{
//					imageToDrawOn.setRGB(x, y, source.getRGB(sourceX, sourceY));
//				}
//			}
//		}
	}
	
	public static boolean colorIsProbablyBuilding(int rgb, int seedRGB)
	{
		//return colorIsCloseEnough(rgb, seedRGB, Config.FILLCOLOR_RGB_TOLERANCE) ||
		//		rgb == -1; //WHITE ;
		
		return 
				 (!colorIsCloseEnough(rgb, Config.MAPS_BACKGROUND_RGB, Config.FILLCOLOR_RGB_TOLERANCE) &&
				  !colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, Config.FILLCOLOR_RGB_TOLERANCE) &&
				  !colorIsCloseEnough(rgb, Config.MAPS_NORMALPATH_RGB, Config.FILLCOLOR_RGB_TOLERANCE));
		
		
	}
	public static boolean colorIsCloseEnough(int rgb1, int rgb2, int tolerance)
	{
		return colorIsCloseEnough(rgb1, rgb2, tolerance, tolerance, tolerance);
	}
	
	public static boolean colorIsCloseEnough(int rgb1, int rgb2, int rTolerance, int gTolerance, int bTolerance)
	{
		int r1 = (rgb1 & 0x00ff0000) >> 16;
		int r2 = (rgb2 & 0x00ff0000) >> 16;
		if (Math.abs(r1 - r2) > rTolerance)
			return false;
		int g1 = (rgb1 & 0x0000ff00) >> 8;
		int g2 = (rgb2 & 0x0000ff00) >> 8;
		if (Math.abs(g1 - g2) > gTolerance)
			return false;
		int b1 = (rgb1 & 0x000000ff);
		int b2 = (rgb2 & 0x000000ff);
		if (Math.abs(b1 - b2) > bTolerance)
			return false;
		return true;
	}

	public static String toRGBHex(int i)
	{
		String res = Integer.toHexString(i);
		String removeAlpha = res.substring(2, res.length());
		return "0x" + removeAlpha;
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static String readMock(String name) {
		try {
			return Tools.readFile("mock/" + name, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static void outputArray(ArrayList list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}

	public static Firebase firebase = new Firebase();
}
