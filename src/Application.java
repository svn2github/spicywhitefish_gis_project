import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
					try {JOptionPane.showMessageDialog(frmDirectedStudyFinal, "Warning: LOOCV may take a few minutes to complete.", "Warning", JOptionPane.INFORMATION_MESSAGE);
						DataPoint.initPoints(Application.app.dataPoints);
						DataPoint.generateLOOCV(chooser.getSelectedFile());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(Application.app.frmDirectedStudyFinal, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
					JOptionPane.showMessageDialog(Application.app.frmDirectedStudyFinal, "Results successfully generated!");
				}
				
			}
		});
		mntmLoocv.setEnabled(false);
		menuBar.add(mntmLoocv);
		
		JMenuItem mntmErrorSummary = new JMenuItem("Error Summary");
		mntmErrorSummary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File input=null, output=null;
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Select Input LOOCV");
				int returnVal = chooser.showOpenDialog(Application.app.frmDirectedStudyFinal);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					input = chooser.getSelectedFile();
					chooser = new JFileChooser();
					chooser.setDialogTitle("Select Output File");
					returnVal = chooser.showOpenDialog(Application.app.frmDirectedStudyFinal);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						output = chooser.getSelectedFile();
					}
					Application.writeErrorFile(Application.getErrorResults(Application.parseLoocvFile(input)), output);
					JOptionPane.showMessageDialog(Application.app.frmDirectedStudyFinal, "Successfully generated error measures file");
				}
			}
		});
		mntmErrorSummary.setMaximumSize(new Dimension(250, 32767));
		mntmErrorSummary.setHorizontalTextPosition(SwingConstants.LEFT);
		mntmErrorSummary.setHorizontalAlignment(SwingConstants.LEFT);
		menuBar.add(mntmErrorSummary);
	}
	private void parseFile() throws FileNotFoundException {
		this.dataPoints = DataPoint.parseFile(this.file);
	}
	
	public static double[][] parseLoocvFile(File file){
		List<Double>[] lists = new LinkedList[10];
		for (int i=0; i<10; i++) {
			lists[i] = new LinkedList<Double>();
		}
		Scanner sc = null;
		try {
			sc = new Scanner(file);
			int index = 0;
			while (sc.hasNextDouble()) {
				lists[index%10].add(new Double(sc.nextDouble()));
				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		double[][] results = new double[10][];
		for(int method=0; method<results.length; method++) {
			results[method] = new double[lists[method].size()];
			int value=0;
			for(Double d : lists[method]) {
				results[method][value] = d.doubleValue();
				value++;
			}
		}
		return results;
	}
	
	private static double[][] getErrorResults(double[][] lists) {
		ExecutorService executor = Executors.newCachedThreadPool();
		List<List<Future<Double>>> futures = new ArrayList<List<Future<Double>>>();
		for(int i=1; i<lists.length; i++) {
			Callable<Double> mae = new MAECallable(lists[0], lists[i]);
			Callable<Double> mse = new MSECallable(lists[0], lists[i]);
			Callable<Double> rmse = new RMSECallable(lists[0], lists[i]);
			Callable<Double> mare = new MARECallable(lists[0], lists[i]);
			ArrayList<Future<Double>> column = new ArrayList<Future<Double>>();
			column.add(executor.submit(mae));
			column.add(executor.submit(mse));
			column.add(executor.submit(rmse));
			column.add(executor.submit(mare));
			futures.add(column);
		}
		executor.shutdown();
		double[][] output = new double[9][];
		for(int i=0; i<output.length; i++) {
			output[i] = new double[4];
		}
		int idwIndex=0, methodIndex=0;
		for(List<Future<Double>> idwMethod : futures) {
			for(Future<Double> errorValueFuture : idwMethod) {
				try {
					output[idwIndex][methodIndex] = errorValueFuture.get().doubleValue();
				} catch (Exception e) {
					e.printStackTrace();
				}
				methodIndex++;
			}
			methodIndex=0;
			idwIndex++;
		}
		return output;
	}
	
	public static void writeErrorFile(double[][] results, File output) {
		BufferedWriter bw = null;
		try{
			FileWriter fw = new FileWriter(output.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			for(int errorMethodIndex=0; errorMethodIndex<4; errorMethodIndex++) {
				StringBuilder sb = new StringBuilder();
				for(int idwMethodIndex=0; idwMethodIndex<9; idwMethodIndex++) {
					switch(errorMethodIndex) {
					case 0:
						sb.append("MAE");
						break;
					case 1:
						sb.append("MSE");
						break;
					case 2:
						sb.append("RMSE");
						break;
					case 3:
						sb.append("MARE");
					default:
						break;
					}
					sb.append(" for IDW with ");
					sb.append(idwMethodIndex/3+3);
					sb.append(" neighbors and exponent ");
					sb.append(idwMethodIndex%3+1);
					sb.append(": ");
					sb.append(results[idwMethodIndex][errorMethodIndex]);
					sb.append("\n");
				}
				sb.append("\n");
				bw.append(sb.toString());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void hasFile(boolean hasFile) {
		mntmParseFile.setEnabled(hasFile);
		mntmInterpolateValue.setEnabled(hasFile);
		mntmLoocv.setEnabled(hasFile);
	}
}