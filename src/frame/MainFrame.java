package frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Ö÷´°¿Ú
 */
public class MainFrame extends JFrame {
	private JPanel buttonPanel;
	private JButton encryptBtn;
	private JButton decryptBtn;
	
	public MainFrame() {
		super();
		setTitle("Encryption");
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
	
		setSize(screenWidth / 5, screenHeight / 2);
		//setLocationByPlatform(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		buttonPanel = new JPanel();
		encryptBtn = new JButton("Encrypt");
		decryptBtn = new JButton("Decrypt");
		add(buttonPanel);
		
		buttonPanel.setLayout(new GridBagLayout());
		
		encryptBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				new EnDialog(MainFrame.this);
			}
		});
		decryptBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				new DeDialog(MainFrame.this);
			}
		});
		
		buttonPanel.add(encryptBtn,new GBC(0, 0).setWeight(1, 1).setInsets(50));
		buttonPanel.add(decryptBtn,new GBC(0, 1).setWeight(1, 1).setInsets(50));
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainFrame();
	}
	
}
