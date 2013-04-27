import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class DataPoint {
//	private static int count =0;
	private static final int MAX_NEIGHBORS = 5;
	double x;
	double y;
	int time;
	double measurement;
	private DataPoint[] neighbors;
	private boolean isInitialized;

	public DataPoint(double[] args) {
		this.x=args[0];
		this.y=args[1];
		this.time=(int)(args[2]);
		this.measurement=args[3];
		this.neighbors = new DataPoint[MAX_NEIGHBORS];
		isInitialized = false;
	}
	public DataPoint(double x, double y, int time, double measurement) {
		this.x=x;
		this.y=y;
		this.time=time;
		this.measurement=measurement;
		this.neighbors = new DataPoint[MAX_NEIGHBORS];
		isInitialized = false;
	}
	public void init(List<DataPoint> pointList) {
		if (!isInitialized) {
			this.initNeighbors(pointList);
		}
	}

	private void initNeighbors(List<DataPoint> pointList) {
		neighbors = new DataPoint[MAX_NEIGHBORS];
		double[] distances = new double[MAX_NEIGHBORS];
		for (DataPoint element : pointList) {
			double distance = this.getDistanceSquaredTo(element);
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
//		System.out.println("Initialized: "+DataPoint.count++);
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
			double iDist = this.getDistanceSquaredTo(neighbors[i]);
			for(int j=i+1; j<neighbors.length; j++) {
				double jDist = this.getDistanceSquaredTo(neighbors[j]);
				if (jDist < iDist) {
					DataPoint swap = neighbors[i];
					neighbors[i]=neighbors[j];
					neighbors[j] = swap;
				}
			}
		}
	}
	
	public static void initPoints(List<DataPoint> points) {
		ExecutorService executor = Executors.newCachedThreadPool();
		for (DataPoint element : points) {
			executor.submit(new InitNeighborRunnable(element, points));
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
			try {
				executor.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(Application.app.frmDirectedStudyFinal, "Initialization Interrupted!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static double interpolateValue(double x, double y, int t,  List<DataPoint> points) {
		return interpolateValue(x, y, t, 3, 1, points);
	}

	public static double interpolateValue(double x, double y, int t, int N, double p, List<DataPoint> points) {
		DataPoint output = new DataPoint(x, y, t, 0);
		output.init(points);
		for(DataPoint neighbor : output.neighbors) {
			neighbor.init(points);
		}
		double sum = 0;
		for(int i=0; i<N; i++) {
			sum+= output.getLambda(output.neighbors, output.neighbors[i], p) * output.neighbors[i].measurement;
		}
		return sum;
	}
	
	double getLambda(DataPoint[] neighbors, DataPoint selectedPoint, double p) {
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

	public double loocv(int N, double p) {
		double sum = 0;
		for(int i=0; i<N; i++) {
			sum+= this.getLambda(this.neighbors, this.neighbors[i], p) * neighbors[i].measurement;
		}
		return sum;
	}
	public double getDistanceTo(DataPoint other) {
		return Math.sqrt((x-other.x)*(x-other.x)+(y-other.y)*(y-other.y)+(time-other.time)*(time-other.time));
	}
	//Performance optimization for finding closest neighbors
	public double getDistanceSquaredTo(DataPoint other) {
		return (x-other.x)*(x-other.x)+(y-other.y)*(y-other.y)+(time-other.time)*(time-other.time);
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
	
	public static void generateLOOCV(File outFile) throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		List<List<Future<Double>>> futures = new ArrayList<List<Future<Double>>>();
		for(DataPoint element : Application.app.dataPoints) {
			List<Future<Double>> rowFutureList = new ArrayList<Future<Double>>(10);
			for(int N=3; N<=5; N++) {
				for(int p=1; p<=3; p++) {
					Callable<Double> worker = new LoocvCallable(element, N, p);
					Future<Double> submit = executor.submit(worker);
					rowFutureList.add(submit);
				}
			}
			futures.add(rowFutureList);
		}
		executor.shutdown();
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
		}
		bw.close();
	}
	
	//Compute the error doubleValue()s 
	public static double MAE(double[] original, double[] interpolated){
		assert original.length == interpolated.length;
		double sum=0;
		for (int i=0;i<original.length;i++){
			sum += Math.abs(interpolated[i]-original[i]);
		}
		return sum/original.length;
	}
	public static double MSE(double[] original, double[] interpolated){
		assert original.length == interpolated.length;
			double sum=0;
			for (int i=0;i<original.length;i++){
				sum += Math.pow(interpolated[i]-original[i],2);
			}
			return sum/original.length;
	}
	public static double RMSE(double[] original, double[] interpolated){
		assert original.length == interpolated.length;
		double sum=0;
		for (int i=0;i<original.length;i++){
			sum += Math.pow(interpolated[i]-original[i],2);
		}
		return Math.sqrt(sum/original.length);
	}
	public static double MARE(double[] original, double[] interpolated){
		assert original.length == interpolated.length;
		double sum=0;
		for (int i=0;i<original.length;i++){
			sum += (Math.abs(interpolated[i]-original[i]))/original[i];
		}
		return sum/original.length;
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