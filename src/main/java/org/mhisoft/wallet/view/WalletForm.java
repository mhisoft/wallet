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

import java.util.List;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
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
import javax.swing.JScrollPane;
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
import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.action.CloseWalletAction;
import org.mhisoft.wallet.action.OpenWalletFileAction;
import org.mhisoft.wallet.action.SaveWalletAction;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.BeanType;
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
	private JButton btnClearFilter;
	private JScrollPane itemListPanel;
	private JScrollPane treePanel;
	private JPanel filterPanel;
	private JPanel treeListPanel;

	private JPanel rightMainPanel;
	private JScrollPane detailFormScrollPane;
	private JPanel buttonPanel;
	JLabel labelCVC;
	JTextField fldCVC;
	private JScrollPane rightScrollPane;


	JMenuBar menuBar;
	JMenu menuFile;
	JMenuItem menuOpen, menuClose, menuImport, menuBackup, menuChangePassword;
	//JRadioButtonMenuItem rbMenuItem;
	//JCheckBoxMenuItem cbMenuItem;

	List<Component> componentsList;
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
		listExploreView = new ListExplorerView(frame, model, itemList, this);
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

				doFilter();

			}
		});
		btnClearFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearFilter();

			}
		});
		btnFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					doFilter();
				}
			}
		});
		btnClearFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					clearFilter();
				}
			}
		});
	}


	public void doFilter() {
		if (fldFilter.getText() != null && fldFilter.getText().trim().length() > 0) {
			itemListPanel.setVisible(true);
			treePanel.setVisible(false);
			treeListPanel.validate();
			listExploreView.filterItems(fldFilter.getText());
		}
	}

	public void clearFilter() {
		fldFilter.setText("");
		itemListPanel.setVisible(false);
		treePanel.setVisible(true);
		treeListPanel.validate();
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
		return itemDetailView.currentMode != DisplayMode.view;
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

		componentsList = ViewHelper.getAllComponents(frame);
		componentsList.add(itemList);

		setupMenu();


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

		itemListPanel.setVisible(false);
		frame.setVisible(true);

		jreDebug();


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


	public void jreDebug() {
		//if (WalletModel.debug) {
		fldNotes.append("\n");
		fldNotes.append("\n");
		fldNotes.append("java.home=" + System.getProperty("java.home") + "\n");
		fldNotes.append("java.specification.version=" + System.getProperty("java.specification.version") + "\n");
		fldNotes.append("java.vendor=" + System.getProperty("java.vendor") + "\n");
		fldNotes.append("java.vendor.url=" + System.getProperty("java.vendor.url") + "\n");
		fldNotes.append("java.version=" + System.getProperty("java.version") + "\n");

		fldNotes.append("user.home=" + System.getProperty("user.home") + "\n");
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
		frame.setJMenuBar(menuBar);

		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);
		menuOpen = new JMenuItem("Open", KeyEvent.VK_O);
		menuFile.add(menuOpen);
		menuClose = new JMenuItem("Close", KeyEvent.VK_C);
		menuFile.add(menuClose);
		menuChangePassword = new JMenuItem("Change Password", KeyEvent.VK_P);
		menuFile.add(menuChangePassword);

		componentsList.add(menuBar);
		componentsList.add(menuFile);
		componentsList.add(menuOpen);
		componentsList.add(menuClose);
		componentsList.add(menuChangePassword);


		menuClose.addActionListener(closeAction);
		btnClose.addActionListener(closeAction);
		menuOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CloseWalletAction closeWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, CloseWalletAction.class);
				ActionResult r = closeWalletAction.execute();
				if (r.isSuccess()) {
					OpenWalletFileAction openWalletFileAction = ServiceRegistry.instance.getService(BeanType.singleton, OpenWalletFileAction.class);
					r = openWalletFileAction.execute();
				}
			}
		});
		menuChangePassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {


			}
		});


	}

	void loadInPreferences() {
		//divider location
		splitPanel.setDividerLocation(WalletSettings.getInstance().getDividerLocation());
		ViewHelper.setFontSize(this.componentsList, WalletSettings.getInstance().getFontSize());
	}

	public void exit() {
		frame.dispose();
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
											  int newFontSize = (Integer) spinnerModel.getValue();
											  ViewHelper.setFontSize(componentsList, newFontSize);

										  }
									  }
		);


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
		return (getDisplayMode() == DisplayMode.add || getDisplayMode() == DisplayMode.edit)
				//compare the current item in the model with the data on the item detail form.
				&& itemDetailView.isModified();
	}

	public void saveCurrentEdit(boolean askToSave) {
		if (isModified()) {
			if (!askToSave || DialogUtils.getConfirmation(ServiceRegistry.instance.getWalletForm().getFrame()
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
		if (labelLastMessage != null)
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