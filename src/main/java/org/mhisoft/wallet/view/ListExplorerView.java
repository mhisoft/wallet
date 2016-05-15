package org.mhisoft.wallet.view;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ListExplorerView {
	DefaultListModel<WalletItem> listModel;

	JList itemList;
	JFrame frame;
	WalletModel model;
	WalletForm form;

	public ListExplorerView(JFrame frame, WalletModel model, JList itemList, WalletForm walletForm) {
		this.frame = frame;
		this.model = model;
		this.itemList = itemList;
		this.form = walletForm;
	}


	public void setupListView() {

		listModel = new DefaultListModel<WalletItem>();
		itemList.setModel(listModel);

		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemList.setSelectedIndex(0);
		//itemList.addListSelectionListener(this);


		model.getItemsFlatList().forEach(item -> {
			listModel.addElement(item);
		});


	}

}
