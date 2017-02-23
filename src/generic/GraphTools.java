package generic;

public class GraphTools {

	private double[][] distance;
	private double[][] elevation;

	public GraphTools(double[][] distance, double[][] elevation) {
		this.distance = distance;
		this.elevation = elevation;
	}

	public GraphTools() {

	}

	public double[][] getDistance() {
		return distance;
	}

	public void setDistance(double[][] distance) {
		this.distance = distance;
	}

	public void setElevation(double[][] elevation) {
		this.elevation = elevation;
	}

}
