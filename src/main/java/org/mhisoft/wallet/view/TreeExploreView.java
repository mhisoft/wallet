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

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description: TreeExploreView
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class TreeExploreView {
	JFrame frame;
	WalletModel model ;
	JTree tree;
	WalletForm form;
	DefaultMutableTreeNode rootNode;

	public TreeExploreView(JFrame frame, WalletModel model, JTree tree, WalletForm walletForm) {
		this.frame = frame;
		this.model = model;
		this.tree = tree;
		this.form = walletForm;
	}

	/**
	 * load all the items recursively into the tree
	 */

	public void buildTree(DefaultMutableTreeNode parentNode) {
		//load model into tree
		WalletItem parentItem = (WalletItem) parentNode.getUserObject();
		if (parentItem.hasChildren()) {
			for (WalletItem item : parentItem.getChildren()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
				if (item.getType() == ItemType.category) {
					parentNode.add(node);
					//recursive into this parent node children
					buildTree(node);
				} else {
					//item leaf
					parentNode.add(node);
				}
			}
		}

	}

	/**
	 * Set up the explore tree.
	 */
	public void setupTreeView() {

		tree.setModel(null);
		model.setupTestData();
		//DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new WalletItem(ItemType.category, "My Default Wallet 1"));
		DefaultTreeModel treeModel = null;

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//set up root node
		WalletItem rootItem=model.getItemsFlatList().get(0);
		rootNode	= new DefaultMutableTreeNode( rootItem);
		treeModel = new DefaultTreeModel(rootNode);
		tree.setModel(treeModel);


		//load all the items recursively  into tree
		buildTree(rootNode);

		tree.getSelectionModel().setSelectionPath(new TreePath(rootNode.getPath()));

		changeNode(rootNode);
		expandRoot(rootNode);

		//Listen for when the selection changes.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

				if (node == null) {
					form.btnAddNode.setVisible(false);
					form.btnDeleteNode.setVisible(false);
					//Nothing is selected.
					return;
				}

				changeNode(node);

			}
		});

	}

	public void changeNode(DefaultMutableTreeNode node) {

		model.setCurrentItem ((WalletItem) node.getUserObject());
		form.displayWalletItemDetails(model.getCurrentItem());
		toggleButton(model.getCurrentItem());
		form.resetHidePassword();

	}


	void toggleButton(WalletItem currentItem) {
		if (currentItem.getType() == ItemType.category) {
			form.btnAddNode.setVisible(true);
			form.btnDeleteNode.setVisible(!currentItem.hasChildren());
		} else {
			form.btnAddNode.setVisible(true);
			form.btnDeleteNode.setVisible(true);

		}
	}


	public void expandRoot(DefaultMutableTreeNode currentNode) {
		//DefaultMutableTreeNode currentNode = treeTop.getNextNode();
		do {
			if (currentNode.getLevel()==1)
				tree.expandPath(new TreePath(currentNode.getPath()));
			currentNode = currentNode.getNextNode();
		}
		while (currentNode != null);
	}

	DefaultMutableTreeNode findNode(DefaultMutableTreeNode node, WalletItem target) {
		DefaultMutableTreeNode ret ;
		WalletItem  a = (WalletItem) node.getUserObject();
		if (a.equals(target))
			return node;
		else if (node.isLeaf())
			return null;

		//dive into children
		for (int i = 0; i < node.getChildCount(); i++) {
			ret = findNode((DefaultMutableTreeNode)node.getChildAt(i),target);
			if (ret!=null)
				return ret;
		}
		return null;

	}



	public void addItem() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		WalletItem item = (WalletItem) node.getUserObject();
		WalletItem parentItem;
		if (item.getType()== ItemType.category) {
			 parentItem = item;
		}
		else {
			//this item is node leaf
			parentItem = item.getParent();
		}


		WalletItem newItem;
		if (model.isRoot(item)) {
			//add another category to the  root
			newItem = new WalletItem(ItemType.category, "New Category - Untitled");
		}
		else
			newItem = new WalletItem(ItemType.item, "New Item- Untitled");


		model.addItem(parentItem, newItem);



		DefaultMutableTreeNode parentNode = findNode(rootNode, parentItem);
		if (parentNode==null)
			throw new RuntimeException("parent node not found for item:" + item);




		//Update the tree nodes and then notify the model:
		//don't need to directly  insert into tree model
		DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode(newItem);
		parentNode.add( newChildNode );
		DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
		treeModel.reload(parentNode);


		//now set selection to this new node
		tree.getSelectionModel().setSelectionPath(new TreePath(newChildNode.getPath()));
		//Make sure the user can see the lovely new node.
		tree.scrollPathToVisible(new TreePath(newChildNode.getPath()));

		if (WalletModel.debug) {
			form.fldNotes.setText(model.dumpFlatList());
		}


	}

	public void removeItem() {
		WalletItem item = model.getCurrentItem();
		if (item.getType()==ItemType.category && item.hasChildren())
			return;

		if (ConfirmationUtil.getConfirmation(frame, "Delete the '" + item.getName() +"'?")==Confirmation.YES ) {

			DefaultMutableTreeNode thisNode = findNode(rootNode, item);
			DefaultMutableTreeNode parentNode = findNode(rootNode, item.getParent());
			if (parentNode==null)
				throw new RuntimeException("parent node not found for item:" + item);

			//remove from jTree node
			parentNode.remove(thisNode);
			DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
			treeModel.reload(parentNode);

			//remove from the model
			model.removeItem(item);


			//now set selection to this new node
			tree.getSelectionModel().setSelectionPath(new TreePath(parentNode.getPath()));
			//Make sure the user can see the lovely new node.
			tree.scrollPathToVisible(new TreePath(parentNode.getPath()));

			if (WalletModel.debug) {
				form.fldNotes.setText(model.dumpFlatList());
			}
		}

	}





}
