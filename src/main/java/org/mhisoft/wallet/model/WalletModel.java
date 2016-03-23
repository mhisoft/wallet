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

	public boolean isRoot(WalletItem item) {
		return  itemsFlatList.get(0).equals(item);
	}

}
