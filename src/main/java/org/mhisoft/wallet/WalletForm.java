/*
 * Copyright (c) 2014- MHISoft LLC and/or its affiliates. All rights reserved.
 * Licensed to MHISoft LLC under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. MHISoft LLC licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mhisoft.wallet;

import java.util.ArrayList;
import java.util.List;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Description: The Wallet Form UI
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletForm {

	JFrame frame;

	private JTree tree;
	private JTextField fldName;
	private JPanel mainPanel;
	private JTextField fldURL;
	private JTextArea fldNotes;
	private JPasswordField fldPassword;
	private JTextField fldUserName;
	private JTextField fldAccountNumber;
	private JSpinner fldFontSize;

	private JLabel labelName;
	private JTabbedPane tabbedPane1;
	private JButton button1;
	private JSplitPane splitPanel;
	private JButton btnAddNode;
	private JButton btnDeleteNode;
	private JPanel treeButtonPanel;
	private JLabel labelURL;
	private JLabel labelNotes;
	private JLabel labelPassword;
	private JLabel labelUserName;
	private JLabel labelAccount;
	private JLabel labelFontSize;

	List<Component> componetsList;
	WalletModel model ;


	public WalletForm() {
		model = new WalletModel();
		//constructor
		btnAddNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
	}

	public void init() {
		frame = new JFrame("Wallet 1.0");
		frame.setContentPane(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1200, 800));

		frame.pack();

		componetsList = getAllComponents(frame);

		/*position it*/
		frame.setLocationRelativeTo(null);  // *** this will center your app ***
		//based on mouse location.
		//		PointerInfo a = MouseInfo.getPointerInfo();
		//		Point b = a.getLocation();
		//		int x = (int) b.getX();
		//		int y = (int) b.getY();
		//		frame.setLocation(x + 100, y);

		loadInPreferences();

		setupTree();
		setupFontSpinner();


		frame.setVisible(true);

	}

	//todo load from config
	void loadInPreferences() {
		//divider location
		splitPanel.setDividerLocation(0.2);
		setFontSize(Float.valueOf(20));
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

		int fontSize = tree.getFont().getSize();

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
											  setFontSize(newFontSize);

										  }
									  }
		);



	}

	void setFontSize(Float newFontSize) {
		for (Component component : componetsList) {
			Font original = component.getFont();
			Font newFont = original.deriveFont(newFontSize);
			component.setFont(newFont);
		}
	}


	public static void main(String[] args) {
		WalletForm form = new WalletForm();
		form.init();
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}


	/**
	 * Set up the explore tree.
	 */
	public void setupTree() {

		tree.setModel(null);
		model.setupTestData();
		//DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new WalletItem(ItemType.category, "My Default Wallet 1"));
		DefaultTreeModel treeModel = null;

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);


		//load model into tree
		DefaultMutableTreeNode lastParentNode=null;
		DefaultMutableTreeNode rootNode=null;

		for (WalletItem item : model.items) {
			if (item.getType()==ItemType.category) {
				DefaultMutableTreeNode catNode = new DefaultMutableTreeNode( item);
				//this is root node
				if (treeModel==null) {
					rootNode= catNode;
					treeModel = new DefaultTreeModel(rootNode);
					tree.setModel(treeModel);
					lastParentNode= catNode;
				}
				else {
					lastParentNode.add(catNode);
					lastParentNode = catNode;
				}
			}
			else if (item.getType()==ItemType.item) {
				DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode( item);
				lastParentNode.add(itemNode);
			}
		}


		tree.getSelectionModel().setSelectionPath(new TreePath(rootNode.getPath()));

		expandRoot(rootNode);


		//Listen for when the selection changes.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

				if (node == null) {
					btnAddNode.setVisible(false);
					btnDeleteNode.setVisible(false);
					//Nothing is selected.
					return;
				}

				WalletItem item = (WalletItem) node.getUserObject();
				displayWalletItemDetails(item);
			}
		});

	}


	void expandRoot(DefaultMutableTreeNode currentNode) {
		//DefaultMutableTreeNode currentNode = treeTop.getNextNode();
		do {
			if (currentNode.getLevel()==1)
				tree.expandPath(new TreePath(currentNode.getPath()));
			currentNode = currentNode.getNextNode();
		}
		while (currentNode != null);
	}

	void displayWalletItemDetails(WalletItem item) {
		if (item.getType() == ItemType.category) {
			fldName.setText(item.getName());
			//todo hide all other fields
			btnAddNode.setVisible(true);

			//todo only allow to delete if there are no items under
			btnDeleteNode.setVisible(true);


		} else {

			btnAddNode.setVisible(false);
			btnDeleteNode.setVisible(true);


			fldName.setText(item.getName());
			fldURL.setText(item.getURL());
			fldUserName.setText(item.getUserName());
			fldPassword.setText(item.getPassword());
			fldAccountNumber.setText(item.getAccountNumber());
			fldNotes.setText(item.getNotes());
		}

	}

}