import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataPoint {
	private static int count =0;
	private static final int MAX_NEIGHBORS = 5;
	double x;
	double y;
	int time;
	double measurement;
	private DataPoint[] neighbors;

	public DataPoint(double[] args) {
		this.x=args[0];
		this.y=args[1];
		this.time=(int)(args[2]);
		this.measurement=args[3];
		neighbors = new DataPoint[MAX_NEIGHBORS];
	}
	public DataPoint(double x, double y, int time, double measurement) {
		this.x=x;
		this.y=y;
		this.time=time;
		this.measurement=measurement;
		neighbors = new DataPoint[MAX_NEIGHBORS];
	}

	private void initNeighbors(List<DataPoint> pointList) {
		neighbors = new DataPoint[MAX_NEIGHBORS];
		double[] distances = new double[MAX_NEIGHBORS];
		for (DataPoint element : pointList) {
			double distance = this.getDistanceTo(element);
			if (element == this || distance == 0)
				continue;
			boolean hasNull = hasNull(neighbors);
			for (int i=0; i<neighbors.length; i++) {
				if (neighbors[i] == null) {
					neighbors[i] = element;
					distances[i] = distance;
					break;
				}
				if (distance < distances[i] && !hasNull) {
					neighbors[i] = element;
					distances[i] = distance;
					break;
				}
			}
		}
		this.sortNeighbors();
		System.out.println("Initialized: "+count++);
	}
	
	private static boolean hasNull(DataPoint[] points) {
		for(int i=0; i<points.length; i++) {
			if (points[i]==null) {
				return true;
			}
		}
		return false;
		
	}
	
	private void sortNeighbors() {
		for(int i=0; i<neighbors.length; i++) {
			double iDist = this.getDistanceTo(neighbors[i]);
			for(int j=i+1; j<neighbors.length; j++) {
				double jDist = this.getDistanceTo(neighbors[j]);
				if (jDist < iDist) {
					DataPoint swap = neighbors[i];
					neighbors[i]=neighbors[j];
					neighbors[j] = swap;
				}
			}
		}
	}
	
	public static void init(List<DataPoint> points) {
		for (DataPoint data : points) {
			data.initNeighbors(points);
		}
	}
	
	public List<DataPoint> getNeighbors(int num) {
		List<DataPoint> output = new ArrayList<DataPoint>(num);
		for(int i=0; i<num; i++) {
			output.add(neighbors[i]);
		}
		return output;
	}
	
	public static double interpolateValue(double x, double y, int t,  List<DataPoint> points) {
		return interpolateValue(x, y, t, 3, 1, points);
	}

	public static double interpolateValue(double x, double y, int t, int N, double p, List<DataPoint> points) {
		DataPoint output = new DataPoint(x, y, t, 0);
		output.initNeighbors(points);
		double sum = 0;
		for(DataPoint neighborPoint : output.getNeighbors(N)) {
			sum+= output.getLambda(points, neighborPoint, p) * neighborPoint.measurement;
		}
		return sum;
	}
	public double loocv(int N, double p, List<DataPoint> points) {
		double sum = 0;
		for(DataPoint neighborPoint : this.getNeighbors(N)) {
			sum+= this.getLambda(points, neighborPoint, p) * neighborPoint.measurement;
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
	
	public static void generateTestResults(File outFile) throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		List<List<Future<Double>>> futures = new ArrayList<List<Future<Double>>>();
		for(DataPoint element : Application.app.dataPoints) {
			List<Future<Double>> rowFutureList = new ArrayList<Future<Double>>(10);
			for(int N=3; N<=5; N++) {
				for(int p=1; p<=3; p++) {
					Callable<Double> worker = new LoocvCallable(element, Application.app.dataPoints, N, p);
					Future<Double> submit = executor.submit(worker);
					rowFutureList.add(submit);
				}
			}
			futures.add(rowFutureList);
		}
		FileWriter fw = new FileWriter(outFile, false);
		BufferedWriter bw = new BufferedWriter(fw);
		for(int i=0; i< futures.size(); i++) {
			List<Future<Double>> row = futures.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append(Application.app.dataPoints.get(i).measurement);
			sb.append(" ");
			for(Future<Double> future : row) {
				sb.append(future.get().toString());
				sb.append(" ");
			}
			sb.append("\n");
			bw.append(sb.toString());
			System.out.println(sb.toString());
		}
		bw.close();
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
			return this.x == dp.x && y == dp.y && time == dp.time; 
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

class LoocvCallable implements Callable<Double> {
	public DataPoint target;
	private List<DataPoint> points;
	private int N;
	private double p;
	public LoocvCallable(DataPoint target, List<DataPoint> points, int N, double p) {
		this.target = target;
		this.points = points;
		this.N = N;
		this.p = p;
		
	}

	@Override
	public Double call() throws Exception {
		return new Double(target.loocv(N, p, points));
	}
}
