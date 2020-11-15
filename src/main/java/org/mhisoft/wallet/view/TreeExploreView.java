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

import java.util.Enumeration;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.mhisoft.common.event.EventDispatcher;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.wallet.action.SaveWalletAction;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description: TreeExploreView
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class TreeExploreView {

	private static final Logger logger = Logger.getLogger(TreeExploreView.class.getName());


	JFrame frame;
	//WalletModel model;
	JTree tree;
	WalletForm form;
	DefaultMutableTreeNode rootNode;
	DefaultTreeModel treeModel;


	private WalletModel getModel() {
		return ServiceRegistry.instance.getWalletModel();
	}

	public TreeExploreView(JFrame frame, WalletModel model, JTree tree, WalletForm walletForm) {
		this.frame = frame;
		//this.model = model;
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
				EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.UserCheckInEvent, "changeNode", null));


			DefaultMutableTreeNode oldNode = null;
				if (e.getOldLeadSelectionPath() != null)
					oldNode = (DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent();


				changeNode(oldNode, node);

			}
		});




		tree.addMouseListener(form.jtreeMouseRightClickListener);
		//tree.setComponentPopupMenu(form.popupMenu);


		//update the tree node when fldName loses focus.
//		form.fldName.addFocusListener(new FocusListener() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				//
//			}
//
//			@Override
//			public void focusLost(FocusEvent e) {
//				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//				if (node!=null && treeModel!=null && (form.getDisplayMode()==DisplayMode.add || form.getDisplayMode()==DisplayMode.edit) ) {
//
//					SwingUtilities.invokeLater( new Runnable() {
//						public void run() {
//							WalletItem item = (WalletItem)node.getUserObject();
//							if (item!=null) {
//								item.setName(form.fldName.getText());
//								treeModel.nodeChanged(node);
//								//tree.revalidate();
//							}
//						}
//					});
//
//				}
//			}
//		});


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

	private void loadPreferences() {
		if (WalletSettings.getInstance().isTreeExpanded())
			expandTree();
		else
			collapseTree();
	}


	/**
	 * Set up the explorer tree base on flat list in the getModel().
	 * buildTreeFromFlatList will be called.
	 */
	public void setupTreeView() {

//		tree.setModel(null);
//		getModel().setupTestData();
		//DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new WalletItem(ItemType.category, "My Default Wallet 1"));
		treeModel = null;

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		getModel().buildTreeFromFlatList();

		//set up root node
		WalletItem rootItem = getModel().getItemsFlatList().get(0);
		rootNode = new DefaultMutableTreeNode(rootItem);
		treeModel = new DefaultTreeModel(rootNode);
		tree.setModel(treeModel);


		//load all the items recursively  into tree
		buildTree(rootNode);

		tree.getSelectionModel().setSelectionPath(new TreePath(rootNode.getPath()));

		changeNode(null, rootNode);
		loadPreferences();


		form.btnFilter.setEnabled(true);
		form.btnClearFilter.setEnabled(true);


	}


	public void closeTree() {
		tree.setModel(null);
		toggleButton(null);

		form.btnFilter.setEnabled(false);
		form.btnClearFilter.setEnabled(false);
	}

	public void changeNode(final DefaultMutableTreeNode oldNode, final DefaultMutableTreeNode node) {

		if (!getModel().isAddingNode() && !getModel().isImporting() )
		   form.saveCurrentEdit(true);

		getModel().setCurrentItem((WalletItem) node.getUserObject());
		form.displayWalletItemDetails(getModel().getCurrentItem(), form.getDisplayMode());
		toggleButton(getModel().getCurrentItem());
		form.resetHidePassword();

	}


	void toggleButton(WalletItem currentItem) {
		if (currentItem == null) {
			form.btnAddNode.setEnabled(false);
			form.btnDeleteNode.setEnabled(false);
			form.btnMoveNode.setEnabled(false);
			form.btnCollapse.setEnabled(false);
			form.menuExport.setEnabled(false);
		} else {

			form.btnCollapse.setEnabled(true);

			if (currentItem.getType() == ItemType.category) {
				form.btnAddNode.setEnabled(true);
				form.btnDeleteNode.setEnabled(!currentItem.hasChildren());
				form.btnMoveNode.setEnabled(false);
			} else {
				form.btnAddNode.setEnabled(true);
				form.btnDeleteNode.setEnabled(true);
				form.btnMoveNode.setEnabled(true);
			}
		}
	}


	public void expandTree() {
		DefaultMutableTreeNode currentNode = rootNode;
		do {
			if (currentNode.getLevel() == 1)
				tree.expandPath(new TreePath(currentNode.getPath()));
			currentNode = currentNode.getNextNode();
		}
		while (currentNode != null);
	}

    public void collapseTree() {
		DefaultMutableTreeNode currentNode =rootNode;
		do {
			if (currentNode.getLevel() == 1)
				tree.collapsePath(new TreePath(currentNode.getPath()));
			currentNode = currentNode.getNextNode();
		}
		while (currentNode != null);
	}

	/**
	 * Find the treeNode for the target Wallet model Item , starting from the  startFromNode
	 *
	 * @param startFromNode
	 * @param target
	 * @return
	 */
	DefaultMutableTreeNode findNode(DefaultMutableTreeNode startFromNode, WalletItem target) {
		if (startFromNode == null)
			return null;
		DefaultMutableTreeNode ret;
		WalletItem a = (WalletItem) startFromNode.getUserObject();
		if (a.equals(target))
			return startFromNode;
		else if (startFromNode.isLeaf())
			return null;

		//dive into children
		for (int i = 0; i < startFromNode.getChildCount(); i++) {
			ret = findNode((DefaultMutableTreeNode) startFromNode.getChildAt(i), target);
			if (ret != null)
				return ret;
		}
		return null;

	}

	// add the new item to the parent to both model and item tree.
	private DefaultMutableTreeNode addItemAndNode(WalletItem parentItem, WalletItem newItem) {

		getModel().addItem(parentItem, newItem);


		DefaultMutableTreeNode parentNode = findNode(rootNode, parentItem);
		if (parentNode == null)
			throw new RuntimeException("parent node not found for item:" + newItem);


		//Update the tree nodes and then notify the model:
		//don't need to directly  insert into tree model
		DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode(newItem);
		parentNode.add(newChildNode);
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		treeModel.reload(parentNode);
		return newChildNode;

	}



	public void addItem() {
		getModel().setAddingNode(true);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		WalletItem item = (WalletItem) node.getUserObject();
		WalletItem parentItem;
		if (item.getType() == ItemType.category) {
			parentItem = item;
		} else {
			//this item is node leaf
			parentItem = item.getParent();
		}


		WalletItem newItem;
		if (getModel().isRoot(item)) {
			//add another category to the  root
			newItem = new WalletItem(ItemType.category, "New Category - Untitled");
		} else
			newItem = new WalletItem(ItemType.item, "New Item- Untitled");


		DefaultMutableTreeNode newChildNode = addItemAndNode(parentItem, newItem);


		//Make sure the user can see the lovely new node.
		tree.scrollPathToVisible(new TreePath(newChildNode.getPath()));

		form.displayWalletItemDetails(getModel().getCurrentItem(), DisplayMode.add);

		//now set selection to this new node
		//this will fire the changeNode event.
		tree.getSelectionModel().setSelectionPath(new TreePath(newChildNode.getPath()));


//		if (SystemSettings.debug) {
//			System.out.println(getModel().dumpFlatList());
//		}

		getModel().setAddingNode(false);


	}


	private void removeItemFromModel(WalletItem item) {
		DefaultMutableTreeNode thisNode = findNode(rootNode, item);
		DefaultMutableTreeNode parentNode = findNode(rootNode, item.getParent());
		if (parentNode == null)
			throw new RuntimeException("parent node not found for item:" + item);

		//remove from jTree node
		parentNode.remove(thisNode);
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		treeModel.reload(parentNode);

		//remove from the model
		getModel().removeItem(item);

	}


	public void removeItem() {
		WalletItem item = getModel().getCurrentItem();
		if (item.getType() == ItemType.category && item.hasChildren())
			return;

		if (DialogUtils.getConfirmation(frame, "Delete the '" + item.getName() + "'?") == Confirmation.YES) {

			DefaultMutableTreeNode parentNode = findNode(rootNode, item.getParent());

			removeItemFromModel(item);

			//save it
			SaveWalletAction saveWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, SaveWalletAction.class);
			saveWalletAction.execute();

			//now set selection to this new node
			tree.getSelectionModel().setSelectionPath(new TreePath(parentNode.getPath()));
			//Make sure the user can see the lovely new node.
			tree.scrollPathToVisible(new TreePath(parentNode.getPath()));

		}

	}

	public void moveItem() {
		WalletItem item = getModel().getCurrentItem();
		if (item.getType() == ItemType.category && item.hasChildren())
			return;

		MoveNodeDialog dialog = new MoveNodeDialog(item);

		dialog.display(item, new MoveNodeDialog.SelectCategoryCallback() {
			@Override
			public void onSelectWalletItem(WalletItem newParentItem) {
				logger.fine("move current item  '" + item + "' to :" + newParentItem);


				removeItemFromModel(item);

				DefaultMutableTreeNode newChildNode = addItemAndNode(newParentItem, item);

				//save it
				SaveWalletAction saveWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, SaveWalletAction.class);
				saveWalletAction.execute();


				//now set selection to this new node
				tree.getSelectionModel().setSelectionPath(new TreePath(newChildNode.getPath()));
				//Make sure the user can see the lovely new node.
				tree.scrollPathToVisible(new TreePath(newChildNode.getPath()));

//				SwingUtilities.invokeLater(new Runnable() {
//
//					@Override
//					public void run() {
//
//						form.btnSaveForm.setVisible(true);
//					}
//				});


			}
		});

	}


	public void setSelectionToCurrentNode() {
		DefaultMutableTreeNode node = findNode(rootNode, getModel().getCurrentItem());
		if (node != null)
			tree.getSelectionModel().setSelectionPath(new TreePath(node.getPath()));

	}

	/**
	 * Update the name changes from item to the tree node.
	 * @param item
	 */
	public void updateNameChange(WalletItem item) {

		DefaultMutableTreeNode node =   findNode(rootNode, item )  ;
		if (node!=null) {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					WalletItem nodeItem = (WalletItem) node.getUserObject();
					if (item != null) {
						//nodeItem is the same as item. same object.
						//nodeItem.setName(form.fldName.getText());
						treeModel.nodeChanged(node);  //this is all we need to refresh the node title.
						//tree.revalidate();
					}
				}
			});
		}
	}

	private void expandCollapseAll(JTree tree, TreePath parent, boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();

		if (node.getChildCount() > 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandCollapseAll(tree, path, expand);
			}
		}
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}


	public void expandTree(JTree tree) {
		TreeNode node = (TreeNode) tree.getModel().getRoot();
		expandCollapseAll(tree, new TreePath(node), true);
	}






}


