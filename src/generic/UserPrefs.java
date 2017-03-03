package generic;

/**
 * Each weight is a double from 0 to 1, 0 being the least important and 1 being the most.
 * i.e. if elevation weight were 1, then choose a long path as long as it means elevation does not change much.
 */
public class UserPrefs {

	private double distanceWeight;
	private double elevationWeight;
	private double buildingWeight;
	private double parkinglotWeight;
	private double wildernessWeight;
	private double stairsWeight;
	private double elevatorWeight;
	public UserPrefs() {}
	public UserPrefs(double distanceWeight, double elevationWeight, double buildingWeight, double parkinglotWeight,
			double wildernessWeight, double stairsWeight, double elevatorWeight) {
		super();
		this.distanceWeight = distanceWeight;
		this.elevationWeight = elevationWeight;
		this.buildingWeight = buildingWeight;
		this.parkinglotWeight = parkinglotWeight;
		this.wildernessWeight = wildernessWeight;
		this.stairsWeight = stairsWeight;
		this.elevatorWeight = elevatorWeight;
	}
	public static UserPrefs DISTANCE_ONLY = new UserPrefs(1,0,0,0,0,0,0);
	
	private boolean validateWeight(double weight) {
		if (weight >= 0 && weight <= 1)
			return true;
		else
			System.err.println("ERROR: Weight must be between 0 and 1. Ignored.");
		return false;
	}
	
	public double getDistanceWeight() {
		return distanceWeight;
	}
	public void setDistanceWeight(double distanceWeight) {
		if (!validateWeight(distanceWeight))
			return;
		this.distanceWeight = distanceWeight;
	}
	public double getElevationWeight() {
		
		return elevationWeight;
	}
	public void setElevationWeight(double elevationWeight) {
		if (!validateWeight(elevationWeight))
			return;
		this.elevationWeight = elevationWeight;
	}
	public double getBuildingWeight() {
		return buildingWeight;
	}
	public void setBuildingWeight(double buildingWeight) {
		if (!validateWeight(buildingWeight))
			return;
		
		this.buildingWeight = buildingWeight;
	}
	public double getParkinglotWeight() {
		return parkinglotWeight;
	}
	public void setParkinglotWeight(double parkinglotWeight) {
		if (!validateWeight(parkinglotWeight))
			return;
		
		this.parkinglotWeight = parkinglotWeight;
	}
	public double getWildernessWeight() {
		return wildernessWeight;
	}
	public void setWildernessWeight(double wildernessWeight) {
		if (!validateWeight(wildernessWeight))
			return;
		
		this.wildernessWeight = wildernessWeight;
	}
	public double getStairsWeight() {
		return stairsWeight;
	}
	public void setStairsWeight(double stairsWeight) {
		if (!validateWeight(stairsWeight))
			return;
		
		this.stairsWeight = stairsWeight;
	}
	public double getElevatorWeight() {
		return elevatorWeight;
	}
	public void setElevatorWeight(double elevatorWeight) {
		if (!validateWeight(elevatorWeight))
			return;
		
		this.elevatorWeight = elevatorWeight;
	}
}
