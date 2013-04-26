import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


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
	
	public static double interpolateValue(double x, double y, int t, int N, double p, List<DataPoint> points) {
		DataPoint output = new DataPoint(x, y, t, 0);
		List<DataPoint> neighbors = output.findNeighbors(points, N);
		double sum = 0;
		for(DataPoint neighborPoint : neighbors) {
			sum+= output.getLambda(points, neighborPoint, p) * neighborPoint.measurement;
		}
		return sum;
	}
	
	double getLambda(List<DataPoint> neighbors, DataPoint selectedPoint, double p) {
		double di = this.getDistanceTo(selectedPoint);
		double numerator = Math.pow(1/di, p);
		double denominator = 0;
		for(DataPoint pointElement : neighbors) {
			double dk = this.getDistanceTo(pointElement);
			denominator += Math.pow(1/dk, p);
		}
		assert denominator != 0;
		return numerator / denominator;
	}
	public List<DataPoint> findNeighbors(List<DataPoint> pointList, int numNeighbors) {
		DataPoint[] neighbors = new DataPoint[numNeighbors];
		double[] distances = new double[numNeighbors];
		for (DataPoint element : pointList) {
			double distance = this.getDistanceTo(element);
			for (int i=0; i<neighbors.length; i++) {
				if (neighbors[i] == null) {
					neighbors[i] = element;
					distances[i] = distance;
					break;
				}
				if (distance < distances[i]) {
					neighbors[i] = element;
					distances[i] = distance;
					break;
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

	public static List<DataPoint> parseFile(File f) throws FileNotFoundException {
		List<DataPoint> output = new ArrayList<DataPoint>();
		Scanner sc = null;
		try {
			sc = new Scanner(f);
			int index = 0;
			double[] dPArgs = new double[4];
			while (sc.hasNextDouble()) {
				double val = sc.nextDouble();
				dPArgs[index%4] = val;
				//Have we filled dataPointArgs?
				if (index % 4 == 3) {
					output.add(new DataPoint(dPArgs));
					dPArgs = new double[4];
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			f = null;
			throw e;
		} finally {
			sc.close();
		}
		return output;
	}
	//Compute the error measurements 
	public static double MAE(List<DataPoint> original, List<DataPoint> interpolated){
		assert original.size() == interpolated.size();
		double sum=0;
		for (int i=0;i<original.size();i++){
			sum += Math.abs(interpolated.get(i).measurement-original.get(i).measurement);
		}
		return sum/original.size();
	}
	public static double MSE(List<DataPoint> original, List<DataPoint> interpolated){
		assert original.size() == interpolated.size();
			double sum=0;
			for (int i=0;i<original.size();i++){
				sum += Math.pow(interpolated.get(i).measurement-original.get(i).measurement,2);
			}
			return sum/original.size();
	}
	public static double RMSE(List<DataPoint> original, List<DataPoint> interpolated){
		assert original.size() == interpolated.size();
		double sum=0;
		for (int i=0;i<original.size();i++){
			sum += Math.pow(interpolated.get(i).measurement-original.get(i).measurement,2);
		}
		return Math.sqrt(sum/original.size());
	}
	public static double MARE(List<DataPoint> original, List<DataPoint> interpolated){
		assert original.size() == interpolated.size();
		double sum=0;
		for (int i=0;i<original.size();i++){
			sum += (Math.abs(interpolated.get(i).measurement-original.get(i).measurement))/original.get(i).measurement;
		}
		return sum/original.size();
	}
	public static double MSRE(List<DataPoint> original, List<DataPoint> interpolated){
		assert original.size() == interpolated.size();
		double sum=0;
		for (int i=0;i<original.size();i++){
			sum += (Math.pow(interpolated.get(i).measurement-original.get(i).measurement,2))/original.get(i).measurement;
		}
		return sum/original.size();
	}
	public static double RMSRE(List<DataPoint> original, List<DataPoint> interpolated){
		assert original.size() == interpolated.size();
		double sum=0;
		for (int i=0;i<original.size();i++){
			sum += (Math.pow(interpolated.get(i).measurement-original.get(i).measurement,2))/original.get(i).measurement;
		}
		return Math.sqrt(sum/original.size());
	}
	
	public boolean equals(Object o) {
		if (o instanceof DataPoint) {
			DataPoint dp = (DataPoint)(o);
			return this.x == dp.x && y == dp.y && measurement == dp.measurement && time == dp.time; 
		} else
			return false;
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
