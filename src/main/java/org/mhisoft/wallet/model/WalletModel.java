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

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.Serializer;

/**
 * Description: The model for the wallet view.
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletModel {
	List<WalletItem> itemsFlatList = new ArrayList<>();
	WalletItem currentItem;

	public WalletModel() {
		//
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

		buildRelations();
	}

	/**
	 * build the hierarchical relationships from the flat list.
	 * The parent and children of each item will be set.
	 */
	public void buildRelations() {
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
	public void updateFlatList() {
		WalletItem root = itemsFlatList.get(0);
		itemsFlatList.clear();
		walkTree(root, itemsFlatList);
	}

	void walkTree(WalletItem parent, List<WalletItem> result) {
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


	/**
	 * Find the item with the GUID on the tree.
	 * @param GUID
	 * @return
	 */
	public WalletItem getNodeByGUID(final String GUID) {
		updateFlatList();
		for (WalletItem item : itemsFlatList) {
			if (item.getSysGUID().equals(GUID))
				return item;
		}
		return null;

	}

	private static  byte[] DELIMITER_bytes ; //=  new byte[] { (byte)0x00, (byte)0x00 };
	static {

		byte[] b00 =new byte[] { (byte)0x00, (byte)0x00 };
		byte[] b1 = FileUtils.concatenateByteArrays("<-00|".getBytes(), b00  );
		DELIMITER_bytes  = FileUtils.concatenateByteArrays( b1, "|00->".getBytes() );


		//DELIMITER_bytes = "<-00|+b00+|00->"
	}
//	byte[] bytes = new byte[2];
//	Arrays.fill( bytes, (byte) 0 );


//	public static byte[] intToBytes(int aInt) {
//		return ByteBuffer.allocate(4).putInt(aInt).array();
//	}
//
//	//4 bytes to int
//	public static int bytesToInt(byte[] bytes ) {
//		if (bytes.length!=4)
//			throw new RuntimeException("Byte array for an int is 4 bytes");
//		ByteBuffer buf = ByteBuffer.wrap(bytes);
//		return  buf.getInt();
//	}

	public void saveToFile(final String filename) {
		FileOutputStream stream = null;


		try {
			stream = new FileOutputStream(filename);
			DataOutputStream outputStream = new DataOutputStream(stream);
			updateFlatList();
			Serializer<WalletItem> serializer  = new Serializer<WalletItem>();
			outputStream.write(FileUtils.intToByteArray(itemsFlatList.size()));

			int i=0;
			for (WalletItem item : itemsFlatList) {
				byte[] byteItem = serializer.serialize(item);
				int size = byteItem.length;

				//int to byte
				outputStream.write(FileUtils.intToByteArray(size));
				//write the object byte stream
				outputStream.write(byteItem);
				i++;
				System.out.println("write " + item.getName()+", size:" + size);
			}

		} catch ( IOException e) {
			e.printStackTrace();

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

			//don't read the whole file
			//byte[] bytesWholeFile = FileUtils.readFile(filename);
			FileInputStream fileInputStream = new FileInputStream( new File(filename));


			//read the size,  int, 4 bytes
			byte[] bytesInt = new byte[4];
			readBytes = fileInputStream.read(bytesInt);
			if (readBytes!=4)
				throw new RuntimeException("didn't read 4 bytes for a integer");

			int numberOfItems = FileUtils.byteArrayToInt(bytesInt);
			System.out.println();
			System.out.println("numberOfItems=" + numberOfItems);


			int k = 0;
			while (k < numberOfItems) {
				readBytes = fileInputStream.read(bytesInt);
				if (readBytes!=4)
					throw new RuntimeException("didn't read 4 bytes for a integer, k=" + k);
				int objectSize = FileUtils.byteArrayToInt(bytesInt);
				System.out.print("read item , size: " + objectSize);
				byte[] byteItem = new byte[objectSize];
				readBytes = fileInputStream.read(byteItem);
				if(readBytes==objectSize) {
					WalletItem item = serializer.deserialize(byteItem);
					System.out.println(", item: " + item.getName());
					ret.add(item);
					k++;
				}
				else {
					throw new RuntimeException("read " + readBytes +" bytes only, expected  objectSize:"+ objectSize);
				}

			}
		} catch (IOException e) {
			//end
			e.printStackTrace();


		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return ret;


	}

}
