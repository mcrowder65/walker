package generic;

public class PathConstituents {

	public boolean grass;
	public boolean parkingLot;
	public boolean wilderness;
	public boolean building;
	public double stairsPercent;

	public PathConstituents(boolean grass, boolean parkingLot, boolean wilderness, boolean buliding,
			double stairsPercent) {
		super();
		this.grass = grass;
		this.parkingLot = parkingLot;
		this.wilderness = wilderness;
		this.building = building;
		this.stairsPercent = stairsPercent;
		// validate();
	}

	public boolean getGrass() {
		return grass;
	}

	public boolean parkingLot() {
		return parkingLot;
	}

	public boolean getWilderness() {
		return wilderness;
	}

	public boolean getBuilding() {
		return building;
	}

	public double getStairsPercent() {
		return stairsPercent;
	}

	// private void validate() {
	// assert normalPercent + grassPercent + cementLotPercent +
	// highVegetationPercent + buildingPercent
	// + stairsPercent == 1.0;
	//
	// }

}
