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

import org.mhisoft.common.event.EventDispatcher;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.common.util.ReflectionUtil;
import org.mhisoft.wallet.WalletMain;
import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.action.BackupAction;
import org.mhisoft.wallet.action.ChangePasswordAction;
import org.mhisoft.wallet.action.CloseWalletAction;
import org.mhisoft.wallet.action.ImportWalletAction;
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
	JButton btnMoveNode;

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
	 JButton btnClearFilter;
	private JScrollPane itemListPanel;
	private JScrollPane treePanel;
	private JPanel filterPanel;
	private JPanel treeListPanel;

	private JPanel rightMainPanel;
	private JScrollPane detailFormScrollPane;
	private JPanel buttonPanel;
	JLabel labelCVC;
	JPasswordField fldCVC;
	JPasswordField fldPin;
	public JLabel labelCurrentOpenFile;

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

		// Put client property
		fldPassword.putClientProperty("JPasswordField.cutCopyAllowed", true);


//		fldName.getDocument().addDocumentListener(new MyDocumentListener(fldName, "name", model));
//		fldName.getDocument().addDocumentListener(new MyDocumentListener(fldName, "URL", model));

		btnEditForm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnEditForm" , null ));
				itemDetailView.editDetailAction();
			}
		});
		btnCancelEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnCancelEdit" , null ));

				itemDetailView.cancelEditAction();
			}
		});
		btnSaveForm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnSaveForm" , null ));


				//save button click , it is two fold. save the item detail edit
				//Or save the whole model when model is modified from import for instance.
				if (isDetailModified())
					saveCurrentEdit(false);
				else if (model.isModified()) {
					//save file
					SaveWalletAction saveWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, SaveWalletAction.class);
					saveWalletAction.execute();

				}

			}
		});


		btnTogglePasswordView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnTogglePasswordView" , null ));

				hidePassword = !hidePassword;
				updatePasswordChar();
			}
		});


		//constructor
		btnAddNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnAddNode" , null ));

				treeExploreView.addItem();
			}
		});

		btnDeleteNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnDeleteNode" , null ));

				treeExploreView.removeItem();
			}
		});

		btnMoveNode.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnMoveNode" , null ));

				treeExploreView.moveItem();

			}
		} );

		btnFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnFilter" , null ));

				doFilter();

			}
		});
		btnClearFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnClearFilter" , null ));

				clearFilter();

			}
		});
		btnFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnFilter" , null ));
					doFilter();
				}
			}
		});
		btnClearFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "btnClearFilter" , null ));
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
		if (hidePassword) {
			fldPassword.setEchoChar('*');
			fldCVC.setEchoChar('*');
			fldPin.setEchoChar('*');
		}
		else {
			fldPassword.setEchoChar((char) 0);
			fldCVC.setEchoChar((char) 0);
			fldPin.setEchoChar((char) 0);
		}
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
		tree.setModel(null);


		String title ;
		if (WalletSettings.getInstance().getLastFile()==null)
			title="Creating a new wallet"  ;
		else
		   title ="Opening file:" + WalletSettings.getInstance().getLastFile();

		PasswordForm passwordForm = new PasswordForm(title);
		passwordForm.showPasswordForm(this, null);


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
		menuImport = new JMenuItem("Import and Merge", KeyEvent.VK_I);
		menuFile.add(menuImport);
		menuBackup = new JMenuItem("Backup", KeyEvent.VK_B);
		menuFile.add(menuBackup);
		menuChangePassword = new JMenuItem("Change Password", KeyEvent.VK_P);
		menuFile.add(menuChangePassword);
		menuClose = new JMenuItem("Quit", KeyEvent.VK_C);
		menuFile.add(menuClose);


		componentsList.add(menuBar);
		componentsList.add(menuFile);
		componentsList.add(menuOpen);
		componentsList.add(menuClose);
		componentsList.add(menuChangePassword);
		componentsList.add(menuImport);
		componentsList.add(menuBackup);


		menuClose.addActionListener(closeAction);
		btnClose.addActionListener(closeAction);
		menuOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "menuOpen" , null ));

				CloseWalletAction closeWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CloseWalletAction.class);
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
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "menuChangePassword" , null ));


				ChangePasswordAction action = ServiceRegistry.instance.getService(BeanType.prototype, ChangePasswordAction.class);
				action.execute();


			}
		});
		menuImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "menuImport" , null ));

				ImportWalletAction importWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, ImportWalletAction.class);
				importWalletAction.execute();
			}
		});

		menuBackup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "menuBackup" , null ));

				BackupAction backupAction = ServiceRegistry.instance.getService(BeanType.singleton, BackupAction.class);
				backupAction.execute();
			//
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
				  EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "fldFontSize" , null ));

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

		btnSaveForm.setVisible(model.isModified() || isDetailModified());
	}

	public void closeView() {
		//reset model with empty data.
		model.setCurrentItem(null);
		model.setCurrentItem(null);
		model.setModified(false);
		clearFilter();
		//todo disable all the buttons.

		treeExploreView.closeTree();
		itemDetailView.closeView();
		listExploreView.closeView();


	}


	public boolean isDetailModified() {
		return
				((getDisplayMode() == DisplayMode.add || getDisplayMode() == DisplayMode.edit)
						//compare the current item in the model with the data on the item detail form.
						&& (itemDetailView.isModified()));

	}


	//called when node changes
	public void saveCurrentEdit(boolean askToSave) {
		if (isDetailModified()) {
			if (!askToSave || DialogUtils.getConfirmation(ServiceRegistry.instance.getWalletForm().getFrame()
					, "Save the changes?") == Confirmation.YES) {
				itemDetailView.updateToModel();
				//save file
				SaveWalletAction saveWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, SaveWalletAction.class);
				saveWalletAction.execute();
				//model.setModified(false);   save action does it.
			}

		}

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