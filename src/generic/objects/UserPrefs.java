package generic.objects;

/**
 * Each weight is a double from 0 to 1, 0 being the least important and 1 being
 * the most. i.e. if elevation weight were 1, then choose a long path as long as
 * it means elevation does not change much.
 */
public class UserPrefs extends WalkerObject {

	public static final double MAX_VAL = 100;
	
	private double stairs;
	private double elevation;
	private double wilderness;
	private double grass;
	private double building;
	private double parkingLots;
	private double preferDesignatedPaths;

	public UserPrefs() {
	}

	public UserPrefs(double stairs, double elevation, double wilderness, double grass, double building,
			double parkingLot, double preferDesignatedPaths) {
		setStairs(stairs);
		setElevation(elevation);
		setWilderness(wilderness);
		setGrass(grass);
		setBuilding(building);
		setParkingLots(parkingLot);
		setPreferDesignatedPaths(preferDesignatedPaths);
	}

	public static UserPrefs DEFAULT = new UserPrefs(100, 0, 100, 0, 0, 99, 99);
	public static UserPrefs BLACK_PATHS = new UserPrefs(100, 100, 100, 100, 100, 100, 0);
	public static UserPrefs DISTANCE_ONLY = new UserPrefs(0,0,0,0,0,0,0);
	public double getStairs() {
		return stairs;
	}

	public void setStairs(double stairs) {
		if (stairs < 0) {
			System.err.println("for some reason stairs is less than 0!!!");
		}

		this.stairs = stairs;
	}

	public double getElevation() {

		return elevation;
	}

	public void setElevation(double elevation) {
		if (elevation < 0) {
			System.err.println("for some reason elevation is less than 0!!!");
		}

		this.elevation = elevation;
	}

	public double getWilderness() {
		return wilderness;
	}

	public void setWilderness(double wilderness) {
		this.wilderness = wilderness;
	}

	public double getGrass() {
		return grass;
	}

	public void setGrass(double grass) {
		this.grass = grass;
	}

	public double getBuilding() {
		return building;
	}

	public void setBuilding(double building) {
		this.building = building;
	}

	public double getParkingLots() {
		return parkingLots;
	}

	public void setParkingLots(double parkingLots) {
		this.parkingLots = parkingLots;
	}

	public double getPreferDesignatedPaths() {
		return preferDesignatedPaths;
	}

	public void setPreferDesignatedPaths(double preferDesignatedPaths) {
		this.preferDesignatedPaths = preferDesignatedPaths;
	}

}
