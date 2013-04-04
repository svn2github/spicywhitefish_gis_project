import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


public class MainFrame extends JFrame implements Runnable {
	static final int Y_OFFSET=30, X_OFFSET=5;
	BufferedImage bgImage;
	ControlFrame cFrame;
	
	Application app;
	
	public MainFrame() {
	}
	
	public void init() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
		bgImage = ImageIO.read(this.getClass().getClassLoader().getResource("map.png"));
		} catch (Exception e) {
			System.err.println("Failed to load map image");
		}
		this.app = Application.getApplication();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		//Optional TODO: Map coordinate system to US map for extra credit.
		g.drawImage(bgImage, X_OFFSET, Y_OFFSET, bgImage.getWidth(), bgImage.getHeight(), null);
		//TODO: Draw selected points
		if (app.hasVisibleData()) {
			drawDataPoints(g, app.getVisibleDataPoints());
		}
	}
	private void drawDataPoints(Graphics g, List<DataPoint> dpList) {
		for(DataPoint dp : dpList) {
			
		}
	}
	
	
	public void update() {
		this.repaint();
	}
	
	public void run() {
		try {
			this.setVisible(true);
			this.init();
			} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
