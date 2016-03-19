package main.java.com.mhisoft.wallet;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletForm {

	JFrame frame;

	private JTree tree1;
	private JTextField fldName;
	private JPanel mainPanel;
	private JTextField fldURL;
	private JTextArea fldNotes;
	private JPasswordField fldPassword;
	private JTextField fldUserName;
	private JTextField fldAccountNumber;
	private JSpinner fldFontSize;

	private JLabel labelName;
	private JLabel labelURL;
	private JLabel labelNotes;
	private JLabel labelPassword;
	private JLabel labelUserName;
	private JLabel labelAccount;
	private JLabel labelFontSize;


	public WalletForm() {



	}

	public void init() {
		frame = new JFrame("Wallet 1.0");
		frame.setContentPane(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1200, 800));

		frame.pack();

		/*position it*/
//		//frame.setLocationRelativeTo(null);  // *** this will center your app ***
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		int x = (int) b.getX();
		int y = (int) b.getY();
		frame.setLocation(x + 100, y);
//
//		btnEditRootDir.setBorder(null);
//		btnCancel.setText("Close");
//		//resize();

		setupTree();
		setupFontSpinner();

		frame.setVisible(true);


	}

	public void setupTree() {
		tree1.setModel(null);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root Node");
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		tree1.setModel(treeModel);

	}


	public void setupFontSpinner() {

		int fontSize = tree1.getFont().getSize();

		SpinnerModel spinnerModel = new SpinnerNumberModel(fontSize, //initial value
				10, //min
				fontSize + 20, //max
				2); //step
		fldFontSize.setModel(spinnerModel);
		fldFontSize.addChangeListener(new ChangeListener() {
			  @Override
			  public void stateChanged(ChangeEvent e) {
				  SpinnerModel spinnerModel = fldFontSize.getModel();
				  Float newFontSize = Float.valueOf((Integer) spinnerModel.getValue());
				  Font original = labelName.getFont();
				  Font newFont = original.deriveFont(newFontSize);

				  labelName.setFont(newFont);

				  tree1.setFont(newFont);
				  fldFontSize.setFont(newFont);
				  fldAccountNumber.setFont(newFont);
				  fldName.setFont(newFont);
				  fldNotes.setFont(newFont);
				  fldPassword.setFont(newFont);
				  fldURL.setFont(newFont);
				  fldUserName.setFont(newFont);

				  //FontUtils.setUIFont(new javax.swing.plaf.FontUIResource(new Font("Arial", Font.PLAIN, newFontSize)));



			  }
			}
		);


	}




	public static void main(String[] args) {
		WalletForm form = new WalletForm();
		form.init();
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}
}


