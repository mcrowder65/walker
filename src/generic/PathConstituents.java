package generic;

public class PathConstituents {

	public double normalPercent;
	public double grassPercent;
	public double cementLotPercent;
	public double highVegetationPercent;
	public double buildingPercent;
	public double stairsPercent;
	public PathConstituents(double normalPercent, double grassPercent, double cementLotPercent,
			double highVegetationPercent, double buildingPercent, double stairsPercent) {
		super();
		this.normalPercent = normalPercent;
		this.grassPercent = grassPercent;
		this.cementLotPercent = cementLotPercent;
		this.highVegetationPercent = highVegetationPercent;
		this.buildingPercent = buildingPercent;
		this.stairsPercent = stairsPercent;
		validate();
	}

	
	public double getNormalPercent() {
		return normalPercent;
	}


	public double getGrassPercent() {
		return grassPercent;
	}


	public double getCementLotPercent() {
		return cementLotPercent;
	}


	public double getHighVegetationPercent() {
		return highVegetationPercent;
	}


	public double getBuildingPercent() {
		return buildingPercent;
	}


	public double getStairsPercent() {
		return stairsPercent;
	}


	private void validate()
	{
		assert normalPercent + grassPercent + cementLotPercent + highVegetationPercent + buildingPercent + stairsPercent == 1.0;
		
	}
	
	
}
