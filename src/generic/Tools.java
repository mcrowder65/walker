package generic;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

import googlemaps.LatLng;

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

	public static String latlngToString(LatLng p, boolean spaced) {
		if (spaced)
			return p.latitude + ", " + p.longitude;
		else
			return p.latitude + "," + p.longitude;
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

	public static void setImageRGB(BufferedImage img, int x, int y, int r, int g, int b) {
		int col = (r << 16) | (g << 8) | b;
		img.setRGB(x, y, col);
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
}
