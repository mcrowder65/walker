package generic;

/**
 * Each weight is a double from 0 to 1, 0 being the least important and 1 being
 * the most. i.e. if elevation weight were 1, then choose a long path as long as
 * it means elevation does not change much.
 */
public class UserPrefs {

	private double distance;
	private boolean building;
	private boolean parkinglot;
	private boolean elevator;
	private boolean wilderness;
	private double elevationWeight;
	private double stairsWeight;

	public UserPrefs() {
	}

	public UserPrefs(double distance, double elevationWeight, boolean building, boolean parkinglot, boolean wilderness,
			double stairsWeight, boolean elevator) {
		super();
		this.distance = distance;
		this.elevationWeight = elevationWeight;
		this.building = building;
		this.parkinglot = parkinglot;
		this.wilderness = wilderness;
		this.stairsWeight = stairsWeight;
		this.elevator = elevator;
	}

	public static UserPrefs DISTANCE_ONLY = new UserPrefs(0, 0, false, false, false, 0, false);

	private boolean validateWeight(double weight) {
		if (weight >= 0 && weight <= 1)
			return true;
		else
			System.err.println("ERROR: Weight must be between 0 and 1. Ignored.");
		return false;
	}

	public double getDistanceWeight() {
		return distance;
	}

	public void setDistanceWeight(double distance) {
		// if (!validateWeight(distanceWeight))
		// return;
		this.distance = distance;
	}

	public double getElevationWeight() {

		return elevationWeight;
	}

	public void setElevationWeight(double elevationWeight) {
		if (!validateWeight(elevationWeight))
			return;
		this.elevationWeight = elevationWeight;
	}

	public boolean getBuildingWeight() {
		return building;
	}

	public void setBuildingWeight(boolean building) {
		// if (!validateWeight(buildingWeight))
		// return;

		this.building = building;
	}

	public boolean getParkinglotWeight() {
		return parkinglot;
	}

	public void setParkinglotWeight(boolean parkinglot) {
		// if (!validateWeight(parkinglot))
		// return;

		this.parkinglot = parkinglot;
	}

	public boolean getWildernessWeight() {
		return wilderness;
	}

	public void setWildernessWeight(boolean wilderness) {
		// if (!validateWeight(wildernessWeight))
		// return;

		this.wilderness = wilderness;
	}

	public double getStairsWeight() {
		return stairsWeight;
	}

	public void setStairsWeight(double stairsWeight) {
		if (!validateWeight(stairsWeight))
			return;

		this.stairsWeight = stairsWeight;
	}

	public boolean getElevatorWeight() {
		return elevator;
	}

	public void setElevatorWeight(boolean elevator) {
		// if (!validateWeight(elevatorWeight))
		// return;

		this.elevator = elevator;
	}
}
