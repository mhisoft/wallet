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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.mhisoft.common.event.EventDispatcher;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.common.logger.Loggerfactory;
import org.mhisoft.common.logger.MHILogger;
import org.mhisoft.wallet.SystemSettings;
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
	private static final MHILogger logger = Loggerfactory.getLogger(TreeExploreView.class,
			SystemSettings.loggerLevel);



	JFrame frame;
	WalletModel model ;
	JTree tree;
	WalletForm form;
	DefaultMutableTreeNode rootNode;
	DefaultTreeModel treeModel;

	public TreeExploreView(JFrame frame, WalletModel model, JTree tree, WalletForm walletForm) {
		this.frame = frame;
		this.model = model;
		this.tree = tree;
		this.form = walletForm;



		//Listen for when the selection changes.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

				if (node == null) {
					form.btnAddNode.setEnabled(false);
					form.btnDeleteNode.setEnabled(false);
					//Nothing is selected.
					return;
				}

				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "changeNode" , null ));

				changeNode(node);

			}
		});




		//update the tree node when fldName loses focus.
		form.fldName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				//
			}

			@Override
			public void focusLost(FocusEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node!=null && treeModel!=null && (form.getDisplayMode()==DisplayMode.add || form.getDisplayMode()==DisplayMode.edit) ) {

					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							WalletItem item = (WalletItem)node.getUserObject();
							if (item!=null) {
								item.setName(form.fldName.getText());
								treeModel.nodeChanged(node);
								//tree.revalidate();
							}
						}
					});

				}
			}
		});







	}

	/**
	 * load all the items recursively into the tree
	 * hierarchical relationships from the flat list need to be built first.
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
	 * Set up the explorer tree base on flat list in the model.
	 * buildTreeFromFlatList will be called.
	 */
	public void setupTreeView() {

//		tree.setModel(null);
//		model.setupTestData();
		//DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new WalletItem(ItemType.category, "My Default Wallet 1"));
		treeModel = null;

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		model.buildTreeFromFlatList();

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


		form.btnFilter.setEnabled(true);
		form.btnClearFilter.setEnabled(true);


	}


	public void closeTree() {
		tree.setModel(null);
		toggleButton(null);

		form.btnFilter.setEnabled(false);
		form.btnClearFilter.setEnabled(false);
	}

	public void changeNode(DefaultMutableTreeNode node) {


		form.saveCurrentEdit(true);

		model.setCurrentItem ((WalletItem) node.getUserObject());
		form.displayWalletItemDetails(model.getCurrentItem());
		toggleButton(model.getCurrentItem());
		form.resetHidePassword();

	}


	void toggleButton(WalletItem currentItem) {
		if (currentItem==null)  {
			form.btnAddNode.setEnabled(false);
			form.btnDeleteNode.setEnabled(false);
			form.btnMoveNode.setEnabled(false);
		}
		else {

			if (currentItem.getType() == ItemType.category) {
				form.btnAddNode.setEnabled(true);
				form.btnDeleteNode.setEnabled(!currentItem.hasChildren());
				form.btnMoveNode.setEnabled(false);
			} else {
				form.btnAddNode.setEnabled(true);
				form.btnDeleteNode.setEnabled(true);
				form.btnMoveNode.setEnabled( true);
			}
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

	/**
	 * Find the treeNode starting from the  startFromNode
	 * @param startFromNode
	 * @param target
	 * @return
	 */
	DefaultMutableTreeNode findNode(DefaultMutableTreeNode startFromNode, WalletItem target) {
		if (startFromNode ==null)
			return null;
		DefaultMutableTreeNode ret ;
		WalletItem  a = (WalletItem) startFromNode.getUserObject();
		if (a.equals(target))
			return startFromNode;
		else if (startFromNode.isLeaf())
			return null;

		//dive into children
		for (int i = 0; i < startFromNode.getChildCount(); i++) {
			ret = findNode((DefaultMutableTreeNode)startFromNode.getChildAt(i),target);
			if (ret!=null)
				return ret;
		}
		return null;

	}

	 // add the new item to the parent to both model and item tree.
	private DefaultMutableTreeNode addItemAndNode(WalletItem parentItem, WalletItem newItem) {

		model.addItem(parentItem, newItem);


		DefaultMutableTreeNode parentNode = findNode(rootNode, parentItem);
		if (parentNode==null)
			throw new RuntimeException("parent node not found for item:" + newItem);


		//Update the tree nodes and then notify the model:
		//don't need to directly  insert into tree model
		DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode(newItem);
		parentNode.add( newChildNode );
		DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
		treeModel.reload(parentNode);
		return newChildNode;

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


		DefaultMutableTreeNode newChildNode = addItemAndNode(parentItem, newItem )  ;


		//now set selection to this new node
		tree.getSelectionModel().setSelectionPath(new TreePath(newChildNode.getPath()));
		//Make sure the user can see the lovely new node.
		tree.scrollPathToVisible(new TreePath(newChildNode.getPath()));

		form.displayWalletItemDetails(model.getCurrentItem(), DisplayMode.edit);

//		if (SystemSettings.debug) {
//			System.out.println(model.dumpFlatList());
//		}


	}


	private void removeItemFromModel(	WalletItem item) {
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

	}


	public void removeItem() {
		WalletItem item = model.getCurrentItem();
		if (item.getType()==ItemType.category && item.hasChildren())
			return;

		if (DialogUtils.getConfirmation(frame, "Delete the '" + item.getName() + "'?")==Confirmation.YES ) {

			DefaultMutableTreeNode parentNode = findNode(rootNode, item.getParent());

			removeItemFromModel (item)  ;

			//now set selection to this new node
			tree.getSelectionModel().setSelectionPath(new TreePath(parentNode.getPath()));
			//Make sure the user can see the lovely new node.
			tree.scrollPathToVisible(new TreePath(parentNode.getPath()));

		}

	}

	public void moveItem() {
		WalletItem item = model.getCurrentItem();
		if (item.getType()==ItemType.category && item.hasChildren())
			return;

		MoveNodeDialog  dialog =  new MoveNodeDialog(item);

		dialog.display(item, new MoveNodeDialog.SelectCategoryCallback() {
			@Override
			public void onSelectWalletItem(WalletItem newParentItem) {
				logger.debug("move current item  '" + item +"' to :" + newParentItem);


				removeItemFromModel(item)  ;

				DefaultMutableTreeNode newChildNode = addItemAndNode(newParentItem, item)  ;
				//now set selection to this new node
				tree.getSelectionModel().setSelectionPath(new TreePath(newChildNode.getPath()));
				//Make sure the user can see the lovely new node.
				tree.scrollPathToVisible(new TreePath(newChildNode.getPath()));


			}
		});

	}


	public void setSelectionToCurrentNode() {
		DefaultMutableTreeNode node = findNode(rootNode, model.getCurrentItem());
		if (node!=null)
		       tree.getSelectionModel().setSelectionPath(new TreePath(node.getPath()));

	}




}
