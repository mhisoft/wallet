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

package org.mhisoft.wallet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mhisoft.common.event.EventDispatcher;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.common.util.security.PBEEncryptor;

/**
 * Description: The model for the wallet view.
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletModel {

	/*
	Vault database version history:
	v13
	v14 -- attachment file is compressed. no changes to the main vault structure.

	 */
	public static final int LATEST_DATA_VERSION = 14;

	List<WalletItem> itemsFlatList = new ArrayList<>();
	WalletItem currentItem;
	transient  String passHash;
	transient String combinationHash;
	boolean modified = false;
	private String vaultFileName;

	transient PBEEncryptor encryptor;
	transient PBEEncryptor encryptor_v12; //for reading the v12 data file

	int dataFileVersion = LATEST_DATA_VERSION;  //version read from exist data file.   default to 13 for the new action.

	private boolean addingNode = false;


	private transient PassCombinationVO passVO;
	private int deletedEntriesInStore;

	private transient boolean importing = false;
	private transient WalletModel impModel;
	private transient PassCombinationVO exportVaultPass;
	private transient String exportVaultFileName;


	public WalletModel() {
	   //
	}

	/**
	 * Get a clone. the encryptor is a pointer ot the same . not a copy.
	 *
	 * @return
	 */
	public WalletModel clone() {
		WalletModel clone = new WalletModel();

		for (WalletItem walletItem : this.itemsFlatList) {
			clone.itemsFlatList.add(walletItem.clone());
		}
		clone.currentItem = this.currentItem;
		clone.passHash = this.passHash;
		clone.combinationHash = this.combinationHash;
		clone.modified = this.modified;
		clone.encryptor = this.encryptor;
		clone.encryptor_v12 = this.encryptor_v12;
		clone.dataFileVersion = this.dataFileVersion;
		clone.passVO = this.passVO == null ? null : this.passVO.clone();
		clone.deletedEntriesInStore = this.deletedEntriesInStore;
		clone.vaultFileName = this.vaultFileName;
		return clone;

	}


	public boolean isWalletOpen() {
		return this.passHash != null;
	}

	public void reset() {
		this.itemsFlatList = new ArrayList<>();
		this.currentItem = null;
		this.passHash = null;
		this.combinationHash = null;
		this.modified = false;
		this.encryptor = null;
		this.addingNode = false;
		this.importing = false;
		this.dataFileVersion = LATEST_DATA_VERSION;
		this.passVO = null;
		this.deletedEntriesInStore = 0;
		this.exportVaultFileName=null;
		this.impModel=null;
		this.exportVaultPass=null;
	}

	public PBEEncryptor createNewEncryptor(final PassCombinationVO newPass) {
		PBEEncryptor enc = new PBEEncryptor(newPass.getPassAndCombination());
		return enc;
	}

	public void initEncryptor(final PassCombinationVO pass) {
		encryptor = new PBEEncryptor(pass.getPassAndCombination());
		if (dataFileVersion == 12)
			encryptor_v12 = new PBEEncryptor(pass.getPass());
	}


	public PBEEncryptor getEncryptor() {
		return encryptor;

	}

	public PBEEncryptor getEncryptorForRead() {
		if (dataFileVersion <= 12)
			return encryptor_v12;
		else
			return encryptor;
	}

	public void setEncryptor(PBEEncryptor encryptor) {
		this.encryptor = encryptor;
	}

	public WalletItem getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(WalletItem currentItem) {
		this.currentItem = currentItem;
	}

	public List<WalletItem> getItemsFlatList() {
		return itemsFlatList;
	}

	public void setItemsFlatList(List<WalletItem> itemsFlatList) {
		this.itemsFlatList = itemsFlatList;
		setModified(false);      //todo
	}

	public String getPassHash() {
		return passHash;
	}

	public void setPassHash(String passHash) {
		this.passHash = passHash;
	}

	public String getCombinationHash() {
		return combinationHash;
	}

	public void setCombinationHash(String combinationHash) {
		this.combinationHash = combinationHash;
	}

	public void setHash(final String passwordHash, final String combinationHash) {
		this.passHash = passwordHash;
		this.combinationHash = combinationHash;
	}

	public boolean isModified() {
		return modified;
	}

	public boolean isImporting() {
		return importing;
	}

	public boolean isAddingNode() {
		return addingNode;
	}

	public void setAddingNode(boolean addingNode) {
		this.addingNode = addingNode;
	}

	public void setImporting(boolean importing) {
		this.importing = importing;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
		EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.ModelChangeEvent, "setModified", Boolean.valueOf(this.modified)));

	}

	/**
	 * The vault version read from the file header. always the current version.
	 * so may not be the latest.
	 *
	 * @return
	 */
	public int getCurrentDataFileVersion() {
		return dataFileVersion;
	}

	public void setDataFileVersion(int dataFileVersion) {
		this.dataFileVersion = dataFileVersion;
	}

	public PassCombinationVO getPassVO() {
		return passVO;
	}

	public void setPassVO(PassCombinationVO passVO) {
		this.passVO = passVO;
	}

	public void setupTestData() {
		//root node
		itemsFlatList.add(new WalletItem(ItemType.category, "My Default Wallet 1"));


		WalletItem item1 = new WalletItem(ItemType.item, "PNC Bank");
		item1.setURL("https://pnc.com");
		WalletItem item2 = new WalletItem(ItemType.item, "GE Bank");
		item1.setURL("https://gecapital.com");

		WalletItem item3 = new WalletItem(ItemType.item, "Audi");
		WalletItem item4 = new WalletItem(ItemType.item, "Honda");


		itemsFlatList.add(new WalletItem(ItemType.category, "Bank Info"));
		itemsFlatList.add(item1);
		itemsFlatList.add(item2);
		itemsFlatList.add(new WalletItem(ItemType.category, "Car"));
		itemsFlatList.add(item3);
		itemsFlatList.add(item4);

		buildTreeFromFlatList();
	}

	public void setupEmptyWalletData(String rootName) {
		//root node
		itemsFlatList.add(new WalletItem(ItemType.category, rootName == null ? "Default eVault" : rootName));
		itemsFlatList.add(new WalletItem(ItemType.category, "Category 1"));
		itemsFlatList.add(new WalletItem(ItemType.item, "Item 1"));
		setModified(true);
	}

	public String dumpFlatList() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < itemsFlatList.size(); i++) {
			WalletItem item = itemsFlatList.get(i);
			sb.append(i + ":").append(item.toStringJson()).append("\n");
		}
		return sb.toString();
	}

	public WalletItem getRootItem() {
		if (itemsFlatList.size() == 0)
			return null;
		return itemsFlatList.get(0);
	}

	/**
	 * build the hierarchical relationships from the flat list.
	 * The parent and children of each item will be set.
	 */
	public void buildTreeFromFlatList() {
		if (itemsFlatList.size() == 0)
			return;

		WalletItem rootNode = itemsFlatList.get(0);


		//reset first
		for (int i = 0; i < itemsFlatList.size(); i++) {
			WalletItem item = itemsFlatList.get(i);
			item.setChildren(null);
			item.setParent(null);
		}


		WalletItem lastParent = rootNode;
		List<WalletItem> rootCats = new ArrayList<>();
		List<WalletItem> tempChildList = new ArrayList<>();
		for (int i = 1; i < itemsFlatList.size(); i++) {
			WalletItem item = itemsFlatList.get(i);

			if (ItemType.category == item.getType()) {
				//rootNode.addChild(item);
				rootCats.add(item);

				if (lastParent != item) {
					//parent changed.
					Collections.sort(tempChildList);

					for (WalletItem c : tempChildList) {
						lastParent.addChild(c);
					}
					tempChildList.clear();
					lastParent = item;
				}


			} else {
				tempChildList.add(item);
				//lastParent.addChild(item);
			}
		}

		Collections.sort(rootCats);
		for (WalletItem c : rootCats) {
			rootNode.addChild(c);
		}

		Collections.sort(tempChildList);
		for (WalletItem c : tempChildList) {
			lastParent.addChild(c);
		}


	}


	/**
	 * rebuild the flat list from the tree by walking it.
	 */
	public void buildFlatListFromTree() {
		WalletItem root = itemsFlatList.get(0);
		itemsFlatList.clear();
		walkTree(root, itemsFlatList);
	}

	protected void walkTree(WalletItem parent, List<WalletItem> result) {
		result.add(parent);
		if (parent.getChildren() != null) {
			for (WalletItem child : parent.getChildren()) {
				walkTree(child, result);
			}
		}
	}


	public boolean isRoot(WalletItem item) {
		return itemsFlatList.get(0).equals(item);
	}


	protected int getItemIndex(WalletItem item) {
		int index = -1;
		for (int i = 0; i < itemsFlatList.size(); i++) {
			if (itemsFlatList.get(i).equals(item)) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new RuntimeException("something is wrong, can't find index for the item in the flat list:" + item);
		}

		return index;
	}


	public void addItem(final WalletItem parentItem, final WalletItem newItem) {
		if (isRoot(parentItem)) {
			if (newItem.getType() != ItemType.category)
				throw new RuntimeException("Can only add category items to the root.");
			parentItem.addChild(newItem);
			itemsFlatList.add(newItem);

		} else {

			int index;
			if (parentItem.getChildren() == null || parentItem.getChildren().size() == 0) {
				//this parent category is empty, add after it
				index = getItemIndex(parentItem);
			} else {
				//find the last child of the parentItem in the flat list and insert after that
				WalletItem lastChildren = parentItem.getChildren().get(parentItem.getChildren().size() - 1);
				index = getItemIndex(lastChildren);

			}


			if (index == itemsFlatList.size() - 1)
				//last one, just append
				itemsFlatList.add(newItem);
			else
				itemsFlatList.add(index + 1, newItem);

			//add to tree structure
			parentItem.addChild(newItem);
		}
		setModified(true);
	}

	public void removeItem(final WalletItem item) {
		item.getParent().removeChild(item);
		itemsFlatList.remove(item);
		setModified(true);
	}


	/**
	 * Find the item with the GUID on the tree.
	 *
	 * @param GUID
	 * @return
	 */
	public WalletItem getNodeByGUID(final String GUID) {
		buildFlatListFromTree();
		for (WalletItem item : itemsFlatList) {
			if (item.getSysGUID().equals(GUID))
				return item;
		}
		return null;

	}

	public WalletItem getWalletItem(String sysGUID) {
		for (WalletItem walletItem : itemsFlatList) {
			if (walletItem.getSysGUID().equals(sysGUID)) {
				return walletItem;
			}
		}
		return null;
	}


	/**
	 * @return PassCombinationEncryptionAdaptor
	 */
	public PassCombinationVO getUserEnteredPassForVerification() {
		if (dataFileVersion >= 13) {
			return passVO;
		} else {
			//v12 format
			PassCombinationVO ret = new PassCombinationEncryptionAdaptor();

			//use getter. passPlain.spinner2 should have been cleared.
			ret.setPass(passVO.getSpinner2() + passVO.getPass() + passVO.getSpinner1() + passVO.getSpinner3());  //v12 version format
			return ret;
		}
	}

	public PassCombinationVO getPassVOForEncryptor() {
		//ret.setPass(passPlain.spinner2 + passPlain.pass + passPlain.spinner1 + passPlain.spinner3);  //v12 version format
		return getUserEnteredPassForVerification();
	}


	public int getDeletedEntriesInStore() {
		return deletedEntriesInStore;
	}

	public void setDeletedEntriesInStore(int deletedEntriesInStore) {
		this.deletedEntriesInStore = deletedEntriesInStore;
	}

	public WalletModel getImpModel() {
		return impModel;
	}

	public void setImpModel(WalletModel impModel) {
		this.impModel = impModel;
	}

	public String getVaultFileName() {
		return vaultFileName;
	}

	public void setVaultFileName(String vaultFileName) {
		this.vaultFileName = vaultFileName;
	}


	public WalletItem findItem(String sysGUID) {
		for (WalletItem walletItem : itemsFlatList) {
			if (walletItem.getSysGUID().equals(sysGUID))
				return walletItem;
		}

		return null;

	}

	public PassCombinationVO getExportVaultPass() {
		return exportVaultPass;
	}

	public void setExportVaultPass(PassCombinationVO exportVaultPass) {
		this.exportVaultPass = exportVaultPass;
	}

	public String getExportVaultFileName() {
		return exportVaultFileName;
	}

	public void setExportVaultFileName(String exportVaultFileName) {
		this.exportVaultFileName = exportVaultFileName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WalletModel that = (WalletModel) o;

		if (itemsFlatList.size()!=that.itemsFlatList.size())
			return false;

		for (int i = 0; i < itemsFlatList.size(); i++) {
			WalletItem walletItem = itemsFlatList.get(i);
			if (!walletItem.compareContent(that.itemsFlatList.get(i))) //calls wallet item compare
				return false;
		}

		return true;
	}


}
