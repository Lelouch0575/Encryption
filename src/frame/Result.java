package frame;

import java.awt.GridBagLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *加密解密之后提示结果
 */
public class Result extends JDialog {
	private JPanel panel;
	private JLabel label;
	
	public Result(JDialog dialog,boolean isOk) {
		super(dialog,true);
		String status = isOk ? "Successful" : "Failed";
		setTitle(status);
		
		setSize(200, 200);
		setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		add(panel);
		
		label = new JLabel(status);
		panel.add(label);
		
		setVisible(true);
	}
}
