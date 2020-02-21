package frame;

import java.io.*;
import java.awt.GridBagLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *解密文件对话框
 */
public class DeDialog extends JDialog{
	private String fileAbsolutePath;
	private boolean decryptFileName;
	
	private JPanel panel;
	private JTextField textField;
	private JButton fileSelectBtn;
	private JCheckBox checkBox;
	private JButton confirmBtn;
	
	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}
	public boolean isDecryptFileName() {
		return decryptFileName;
	}
	
	public DeDialog(JFrame frame) {
		super(frame,"Decrypt",true);
		
		fileAbsolutePath = null;
		decryptFileName = false;
		
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
				fileChooser.setFileFilter(new FileNameExtensionFilter("Encrypted files(*.crypt)", "crypt"));
				fileChooser.showOpenDialog(DeDialog.this);
				if(fileChooser.getSelectedFile() != null) {
					fileAbsolutePath = fileChooser.getSelectedFile().getPath();
					textField.setText(fileAbsolutePath);
				}
			}
		});
		
		checkBox = new JCheckBox("Decrypt file name");
		checkBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				decryptFileName = true;
			}
		});
		
		confirmBtn = new JButton("Confirm");
		confirmBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(fileAbsolutePath == null)
					new FileNotFoundDialog(DeDialog.this);
				else {
					File file = new File(fileAbsolutePath);
					if(file.exists()) {
						new DePasswordDialog(DeDialog.this);
					}else {
						new FileNotFoundDialog(DeDialog.this);
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