
public class DataPoint {
	double x;
	double y;
	int time;
	double measurement;

	static double min_x;
	static double max_x;
	static double min_y;
	static double max_y;
	static boolean hasData;
	
	public DataPoint(double x, double y, int time, double measurement) {
		this.x=x;
		this.y=y;
		this.time=time;
		this.measurement=measurement;
		if (hasData) {
			if (x<min_x) {
				min_x = x;
			} else {
				if (x>max_x) {
					max_x = x;
				}
			}
			if (x<min_y) {
				min_y = y;
			} else {
				if (x>max_y) {
					max_y = y;
				}
			}
		} else {
			min_x = x;
			max_x = x;
			min_y = y;
			max_y = y;
		}
	}
	public static void resetData() {
		hasData=false;
	}
}
