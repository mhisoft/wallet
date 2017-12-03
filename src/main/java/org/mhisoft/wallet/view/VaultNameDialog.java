package org.mhisoft.wallet.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.StringUtils;
import org.mhisoft.wallet.model.WalletSettings;


public class VaultNameDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel labelTitle;

	private JTextField fldNewFileName;

	//reuse it
	NewVaultCallback callback = null;

	public VaultNameDialog(String title, String label) {

		setContentPane(contentPane);
		setModal(true);
		setTitle(title);
		if (label!=null)
			this.labelTitle.setText(label);


		getRootPane().setDefaultButton(buttonOK);
		//ViewHelper.setUIManagerFontSize();
		ViewHelper.setFontSize(contentPane, WalletSettings.getInstance().getFontSize());
		contentPane.setPreferredSize(new Dimension(WalletSettings.getInstance().getDimensionX()/3
				, WalletSettings.getInstance().getDimensionY()/6
		));



		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}


	private void onOK() {

		String fname  = fldNewFileName.getText().trim() ;
		if (StringUtils.hasValue(fname)) {

			if (!fname.startsWith(WalletSettings.userHome))
			     fname = WalletSettings.userHome + fname;
			if (!fname.endsWith(WalletSettings.fileExt))
				fname = fname + WalletSettings.fileExt;


			if (FileUtils.fileExists(fname))  {
				DialogUtils.getInstance().error("File with this name already exists. Please use a different name.");
			}
			else {
				//hand it off to the caller
				setVisible(false);
				callback.onOK(fname);
				dispose();
			}
		}
	}

	private void onCancel() {
		// add your code here if necessary
		//setVisible(false);
		callback.onCancel();
		dispose();
	}




	/**
	 * Display the dialog
	 */
	public static void display( String title, String label, NewVaultCallback callback) {

		//create a new dialog every time.
		VaultNameDialog dialog = new VaultNameDialog(title, label );
		dialog.callback = callback;

		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);


//
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//
//			}
//		});


	}

	private void createUIComponents() {
		fldNewFileName = new JTextField();
		String fname = "eVault-" + System.currentTimeMillis();
		fname = WalletSettings.userHome+fname +".dat";
		fldNewFileName.setText(fname);

	}


	public interface NewVaultCallback {

		public void onOK(String fileName);

		public void onCancel();

	}




}
