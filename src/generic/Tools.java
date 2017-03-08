package generic;

import java.awt.Color;
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
	

	public static void WriteImage(BufferedImage img, String path) {
		File file = new File(path);

		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			System.out.println("Could not write image.");
			e.printStackTrace();
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
