package frame;

import java.io.*;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/**
 *加密文件的对话框
 */
public class EnDialog extends JDialog{
	private String fileAbsolutePath;
	private boolean encryptFileName;
	
	private JPanel panel;
	private JTextField textField;
	private JButton fileSelectBtn;
	private JCheckBox checkBox;
	private JButton confirmBtn;
	
	String getFileAbsolutePath() {
		return fileAbsolutePath;
	}
	boolean isEncryptFileName() {
		return encryptFileName;
	}
	
	public EnDialog(JFrame frame) {
		super(frame,"Encrypt",true);
		
		fileAbsolutePath = null;
		encryptFileName = false;
		
		setSize(600,250);
		setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		add(panel);
		
		textField = new JTextField(32);
		textField.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				fileAbsolutePath = textField.getText();
			}
		});
		
		
		fileSelectBtn = new JButton("Select File");
		fileSelectBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("~"));
				fileChooser.showOpenDialog(EnDialog.this);
				if(fileChooser.getSelectedFile() != null) {
					fileAbsolutePath = fileChooser.getSelectedFile().getPath();
					textField.setText(fileAbsolutePath);
				}
			}
		});
		
		
		checkBox = new JCheckBox("Encrypt file name");
		checkBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				encryptFileName = true;
			}
		});
		
		
		confirmBtn = new JButton("Confirm");
		confirmBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(fileAbsolutePath == null)
					new FileNotFoundDialog(EnDialog.this);
				else {
					File file = new File(fileAbsolutePath);
					if(file.exists()) {
						new EnPasswordDialog(EnDialog.this);
					}else {
						new FileNotFoundDialog(EnDialog.this);
					}
				}
				dispose();
			}
		});
		
		panel.add(textField,new GBC(0, 0).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		panel.add(fileSelectBtn,new GBC(1, 0).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		panel.add(checkBox,new GBC(0, 1).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST));
		panel.add(confirmBtn,new GBC(1, 1).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		setVisible(true);		
	}
	
	
}
