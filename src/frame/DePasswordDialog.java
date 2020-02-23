package frame;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import core.AESUtil;
import core.IncorrectPasswordException;

/**
 *解密文件输入密码的对话框
 */
public class DePasswordDialog extends JDialog{
	private String password;
	
	private JPanel panel;
	private JLabel label;
	private JPasswordField inputPassword;
	private JButton okBtn;
	
	public DePasswordDialog(DeDialog dialog) {
		super(dialog,"Enter password",true);
		password = null;
		
		setSize(800, 200);
		setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		add(panel);
		
		label = new JLabel("Enter password:");
		inputPassword = new JPasswordField(35);
		okBtn = new JButton("OK");

		okBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				password = new String(inputPassword.getPassword());
				dispose();
				try {
					AESUtil.decrypt(dialog.getFileAbsolutePath(), password, dialog.isDecryptFileName());
				} catch (IncorrectPasswordException e1) { 
					e1.printStackTrace();
					new Result(DePasswordDialog.this, false);
					return ;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				new Result(DePasswordDialog.this,true);
			}
		});
		
		panel.add(label,new GBC(0, 0).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST));
		panel.add(inputPassword,new GBC(1, 0).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		panel.add(okBtn,new GBC(2, 0).setWeight(1, 1).setInsets(20).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		setVisible(true);
	}
}
