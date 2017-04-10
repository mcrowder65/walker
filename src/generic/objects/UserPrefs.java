package generic.objects;

/**
 * Each weight is a double from 0 to 1, 0 being the least important and 1 being
 * the most. i.e. if elevation weight were 1, then choose a long path as long as
 * it means elevation does not change much.
 */
public class UserPrefs extends WalkerObject {

	private double stairs;
	private double elevation;
	private boolean wilderness;
	private boolean grass;
	private boolean building;
	private boolean parkingLots;
	private boolean preferDesignatedPaths;

	public UserPrefs() {
	}

	public UserPrefs(double stairs, double elevation, boolean wilderness, boolean grass, boolean building,
			boolean parkingLot, boolean preferDesignatedPaths) {
		setStairs(stairs);
		setElevation(elevation);
		setWilderness(wilderness);
		setGrass(grass);
		setBuilding(building);
		setParkingLots(parkingLot);
		setPreferDesignatedPaths(preferDesignatedPaths);
	}
	
	public static UserPrefs DEFAULT = new UserPrefs(0,0,false,true,true,false,false);
	public static UserPrefs BLACK_PATHS = new UserPrefs(0,0,false,false,false,false, true);
	public double getStairs() {
		if (stairs > 1) {
			System.err
					.println("why is stairs greater than 1??? something is going wrong, did you do setStairs before?");
		}
		return stairs;
	}

	public void setStairs(double stairs) {
		if (stairs < 0) {
			System.err.println("for some reason stairs is less than 0!!!");
		}
		if (stairs > 1) {
			stairs /= 100;
		}
		this.stairs = stairs;
	}

	public double getElevation() {
		if (elevation > 1) {
			System.err.println(
					"why is elevation greater than 1??? something is going wrong, did you do setElevation before?");
		}
		return elevation;
	}

	public void setElevation(double elevation) {
		if (elevation < 0) {
			System.err.println("for some reason elevation is less than 0!!!");
		}
		if (elevation > 1) {
			elevation /= 100;
		}
		this.elevation = elevation;
	}

	public boolean isWilderness() {
		return wilderness;
	}

	public void setWilderness(boolean wilderness) {
		this.wilderness = wilderness;
	}

	public boolean isGrass() {
		return grass;
	}

	public void setGrass(boolean grass) {
		this.grass = grass;
	}

	public boolean isBuilding() {
		return building;
	}

	public void setBuilding(boolean building) {
		this.building = building;
	}

	public boolean isParkingLots() {
		return parkingLots;
	}

	public void setParkingLots(boolean parkingLots) {
		this.parkingLots = parkingLots;
	}

	public boolean isPreferDesignatedPaths() {
		return preferDesignatedPaths;
	}

	public void setPreferDesignatedPaths(boolean preferDesignatedPaths) {
		this.preferDesignatedPaths = preferDesignatedPaths;
	}

}
