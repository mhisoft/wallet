/*
 *
 *  * Copyright (c) 2014- MHISoft LLC and/or its affiliates. All rights reserved.
 *  * Licensed to MHISoft LLC under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. MHISoft LLC licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 *
 */

package org.mhisoft.wallet.view;

import java.util.ArrayList;
import java.util.List;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.mhisoft.common.util.ReflectionUtil;
import org.mhisoft.wallet.WalletMain;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.action.CloseWalletAction;
import org.mhisoft.wallet.action.SaveWalletAction;
import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description: The Wallet Form UI
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletForm {

	JFrame frame;

	JTree tree;
	JPanel mainPanel;


	JTextField fldName;
	JTextField fldURL;
	JTextArea fldNotes;
	JPasswordField fldPassword;
	JTextField fldUserName;
	JTextField fldAccountNumber;


	JSpinner fldFontSize;


	JTabbedPane tabbedPane1;
	JButton btnTogglePasswordView;
	JSplitPane splitPanel;
	JButton btnAddNode;
	JButton btnDeleteNode;
	JPanel treeButtonPanel;
	JButton btnEditForm;
	JButton btnSaveForm;
	JButton btnCancelEdit;
	JButton btnClose;

	JLabel labelName;
	JLabel labelURL;
	JLabel labelUsername;
	JLabel labelNotes;
	JLabel labelPassword;
	JLabel labelAccount;
	JLabel labelFontSize;
	JTextField fldPin;
	JTextField fldExpMonth;
	JTextField fldExpYear;
	JTextField fldFilter;
	JList itemList;
	JTextField fldAccountType;
	JTextField fldPhone;
	JTextField fldDetail1;
	JTextField fldDetail2;
	JTextField fldDetail3;
	JLabel labelPin;
	JLabel labelExpMonth;
	JLabel labelExpYear;
	JLabel labelAccountType;
	JLabel labelPhone;
	JLabel labelDetail1;
	JLabel labelDetail2;
	JLabel labelDetail3;
	JLabel labelLastMessage;
	 JButton btnFilter;


	JMenuBar menuBar;
	JMenu menuFile;
	JMenuItem menuOpen, menuClose;
	//JRadioButtonMenuItem rbMenuItem;
	//JCheckBoxMenuItem cbMenuItem;

	List<Component> componetsList;
	WalletModel model;
	TreeExploreView treeExploreView;
	ListExplorerView listExploreView;
	ItemDetailView itemDetailView;



	boolean hidePassword = true;

	public WalletModel getModel() {
		return model;
	}

	public void setModel(WalletModel model) {
		this.model = model;
	}

	public WalletForm() {
		model = new WalletModel();
		treeExploreView = new TreeExploreView(frame, model, tree, this);
		listExploreView= new ListExplorerView(frame, model, itemList, this);
		itemDetailView = new ItemDetailView(model, this);

		ServiceRegistry.instance.registerSingletonService(this);


//		fldName.getDocument().addDocumentListener(new MyDocumentListener(fldName, "name", model));
//		fldName.getDocument().addDocumentListener(new MyDocumentListener(fldName, "URL", model));

		btnEditForm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemDetailView.editDetailAction();
			}
		});
		btnCancelEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemDetailView.cancelEditAction();
			}
		});
		btnSaveForm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					saveCurrentEdit(false);

			}
		});


		btnTogglePasswordView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hidePassword = !hidePassword;
				updatePasswordChar();
			}
		});



		//constructor
		btnAddNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				treeExploreView.addItem();
			}
		});

		btnDeleteNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				treeExploreView.removeItem();
			}
		});

		btnFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
	}

	public void resetHidePassword() {
		hidePassword = true;
		updatePasswordChar();
	}

	public void updatePasswordChar() {
		if (hidePassword)
			fldPassword.setEchoChar('*');
		else
			fldPassword.setEchoChar((char) 0);
	}


	public boolean hasUnsavedData() {
		return itemDetailView.currentMode!=DisplayMode.view;
	}

	public JFrame getFrame() {
		return frame;
	}

	public JSplitPane getSplitPanel() {
		return splitPanel;
	}

	public void init() {
		frame = new JFrame("MHISoft eWallet " + WalletMain.version);
		frame.setContentPane(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



		frame.setPreferredSize(new Dimension(WalletSettings.getInstance().getDimensionX(), WalletSettings.getInstance().getDimensionY()));


//removes the title bar with X button.
//		frame.setUndecorated(true);
//		frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

		frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);

		frame.pack();

		DialogUtils.create(frame);

		setupMenu();



		componetsList = getAllComponents(frame);
		componetsList.add(menuBar);
		componetsList.add(menuFile);
		componetsList.add(menuOpen);
		componetsList.add(menuClose);
		componetsList.add(itemList);

		/*position it*/
		frame.setLocationRelativeTo(null);  // *** this will center your app ***
		//based on mouse location.
		//		PointerInfo a = MouseInfo.getPointerInfo();
		//		Point b = a.getLocation();
		//		int x = (int) b.getX();
		//		int y = (int) b.getY();
		//		frame.setLocation(x + 100, y);

		loadInPreferences();


		setupFontSpinner();


		frame.setVisible(true);

		PasswordForm passwordForm = new PasswordForm();
		passwordForm.showPasswordForm(this);


		//remove the X buttons
		//frame.setUndecorated(true);





//		Runtime.getRuntime().addShutdownHook(new Thread()
//		{
//			@Override
//			public void run() {
//				CloseWalletAction closeWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CloseWalletAction.class);
//				closeWalletAction.execute();
//			}
//		});


	}





	protected void setupMenu() {

		ActionListener closeAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CloseWalletAction closeWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CloseWalletAction.class);
				ActionResult r = closeWalletAction.execute();
				if (r.isSuccess())
					exit();

			}
		};

		//menu
		//Create the menu bar.
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);
		menuOpen = new JMenuItem("Open", KeyEvent.VK_O);
		menuFile.add(menuOpen);
		menuClose = new JMenuItem("Close", KeyEvent.VK_C);
		menuFile.add(menuClose);
		frame.setJMenuBar(menuBar);
		menuClose.addActionListener(closeAction);

		btnClose.addActionListener(closeAction);


	}

	void loadInPreferences() {
		//divider location
		splitPanel.setDividerLocation(WalletSettings.getInstance().getDividerLocation());
		setFontSize(WalletSettings.getInstance().getFontSize());
	}

	public void exit() {
		frame.dispose();
	}

	/**
	 * Resgier allthe components in the jFrame.
	 *
	 * @param c
	 * @return
	 */
	public static List<Component> getAllComponents(final Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Container)
				compList.addAll(getAllComponents((Container) comp));
		}
		return compList;
	}


	/**
	 * Use the font spinner to increase and decrease the font size.
	 */
	public void setupFontSpinner() {

		int fontSize = WalletSettings.getInstance().getFontSize();

		SpinnerModel spinnerModel = new SpinnerNumberModel(fontSize, //initial value
				10, //min
				fontSize + 20, //max
				2); //step
		fldFontSize.setModel(spinnerModel);
		fldFontSize.addChangeListener(new ChangeListener() {
										  @Override
										  public void stateChanged(ChangeEvent e) {
											  SpinnerModel spinnerModel = fldFontSize.getModel();
											  int newFontSize = (Integer)spinnerModel.getValue();
											  setFontSize(newFontSize);

										  }
									  }
		);


	}

	void setFontSize(int newFontSize) {
		WalletSettings.getInstance().setFontSize(newFontSize);
		for (Component component : componetsList) {
			Font original = component.getFont();
			Font newFont = original.deriveFont(Float.valueOf(newFontSize));
			component.setFont(newFont);

		}
	}


	void createUIComponents() {
		// TODO: place custom component creation code here
	}


	public void displayWalletItemDetails(final WalletItem item) {
		itemDetailView.displayWalletItemDetails(item, DisplayMode.view);
	}


	public DisplayMode getDisplayMode() {
		return itemDetailView.getDisplayMode();
	}

	public void displayWalletItemDetails(final WalletItem item, final DisplayMode mode) {
		itemDetailView.displayWalletItemDetails(item, mode);
	}


	public void loadTree() {
		treeExploreView.setupTreeView();
		listExploreView.setupListView();
	}


	public boolean isModified() {
		 return (getDisplayMode()==DisplayMode.add || getDisplayMode()==DisplayMode.edit )
				 //compare the current item in the model with the data on the item detail form.
				 && itemDetailView.isModified();
	}

	public void saveCurrentEdit(boolean askToSave) {
		if (isModified()) {
			if (!askToSave ||  DialogUtils.getConfirmation(ServiceRegistry.instance.getWalletForm().getFrame()
					, "Save the changes to the current item?") == Confirmation.YES) {
				itemDetailView.updateToModel();
				//save file
				SaveWalletAction saveWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, SaveWalletAction.class);
				saveWalletAction.execute();
			}
		}

		model.setModified(false);
		btnSaveForm.setVisible(false);


	}

	public void setMessage(String s) {
		if (labelLastMessage!=null)
		labelLastMessage.setText(s);
	}

}



class MyDocumentListener implements DocumentListener {
	// implement the methods
	JTextField field;
	String itemFieldName; //name, URL etc
	WalletModel model;

	public MyDocumentListener(JTextField field, String itemFieldName, WalletModel model) {
		this.field = field;
		this.itemFieldName = itemFieldName;
		this.model = model;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		valueChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		valueChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		valueChanged();

	}

	public void valueChanged() {
		try {
			if (model.getCurrentItem() != null) {
				ReflectionUtil.setFieldValue(model.getCurrentItem(), itemFieldName, field.getText());
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occured", e.getMessage());
		}
	}

	//read settings
	private void initSettings() {

	}

}