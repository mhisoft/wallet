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
import java.util.List;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.Serializer;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description: The model for the wallet view.
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletModel {
	public static boolean debug = Boolean.getBoolean("debug");

	List<WalletItem> itemsFlatList = new ArrayList<>();
	WalletItem currentItem;

	public WalletModel() {
		Encryptor.createInstance("testit&(9938447");
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

	public String dumpFlatList() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < itemsFlatList.size(); i++) {
			WalletItem item = itemsFlatList.get(i);
			sb.append(i +":").append(item.toStringJson()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * build the hierarchical relationships from the flat list.
	 * The parent and children of each item will be set.
	 */
	public void buildTreeFromFlatList() {
		if (itemsFlatList.size()==0)
			return;

		WalletItem rootNode =itemsFlatList.get(0) ;
		WalletItem lastParent = rootNode;
		for (int i = 1; i < itemsFlatList.size(); i++) {
			WalletItem item = itemsFlatList.get(i);
			if (ItemType.category==item.getType()) {
				rootNode.addChild(item);
				lastParent = item;
			}
			else  {
				lastParent.addChild(item);

			}
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
		if (parent.getChildren()!=null) {
			for (WalletItem child : parent.getChildren()) {
			     walkTree(child, result);
			}
		}
	}


	public boolean isRoot(WalletItem item) {
		return  itemsFlatList.get(0).equals(item);
	}


	public void addItem(final WalletItem parentItem, final WalletItem newItem) {
		if ( isRoot(parentItem) ) {
			if (newItem.getType()!= ItemType.category)
			throw new RuntimeException("Can only add category items to the root.");
			parentItem.addChild(newItem);
			itemsFlatList.add(newItem);

		}
		else {

			//find the last child of the parentItem in the flat list and insert after that
			WalletItem lastChildren = parentItem.getChildren().get(parentItem.getChildren().size() - 1);


			int index = -1;
			for (int i = 0; i < itemsFlatList.size(); i++) {
				if (itemsFlatList.get(i).equals(lastChildren)) {
					index = i;
					break;
				}
			}

			if (index == itemsFlatList.size() - 1)
				//last one, just append
				itemsFlatList.add(newItem);
			else
				itemsFlatList.add(index+1, newItem);

			parentItem.addChild(newItem);
		}
	}

	public void  removeItem(final WalletItem item) {
		item.getParent().removeChild(item);
		itemsFlatList.remove(item);
	}


	/**
	 * Find the item with the GUID on the tree.
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

	private static int FIXED_RECORD_LENGTH =2000;

	public void saveToFile(final String filename) {
		FileOutputStream stream = null;


		try {

			stream = new FileOutputStream(filename);
			DataOutputStream outputStream = new DataOutputStream(stream);
			buildFlatListFromTree();
			Serializer<WalletItem> serializer  = new Serializer<WalletItem>();
			/*//#1: list size 4 bytes*/
			outputStream.write(FileUtils.intToByteArray(itemsFlatList.size()));

			int i=0;
			byte[] cipherParameters;
			for (WalletItem item : itemsFlatList) {
				byte[] _byteItem = serializer.serialize(item);
				byte[] enc = Encryptor.getInstance().encrypt(_byteItem);
				cipherParameters = Encryptor.getInstance().getCipherParameters();
				/*#2: cipherParameters size 4 bytes*/
				outputStream.write(FileUtils.intToByteArray(cipherParameters.length));

				/*#3: cipherParameters body*/
				outputStream.write(cipherParameters);

				byte[] byteItem = FileUtils.padByteArray(enc, FIXED_RECORD_LENGTH);

				/*#4: item body*/
				//write the object byte stream
				outputStream.write(byteItem);
				i++;
				System.out.println("write " + item.getName()+", size:" + byteItem.length);
			}

		} catch ( IOException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred when saveToFile()", e.getMessage());

		} finally {
			if (stream!=null)
				try {
					stream.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
		}
	}



	public List<WalletItem> readFromFile(final String filename) {
		//ByteArrayInputStream input = null;
		//byte[] readBuf = new byte[DELIMITER_bytes.length];
		List<WalletItem> ret = new ArrayList<>();
		Serializer<WalletItem> serializer  = new Serializer<WalletItem>();
		int readBytes = 0;
		try {
			Encryptor encryptor = Encryptor.createInstance("testit&(9938447");


			//don't read the whole file
			//byte[] bytesWholeFile = FileUtils.readFile(filename);
			FileInputStream fileInputStream = new FileInputStream( new File(filename));


			/*//read the size,  int, 4 bytes*/
			int numberOfItems = FileUtils.readInt(fileInputStream);
			System.out.println();
			System.out.println("numberOfItems=" + numberOfItems);


			int k = 0;
			while (k < numberOfItems) {

                /*#2: ciperParameters size 4 bytes*/
				int cipherParametersLength  = FileUtils.readInt(fileInputStream);

			    /*#3: cipherParameters body*/
				byte[] _byteCiper = new byte[cipherParametersLength];
				readBytes = fileInputStream.read(_byteCiper);
				if (readBytes!=cipherParametersLength)
					throw new RuntimeException("read " + readBytes +" bytes only, expected to read:"+ _byteCiper);

				AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(Encryptor.ALGORITHM);
				algorithmParameters.init(_byteCiper);

				/*#4: item body*/
				int objectSize =FIXED_RECORD_LENGTH;
				byte[] _byteItem = new byte[FIXED_RECORD_LENGTH];
				readBytes = fileInputStream.read(_byteItem);
				if(readBytes==objectSize) {
					_byteItem = FileUtils.trimByteArray(_byteItem);
					byte[] byteItem = encryptor.decrypt(_byteItem, algorithmParameters);
					WalletItem item = serializer.deserialize(byteItem);
					System.out.println(", item: " + item.getName());
					ret.add(item);
					k++;
				}
				else {
					throw new RuntimeException("read " + readBytes +" bytes only, expected  objectSize:"+ objectSize);
				}

			}
		} catch (Exception e) {
			//end
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred in readFromFile()", e.getMessage());
		}

		return ret;


	}

}
