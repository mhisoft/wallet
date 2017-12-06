package org.mhisoft.wallet.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mhisoft.common.event.EventDispatcher;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ListExplorerView  implements ListSelectionListener {
	DefaultListModel<WalletItem> listModel;

	JList itemList;
	JFrame frame;
	WalletModel model;
	WalletForm form;

	WalletItem currentItem;

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
		itemList.addListSelectionListener(this);
		itemList.addMouseListener(form.jtreeMouseRightClickListener);


		model.getItemsFlatList().forEach(item -> {
			listModel.addElement(item);
		});


	}

	public void closeView() {
		if (listModel!=null)
		listModel.clear();
	}


	public void filterItems(String filter) {
		listModel.clear();

		model.getItemsFlatList().forEach(item -> {
			if (filter==null || item.isMatch(filter)) {
				listModel.addElement(item);
			}
		});

		itemList.setSelectedIndex(0);


	}

	public WalletItem getCurrentItem() {
		return currentItem;
	}

	//This method is required by ListSelectionListener.
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {

			EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "ListExplorerView.valueChanged" , null ));


			if (itemList.getSelectedIndex() == -1) {
				//No selection, disable fire button.
				//fireButton.setEnabled(false);

			} else {
				//Selection, enable the fire button.
				currentItem = (WalletItem)itemList.getSelectedValue();
				model.setCurrentItem(currentItem);

				form.saveCurrentEdit(true);
				form.displayWalletItemDetails(model.getCurrentItem(), DisplayMode.view);
				form.resetHidePassword();


			}
		}
	}

	MouseListener mouseRightClickListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {

			if (SwingUtilities.isRightMouseButton(e)) {

				DialogUtils.getInstance().info("Selected item:" + (model.getCurrentItem() == null ? "none" : model.getCurrentItem().getName()));


			}
		}
	};


}
