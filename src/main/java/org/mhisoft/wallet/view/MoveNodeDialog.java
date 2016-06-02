package org.mhisoft.wallet.view;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.service.ServiceRegistry;

public class MoveNodeDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel labelMoveNode;
	private JComboBox<WalletItem> comboCategories;
	//String[] petStrings = {"Bird", "Cat", "Dog", "Rabbit", "Pig"};

	//reuse it
	SelectCategoryCallback callback = null;
	WalletItem currentItem ;

	public MoveNodeDialog() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		ViewHelper.setUIManagerFontSize();

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
		WalletItem moveTo = (WalletItem) comboCategories.getSelectedItem();
		//hand it off to the caller
		setVisible(false);
		callback.onSelectWalletItem(moveTo);
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		//setVisible(false);
		dispose();
	}



	private void createUIComponents() {

		List<WalletItem>    items  = new ArrayList<>() ;
		for (int i = 1; i < ServiceRegistry.instance.getWalletModel().getItemsFlatList().size(); i++) {
			WalletItem item = ServiceRegistry.instance.getWalletModel().getItemsFlatList().get(i);
			if (item.getType() == ItemType.category && !item.equals(currentItem))
				//comboCategories.addItem(item.getName());
				items.add(item)   ;
		}

		comboCategories = new JComboBox<>(  items.toArray(new WalletItem[items.size()])  );
	}



	/**
	 * Display the dialog
	 *
	 * @param currentItem
	 */
	public void display(WalletItem currentItem, SelectCategoryCallback callback) {

		//create a new dialog every time.
		MoveNodeDialog dialog = new MoveNodeDialog();
		dialog.callback = callback;
		dialog.currentItem = currentItem;


		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

	}


	public interface SelectCategoryCallback {
		public void onSelectWalletItem(WalletItem selectedItem);

	}




}
