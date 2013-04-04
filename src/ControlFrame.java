import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class ControlFrame extends JFrame implements Runnable {

	private JPanel contentPane;
	JButton btnSelectDataFile;
	
	Application app;
	/**
	 * Create the frame.
	 */
	public ControlFrame() {
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 9291, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnSelectDataFile = new JButton("Select Data File");
		btnSelectDataFile.setBounds(25, 25, 156, 19);
		btnSelectDataFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					Application.getApplication().readFile(fc.getSelectedFile());
				}
			}
		});
		contentPane.add(btnSelectDataFile);
	}
	
	public void init() {
		this.app = Application.getApplication();	
	}
	
	public void run() {
		try {
			this.setVisible(true);
			this.init();
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void selectDataFile() {
		
	}
}
