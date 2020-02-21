package frame;

import java.awt.GridBagLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *��ָ���ļ�δ�ҵ��������˶Ի���
 */
public class FileNotFoundDialog extends JDialog {
	private JPanel panel;
	private JLabel label;
	
	public FileNotFoundDialog(JDialog dialog) {
		super(dialog,"Error",true);
		
		setSize(200,200);
		setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		add(panel);
		
		label = new JLabel("File not found!");
		panel.add(label);
		
		setVisible(true);
	}
}
