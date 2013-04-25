import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.Dimension;


public class Application {

	public JFrame frmDirectedStudyFinal;
	public static Application app;
	public JTextField txtOpenedFile;
	public JMenuItem mntmParseFile;
	public List<DataPoint> dataPoints;
	

	public File file;
	public JMenuItem mntmInterpolateValue;
	public JLabel lblInterpolated;
	public JTextField txtInterpolated;
	private JLabel lblX;
	private JLabel lblY;
	private JLabel lblTime;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					Application.app = window;
					window.frmDirectedStudyFinal.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
		
	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
		dataPoints = new ArrayList<DataPoint>();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDirectedStudyFinal = new JFrame();
		frmDirectedStudyFinal.setTitle("Directed Study Final");
		frmDirectedStudyFinal.setBounds(100, 100, 800, 600);
		frmDirectedStudyFinal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDirectedStudyFinal.getContentPane().setLayout(null);
		
		JLabel lblOpenedFile = new JLabel("File Opened:");
		lblOpenedFile.setBounds(12, 12, 98, 15);
		frmDirectedStudyFinal.getContentPane().add(lblOpenedFile);
		
		txtOpenedFile = new JTextField();
		txtOpenedFile.setEditable(false);
		txtOpenedFile.setText("No file opened.");
		txtOpenedFile.setBounds(12, 31, 318, 19);
		frmDirectedStudyFinal.getContentPane().add(txtOpenedFile);
		txtOpenedFile.setColumns(10);
		
		lblInterpolated = new JLabel("Interpolated Value:");
		lblInterpolated.setBounds(12, 118, 149, 15);
		frmDirectedStudyFinal.getContentPane().add(lblInterpolated);
		
		txtInterpolated = new JTextField();
		txtInterpolated.setBounds(165, 116, 165, 19);
		frmDirectedStudyFinal.getContentPane().add(txtInterpolated);
		txtInterpolated.setColumns(10);
		
		lblX = new JLabel("X:");
		lblX.setBounds(12, 78, 98, 15);
		frmDirectedStudyFinal.getContentPane().add(lblX);
		
		lblY = new JLabel("Y:");
		lblY.setBounds(122, 78, 98, 15);
		frmDirectedStudyFinal.getContentPane().add(lblY);
		
		lblTime = new JLabel("Time:");
		lblTime.setBounds(232, 78, 98, 15);
		frmDirectedStudyFinal.getContentPane().add(lblTime);
		
		JMenuBar menuBar = new JMenuBar();
		frmDirectedStudyFinal.setJMenuBar(menuBar);
		
		JMenuItem mntmImport = new JMenuItem("Import");
		mntmImport.setMaximumSize(new Dimension(80, 32767));
		mntmImport.setHorizontalAlignment(SwingConstants.LEFT);
		mntmImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(Application.app.frmDirectedStudyFinal);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					Application.app.file = chooser.getSelectedFile(); 
					txtOpenedFile.setText(chooser.getSelectedFile().getAbsolutePath());
					mntmParseFile.setEnabled(true);
					mntmInterpolateValue.setEnabled(true);
				}
			}
		});
		menuBar.add(mntmImport);
		
		mntmParseFile = new JMenuItem("Parse File");
		mntmParseFile.setMaximumSize(new Dimension(100, 32767));
		mntmParseFile.setHorizontalAlignment(SwingConstants.LEFT);
		mntmParseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
				Application app = Application.app;
				app.parseFile();
				JOptionPane.showMessageDialog(frmDirectedStudyFinal, "File successfully parsed.", "Success", JOptionPane.PLAIN_MESSAGE);
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(frmDirectedStudyFinal, "File not found!", "Error!", JOptionPane.ERROR_MESSAGE);
					mntmParseFile.setEnabled(false);
					mntmInterpolateValue.setEnabled(false);
					txtOpenedFile.setText("No file opened.");					
				}
			}
		});
		mntmParseFile.setEnabled(false);
		menuBar.add(mntmParseFile);
		
		mntmInterpolateValue = new JMenuItem("Interpolate Value");
		mntmInterpolateValue.setMaximumSize(new Dimension(150, 32767));
		mntmInterpolateValue.setHorizontalAlignment(SwingConstants.LEFT);
		mntmInterpolateValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double x = Double.parseDouble(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "X?"));
				double y = Double.parseDouble(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "Y?"));
				int time = Integer.parseInt(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "Time?"));
				lblX.setText("X: "+x);
				lblY.setText("Y: "+y);
				lblTime.setText("Time: "+time);
				double value = DataPoint.interpolateValue(x, y, time, app.dataPoints);
				txtInterpolated.setText(value+"");
			}
		});
		mntmInterpolateValue.setEnabled(false);
		menuBar.add(mntmInterpolateValue);
	}
	
	public void parseFile() throws FileNotFoundException {
		dataPoints = new ArrayList<DataPoint>();
		Scanner sc = null;
		try {
			sc = new Scanner(file);
			int index = 0;
			double[] dPArgs = new double[4];
			while (sc.hasNextDouble()) {
				double val = sc.nextDouble();
				dPArgs[index%4] = val;
				//Have we filled dataPointArgs?
				if (index % 4 == 3) {
					dataPoints.add(new DataPoint(dPArgs));
					dPArgs = new double[4];
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			file = null;
			throw e;
		} finally {
			sc.close();
		}
		
	}
}
