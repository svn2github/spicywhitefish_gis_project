
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
