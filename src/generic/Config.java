package generic;

public class Config {

	public static final boolean USE_MOCK = true;
	public static final int GOOGLE_LOGO_HEIGHT = 22;
	public static final int GOOGLE_MAX_IMAGE_DIMENSIONS_PIXELS = 640;
	public static final String DIRECTIONS_KEY = "AIzaSyB7zQeZeCZFWzwupwYjbioQYldZkdF3oPk";
	public static final String STATICMAP_KEY = "AIzaSyBrlZw4DeO4UtdBQDmSEM9rD6gSkpF3F1g";
	public static final String ELEVATION_KEY = "AIzaSyCrDlO2Sy--Cwg8P5LSsRKbHaMZK3C9GHc";
	public static final String GEOCODING_KEY = "AIzaSyBMX_txZDSZo8tHyvMBWl_AIFWCKWL1DNs";
	public static final String TIMEZONE_KEY = "AIzaSyDeSFM31oN1WKBU3M-z5o8KfYykouhzj90";

	// Colors can be made as a hex (ARGB, where A is the alpha channel (always
	// ff ) )

	public static final int MAPS_BACKGROUND_RGB = -1776417; // GRAY
	public static final int MAPS_GRASS_RGB = -3479901; // GREEN
	public static final int MAPS_NORMALPATH_RGB = 0xff000000; // BLACK
	public static final int MAPS_BUILDING_RGB = 0xffff0000; // RED
	public static final int MAPS_WEIRD_OLIVE_PATH = 0xffB7B393; // A WEIRD OLIVE
	public static final int MAPS_STAIRS_RGB = 0xffA349A4; // PURPLE

	public static final int FILLCOLOR_PIXELDIM_TOLERANCE = 0;
	public static final int FILLCOLOR_MODE_RADIUS = 1;
	public static final int FILLCOLOR_RGB_TOLERANCE = 2;

	public static final int MAX_BLOCK_SIZE = 10;
	public static final int MAX_BLOCK_SIZE_BLACK = 8;
	public static double MAX_BLOCK_DIST_SQUARED;
	public static double MAX_BLOCK_DIST_SQUARED_BLACK;

	public static final int ELEVATION_GRADIENT_SKIP = 4;
	public static double LAT_STEPPING_DIST;
	public static double LONG_STEPPING_DIST;
	public static int SEARCH_RADIUS = 50; // in meters

	public static final int LOCAL_TIMEZONE = -7;
	public static final int DAYLIGHT_SAVINGS = 1;

	public static final double BINARY_COEFF = 0.000006;
	public static final double ELEVATION_COEFF = 0.00001;
	public static final double ELEVATION_THRESHOLD = 10;

	public static final double LAT_BIAS = -0.000055;
	public static final double LON_BIAS = 0.000075;

	public static Graph GRAPH;
}
