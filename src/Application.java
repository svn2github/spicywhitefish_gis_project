import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Application {

	public JFrame frmDirectedStudyFinal;
	public static Application app;
	public JTextField txtOpenedFile;
	public List<DataPoint> dataPoints;
	

	public File file;
	public JMenuItem mntmInterpolateValue;
	public JMenuItem mntmImport; 
	public JMenuItem mntmParseFile;
	public JMenuItem mntmLoocv;
	public JLabel lblInterpolated;
	public JTextField txtInterpolated;
	private JLabel lblX;
	private JLabel lblY;
	private JLabel lblTime;
	private JLabel lblN;
	private JLabel lblP;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//File f = new File("data/test_data.txt");
		//writeErrorFile(extractIntData(f));
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.initialize();
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
		lblInterpolated.setBounds(12, 159, 149, 15);
		frmDirectedStudyFinal.getContentPane().add(lblInterpolated);
		
		txtInterpolated = new JTextField();
		txtInterpolated.setBounds(157, 157, 165, 19);
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
		
		lblN = new JLabel("Number of Neighbors:");
		lblN.setBounds(12, 105, 250, 15);
		frmDirectedStudyFinal.getContentPane().add(lblN);
		
		lblP = new JLabel("P Value: ");
		lblP.setBounds(12, 132, 98, 15);
		frmDirectedStudyFinal.getContentPane().add(lblP);
		
		JMenuBar menuBar = new JMenuBar();
		frmDirectedStudyFinal.setJMenuBar(menuBar);
		
		mntmImport = new JMenuItem("Import");
		mntmImport.setMaximumSize(new Dimension(80, 32767));
		mntmImport.setHorizontalAlignment(SwingConstants.LEFT);
		mntmImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(Application.app.frmDirectedStudyFinal);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					Application.app.file = chooser.getSelectedFile(); 
					txtOpenedFile.setText(chooser.getSelectedFile().getAbsolutePath());
					Application.app.hasFile(true);
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
					Application.app.hasFile(false);
					txtOpenedFile.setText("No file opened.");					
				}
			}
		});
		mntmParseFile.setEnabled(false);
		menuBar.add(mntmParseFile);
		
		mntmInterpolateValue = new JMenuItem("Interpolate Value");
		mntmInterpolateValue.setMaximumSize(new Dimension(210, 32767));
		mntmInterpolateValue.setHorizontalAlignment(SwingConstants.LEFT);
		mntmInterpolateValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double x = Double.parseDouble(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "X?"));
				double y = Double.parseDouble(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "Y?"));
				int time = Integer.parseInt(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "Time?"));
				int n = Integer.parseInt(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "Number of Neighbors?"));
				int p = Integer.parseInt(JOptionPane.showInputDialog(Application.app.frmDirectedStudyFinal, "P Value?"));
				lblX.setText("X: "+x);
				lblY.setText("Y: "+y);
				lblTime.setText("Time: "+time);
				lblN.setText("Number of Neighbors: "+n);
				lblP.setText("P Value: "+p);
				double value = DataPoint.interpolateValue(x, y, time, n, p, app.dataPoints);
				txtInterpolated.setText(value+"");
			}
		});
		mntmInterpolateValue.setEnabled(false);
		menuBar.add(mntmInterpolateValue);
		
		mntmLoocv = new JMenuItem("LOOCV");
		mntmLoocv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(Application.app.frmDirectedStudyFinal);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					Application.app.file = chooser.getSelectedFile(); 
					try {
						DataPoint.generateTestResults(chooser.getSelectedFile());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(Application.app.frmDirectedStudyFinal, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
					JOptionPane.showMessageDialog(Application.app.frmDirectedStudyFinal, "Results successfully generated!");
				}
				
			}
		});
		mntmLoocv.setEnabled(false);
		menuBar.add(mntmLoocv);
	}
	private void parseFile() throws FileNotFoundException {
		this.dataPoints = DataPoint.parseFile(this.file);
		DataPoint.init(this.dataPoints);
	}
	//create file with error reports called error_statistics_idw.txt inside of data.
	public static void writeErrorFile(LinkedList<Double>[] lists){
		String content = "";
		content+="MAE for IDW with 3 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[1]))+"\n";
		content+="MAE for IDW with 3 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[2]))+"\n";
		content+="MAE for IDW with 3 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[3]))+"\n";
		content+="MAE for IDW with 4 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[4]))+"\n";
		content+="MAE for IDW with 4 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[5]))+"\n";
		content+="MAE for IDW with 4 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[6]))+"\n";
		content+="MAE for IDW with 5 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[7]))+"\n";
		content+="MAE for IDW with 5 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[8]))+"\n";
		content+="MAE for IDW with 5 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MAE(lists[0],lists[9]))+"\n\n";
		
		content+="MSE for IDW with 3 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[1]))+"\n";
		content+="MSE for IDW with 3 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[2]))+"\n";
		content+="MSE for IDW with 3 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[3]))+"\n";
		content+="MSE for IDW with 4 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[4]))+"\n";
		content+="MSE for IDW with 4 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[5]))+"\n";
		content+="MSE for IDW with 4 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[6]))+"\n";
		content+="MSE for IDW with 5 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[7]))+"\n";
		content+="MSE for IDW with 5 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[8]))+"\n";
		content+="MSE for IDW with 5 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MSE(lists[0],lists[9]))+"\n\n";
		
		content+="RMSE for IDW with 3 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[1]))+"\n";
		content+="RMSE for IDW with 3 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[2]))+"\n";
		content+="RMSE for IDW with 3 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[3]))+"\n";
		content+="RMSE for IDW with 4 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[4]))+"\n";
		content+="RMSE for IDW with 4 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[5]))+"\n";
		content+="RMSE for IDW with 4 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[6]))+"\n";
		content+="RMSE for IDW with 5 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[7]))+"\n";
		content+="RMSE for IDW with 5 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[8]))+"\n";
		content+="RMSE for IDW with 5 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.RMSE(lists[0],lists[9]))+"\n\n";
		
		content+="MARE for IDW with 3 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[1]))+"\n";
		content+="MARE for IDW with 3 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[2]))+"\n";
		content+="MARE for IDW with 3 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[3]))+"\n";
		content+="MARE for IDW with 4 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[4]))+"\n";
		content+="MARE for IDW with 4 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[5]))+"\n";
		content+="MARE for IDW with 4 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[6]))+"\n";
		content+="MARE for IDW with 5 neighbors and exponent 1: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[7]))+"\n";
		content+="MARE for IDW with 5 neighbors and exponent 2: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[8]))+"\n";
		content+="MARE for IDW with 5 neighbors and exponent 3: ";
		content+=Double.toString(DataPoint.MARE(lists[0],lists[9]))+"\n\n";
		
		try{
			File errFile= new File("data/error_statistics_idw.txt");
			if (!errFile.exists()) {
				errFile.createNewFile();
			}
			FileWriter fw = new FileWriter(errFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static LinkedList<Double>[] extractIntData(File file){
		LinkedList<Double> original = new LinkedList<Double>();
		LinkedList<Double> p3n1 = new LinkedList<Double>();
		LinkedList<Double> p3n2 = new LinkedList<Double>();
		LinkedList<Double> p3n3 = new LinkedList<Double>();
		LinkedList<Double> p4n1 = new LinkedList<Double>();
		LinkedList<Double> p4n2 = new LinkedList<Double>();
		LinkedList<Double> p4n3 = new LinkedList<Double>();
		LinkedList<Double> p5n1 = new LinkedList<Double>();
		LinkedList<Double> p5n2 = new LinkedList<Double>();
		LinkedList<Double> p5n3 = new LinkedList<Double>();
		
		LinkedList[] lists = {original,p3n1,p3n2,p3n3,p4n1,p4n2,p4n3,p5n1,p5n2,p5n3};
		Scanner sc = null;
		try {
			sc = new Scanner(file);
			int index = 0;
			while (sc.hasNextDouble()){
				lists[index].add(sc.nextDouble());
				if (index+1%10==0){
					index=0;
					continue;
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lists;
	}
	private void hasFile(boolean hasFile) {
		mntmParseFile.setEnabled(hasFile);
		mntmInterpolateValue.setEnabled(hasFile);
		mntmLoocv.setEnabled(hasFile);
	}
}
