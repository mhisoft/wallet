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

import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description: The Wallet Form UI
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletForm {

	JFrame frame;

	 JTree tree;
	 JTextField fldName;
	 JPanel mainPanel;
	 JTextField fldURL;
	 JTextArea fldNotes;
	 JPasswordField fldPassword;
	 JTextField fldUserName;
	 JTextField fldAccountNumber;
	 JSpinner fldFontSize;

	 JLabel labelName;
	 JTabbedPane tabbedPane1;
	 JButton button1;
	 JSplitPane splitPanel;
	 JButton btnAddNode;
	 JButton btnDeleteNode;
	 JPanel treeButtonPanel;
	 JLabel labelURL;
	 JLabel labelNotes;
	 JLabel labelPassword;
	 JLabel labelUserName;
	 JLabel labelAccount;
	 JLabel labelFontSize;

	List<Component> componetsList;
	WalletModel model ;
	TreeExploreView treeExploreView;
	ItemDetailView itemDetailView;

	public static void main(String[] args) {
		WalletForm form = new WalletForm();
		form.init();
	}



	public WalletForm() {
		model = new WalletModel();
		treeExploreView = new TreeExploreView(model, tree, this);
		itemDetailView = new ItemDetailView(model, this);


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

		treeExploreView.setupTreeView();
		setupFontSpinner();


		frame.setVisible(true);





		//constructor
		btnAddNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				treeExploreView.addItem();
			}
		});

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



	 void createUIComponents() {
		// TODO: place custom component creation code here
	}




	public void displayWalletItemDetails(final WalletItem item) {
		itemDetailView.displayWalletItemDetails(item);
	}

}