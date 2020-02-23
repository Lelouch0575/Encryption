package frame;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import core.AESUtil;

/**
 *加密文件输入密码的对话框
 */
public class EnPasswordDialog extends JDialog {
	private String password;
	private String cfmpassword;
	
	private JPanel panel;
	private JLabel firstLabel;
	private JLabel secondLabel;
	private JLabel commentLabel;
	private JPasswordField inputPassword;
	private JPasswordField confirmPassword;
	private JButton okBtn;
	
	public EnPasswordDialog(EnDialog dialog) {
		super(dialog,"Enter password",true);
		password = null;
		cfmpassword = null;
		
		setSize(800, 300);
		setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		add(panel);
		
		
		firstLabel = new JLabel("Enter the password:");
		secondLabel = new JLabel("Enter the password again:");
		commentLabel = new JLabel();
		
		inputPassword = new JPasswordField(35);
		inputPassword.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				password = new String(inputPassword.getPassword());
				if(!password.equals(cfmpassword)) {
					commentLabel.setText("Passwords are inconsistent!");
				}else {
					commentLabel.setText("OK!");
				}
			}
		});
		
		
		confirmPassword = new JPasswordField(35);
		confirmPassword.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				cfmpassword = new String(confirmPassword.getPassword());
				if(!cfmpassword.equals(password)) {
					commentLabel.setText("Passwords are inconsistent!");
				}else {
					commentLabel.setText("OK!");
				}
			}
		});
		
			
		okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				password = new String(inputPassword.getPassword());
				cfmpassword = new String(confirmPassword.getPassword());
				
				if(password.equals(cfmpassword)) {
					dispose();
					try {
						AESUtil.encrypt(dialog.getFileAbsolutePath(), password, dialog.isEncryptFileName());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				
				new Result(EnPasswordDialog.this,true);
				}
			}
		});
		
		panel.add(firstLabel,new GBC(0, 0).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST));
		panel.add(inputPassword,new GBC(1,0).setWeight(1, 1).setInsets(0,20,0,20).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		panel.add(secondLabel,new GBC(0, 1).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST));
		panel.add(confirmPassword,new GBC(1,1).setWeight(1, 1).setInsets(0,20,0,20).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		panel.add(commentLabel,new GBC(0,2).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST));
		panel.add(okBtn,new GBC(1,2).setWeight(1, 1).setInsets(20).setAnchor(GBC.EAST));

		setVisible(true);
	}
	
}
