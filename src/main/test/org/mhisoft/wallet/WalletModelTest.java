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

package org.mhisoft.wallet;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;

import junit.framework.Assert;

/**
 * Description: WalletModelTest
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletModelTest {
	WalletModel model;

	@Before
	public  void setup() {

		/*
		     root
		        --b
		        --c --d
		            --e
		        --f --g

		 */

		model = new WalletModel();
		//root node
		model.getItemsFlatList().add(new WalletItem(ItemType.category, "root"));
		model.getItemsFlatList().add(new WalletItem(ItemType.category, "b"));
		model.getItemsFlatList().add(new WalletItem(ItemType.category, "c"));
		model.getItemsFlatList().add(new WalletItem(ItemType.item, "d"));
		model.getItemsFlatList().add(new WalletItem(ItemType.item, "e"));
		model.getItemsFlatList().add(new WalletItem(ItemType.category, "f"));
		model.getItemsFlatList().add(new WalletItem(ItemType.item, "g"));
		model.buildRelations();
	}

	@Test
	public void testWalkTree() {


		WalletItem root =model.getItemsFlatList().get(0);
		Assert.assertEquals(3, root.getChildren().size());
		WalletItem cNode = model.getNodeByGUID(model.getItemsFlatList().get(2).getSysGUID());
		Assert.assertEquals(2, cNode.getChildren().size());
		Assert.assertEquals(cNode.getParent(), root);



		//to flat list again.
		model.updateFlatList();
		Assert.assertEquals(model.getItemsFlatList().get(0).getName(), "root");
		Assert.assertEquals(model.getItemsFlatList().get(1).getName(), "b");
		Assert.assertEquals(model.getItemsFlatList().get(2).getName(), "c");
		Assert.assertEquals(model.getItemsFlatList().get(3).getName(), "d");
		Assert.assertEquals(model.getItemsFlatList().get(4).getName(), "e");
		Assert.assertEquals(model.getItemsFlatList().get(5).getName(), "f");
		Assert.assertEquals(model.getItemsFlatList().get(6).getName(), "g");

	}


	//add a node to tree and get flat list
	@Test
	public void testUpdateFlatList() {

		//to flat list again.
		model.updateFlatList();
		WalletItem root =model.getItemsFlatList().get(0);


		root.addChild(new WalletItem(ItemType.item, "h"));


		WalletItem cNode = model.getNodeByGUID(model.getItemsFlatList().get(2).getSysGUID());
		cNode.addChild(new WalletItem(ItemType.item, "c2"));


		/*
		     root
		        --b
		        --c --d
		            --e
		            --c2
		        --f --g
		        --h

		 */


		model.updateFlatList();

		Assert.assertEquals(model.getItemsFlatList().get(0).getName(), "root");
		Assert.assertEquals(model.getItemsFlatList().get(1).getName(), "b");
		Assert.assertEquals(model.getItemsFlatList().get(2).getName(), "c");
		Assert.assertEquals(model.getItemsFlatList().get(3).getName(), "d");
		Assert.assertEquals(model.getItemsFlatList().get(4).getName(), "e");
		Assert.assertEquals(model.getItemsFlatList().get(5).getName(), "c2");

		Assert.assertEquals(model.getItemsFlatList().get(6).getName(), "f");
		Assert.assertEquals(model.getItemsFlatList().get(7).getName(), "g");
		Assert.assertEquals(model.getItemsFlatList().get(8).getName(), "h");
	}


	@Test
	public void testSaveFile() {
		model.getItemsFlatList().clear();
		model.setupTestData();

		model.saveToFile("test1.dat");
	}

	@Test
	public void testReadFile() {
		File f = new File("test2.dat");
		f.delete();

		model.getItemsFlatList().clear();
		model.setupTestData();
		model.saveToFile("test2.dat");

		model.setItemsFlatList(model.readFromFile("test2.dat"));
		Assert.assertEquals(7, model.getItemsFlatList().size());


	}
}
