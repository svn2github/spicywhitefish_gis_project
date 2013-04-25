import java.util.Arrays;
import java.util.List;


public class DataPoint {
	double x;
	double y;
	int time;
	double measurement;

	public DataPoint(double[] args) {
		this.x=args[0];
		this.y=args[1];
		this.time=(int)(args[2]);
		this.measurement=args[3];
	}
	public DataPoint(double x, double y, int time, double measurement) {
		this.x=x;
		this.y=y;
		this.time=time;
		this.measurement=measurement;
	}

	public static double interpolateValue(double x, double y, int t,  List<DataPoint> points) {
		return interpolateValue(x, y, t, 3, 1, points);
	}
	
	public static double interpolateValue(double x, double y, int t, int N, int p, List<DataPoint> points) {
		DataPoint output = new DataPoint(x, y, t, 0);
		List<DataPoint> neighbors = output.findNeighbors(points, N);
		double sum = 0;
		for(DataPoint neighborPoint : neighbors) {
			sum+= output.getLambda(points, neighborPoint, p) * neighborPoint.measurement;
		}
		return sum;
	}
	
	double getLambda(List<DataPoint> points, DataPoint selectedPoint, int p) {
		double di = this.getDistanceTo(selectedPoint);
		double numerator = Math.pow(1/di, p);
		double denominator = 0;
		for(DataPoint pointElement : points) {
			double dk = this.getDistanceTo(pointElement);
			denominator += Math.pow(1/dk, p);
		}
		return numerator / denominator;
	}
	private List<DataPoint> findNeighbors(List<DataPoint> pointList, int numNeighbors) {
		DataPoint[] neighbors = new DataPoint[numNeighbors];
		double[] distances = new double[numNeighbors];
		for(int i=0; i<distances.length; i++) {
			distances[i] = Double.MAX_VALUE;
		}
		for (DataPoint pointElement : pointList) {
			double distance = this.getDistanceTo(pointElement);
			for(int i=0; i<distances.length; i++) {
				if (distance < distances[i]) {
					distances[i] = distance;
					neighbors[i] = pointElement;
				}
			}
		}
		return Arrays.asList(neighbors);
	}
	public double getDistanceTo(DataPoint other) {
		double dx = x-other.x;
		double dy = y - other.y;
		double dt = time - other.time;
		return Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)+Math.pow(dt,2));
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(x);
		sb.append(" ");
		sb.append(y);
		sb.append(" ");
		sb.append(time);
		sb.append(" ");
		sb.append(measurement);
		return sb.toString();
	}
}
