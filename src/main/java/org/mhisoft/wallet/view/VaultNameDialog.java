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

import org.mhisoft.common.util.StringUtils;
import org.mhisoft.wallet.model.WalletSettings;


public class VaultNameDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel labelTitle;

	private JTextField fldNewFileName;
	private String filename;

	//reuse it
	NewVaultCallback callback = null;

	public VaultNameDialog(String title, String label, String filename) {

		this.filename = filename;
		setContentPane(contentPane);
		setModal(true);
		setTitle(title);
		if (label != null)
			this.labelTitle.setText(label);


		getRootPane().setDefaultButton(buttonOK);
		//ViewHelper.setUIManagerFontSize();
		ViewHelper.setFontSize(contentPane, WalletSettings.getInstance().getFontSize());
		contentPane.setPreferredSize(new Dimension(WalletSettings.getInstance().getDimensionX() / 3
				, WalletSettings.getInstance().getDimensionY() / 6
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

		String fname = fldNewFileName.getText().trim();
		if (StringUtils.hasValue(fname)) {

			if (!fname.startsWith(WalletSettings.userHome))
				fname = WalletSettings.userHome + fname;
			if (!fname.endsWith(WalletSettings.fileExt))
				fname = fname + WalletSettings.fileExt;


			//hand it off to the caller
			setVisible(false);
			callback.onOK(fname);
			dispose();

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
	public static void display(String title, String label, String defaultFileNamePattern, NewVaultCallback callback) {

		//create a new dialog every time.
		VaultNameDialog dialog = new VaultNameDialog(title, label, defaultFileNamePattern);
		dialog.callback = callback;
		dialog.setFilename(defaultFileNamePattern);

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

	public String getFilename() {
		if (filename == null) {
			filename = "eVault-" + System.currentTimeMillis();
		}
		filename = WalletSettings.userHome + filename + ".dat";
		return filename;

	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	private void createUIComponents() {
		fldNewFileName = new JTextField();
		String fname = getFilename();
		fldNewFileName.setText(fname);

	}


	public interface NewVaultCallback {

		public void onOK(String fileName);

		public void onCancel();

	}


}
