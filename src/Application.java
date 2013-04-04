import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.UIManager;


public class Application {
	
	static MainFrame mFrame;
	static ControlFrame cFrame;
	private static Application singleton;
	
	
	private boolean hasData;
	private List<DataPoint> dataPoints;
	private static Pattern dataPattern;
	static {
		dataPattern = Pattern.compile("(\\d\\.\\d)\\s+(\\d\\.\\d)\\s+(\\d\\.\\d)\\s+(\\d\\.\\d)");
	}
	
	public static void main(String[] args) {
		Application app = Application.getApplication();
	    mFrame = new MainFrame();
	    cFrame = new ControlFrame();
		app.startFrames();
	}
	
	private Application() {
	}
	
	public static Application getApplication() {
		if (Application.singleton == null)
			singleton = new Application();
		return Application.singleton;
	}
	
	private void startFrames() {
	    try {
	        // Set System L&F
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) {}
	   //EventQueue.invokeLater(mFrame);
	    EventQueue.invokeLater(cFrame);
	}
	
	public boolean hasVisibleData() {
		return this.hasData;
	}
	public void readFile(File f) {
		Scanner sc = null;
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(cFrame, "Error: File not Found", "File not found", JOptionPane.ERROR_MESSAGE);
		}
		dataPoints = new ArrayList<DataPoint>();
		while(sc.hasNextLine()) {
			Matcher match = dataPattern.matcher(sc.nextLine());
			if (match.find()) {
				dataPoints.add(new DataPoint(Double.parseDouble(match.group(1)),
											 Double.parseDouble(match.group(2)),
											 Integer.parseInt(match.group(3)), 
											 Double.parseDouble(match.group(4)))
				);
			}
		}
		mFrame.update();
		System.out.println("Min X "+DataPoint.min_x);
		System.out.println("Max X "+DataPoint.max_x);
		System.out.println("Min Y "+DataPoint.min_y);
		System.out.println("Max Y "+DataPoint.max_y);
	}
	public List<DataPoint> getVisibleDataPoints() {
		return this.dataPoints;
		//TODO: determine based on state updated by ControlFrame which data points should be presented.
	}
	
}
