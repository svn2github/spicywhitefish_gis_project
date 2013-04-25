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


public class Application {

	public JFrame frmDirectedStudyFinal;
	public static Application app;
	public JTextField txtOpenedFile;
	public JMenuItem mntmParseFile;
	public List<DataPoint> dataPoints;
	public JTextPane txtpnFileInfo; 
	

	public File file;
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
		txtOpenedFile.setBounds(12, 31, 289, 19);
		frmDirectedStudyFinal.getContentPane().add(txtOpenedFile);
		txtOpenedFile.setColumns(10);
		
		JLabel lblFileInfo = new JLabel("File Info:");
		lblFileInfo.setBounds(12, 62, 70, 15);
		frmDirectedStudyFinal.getContentPane().add(lblFileInfo);
		
		txtpnFileInfo = new JTextPane();
		txtpnFileInfo.setEditable(false);
		txtpnFileInfo.setText("No File Info");
		txtpnFileInfo.setBounds(12, 89, 289, 106);
		frmDirectedStudyFinal.getContentPane().add(txtpnFileInfo);
		
		JMenuBar menuBar = new JMenuBar();
		frmDirectedStudyFinal.setJMenuBar(menuBar);
		
		JMenuItem mntmImport = new JMenuItem("Import");
		mntmImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(Application.app.frmDirectedStudyFinal);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					Application.app.file = chooser.getSelectedFile(); 
					txtOpenedFile.setText(chooser.getSelectedFile().getAbsolutePath());
					mntmParseFile.setEnabled(true);
				}
			}
		});
		menuBar.add(mntmImport);
		
		mntmParseFile = new JMenuItem("Parse File");
		mntmParseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
				Application app = Application.app;
				app.parseFile();
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(frmDirectedStudyFinal, "File not found!");
					mntmParseFile.setEnabled(false);
					txtOpenedFile.setText("No file opened.");					
				}
				StringBuilder sb = new StringBuilder();
				sb.append("Number of measurements: ");
				sb.append(app.dataPoints.size());
				sb.append("\n");
				Application.app.txtpnFileInfo.setText(sb.toString());
			}
		});
		mntmParseFile.setEnabled(false);
		menuBar.add(mntmParseFile);
	}
	
	public void parseFile() throws FileNotFoundException {
		dataPoints = new ArrayList<DataPoint>();
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			file = null;
			throw e;
		}
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
	}
}
