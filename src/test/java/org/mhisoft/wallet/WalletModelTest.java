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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description: WalletModelTest
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WalletModelTest {
	WalletModel model;
	WalletItem root;
	WalletItem eNode;
	WalletItem fNode;
	WalletItem gNode;
	WalletItem cNode;
	WalletItem dNode;
	WalletItem hNode;


	@BeforeEach
	public void setup() {

		/*
			 root
		        --b
		        --c --d
		            --e
		        --f --g

		 */

		model = new WalletModel();

		//root node
		root = new WalletItem(ItemType.category, "root");
		model.getItemsFlatList().add(root);
		model.getItemsFlatList().add(new WalletItem(ItemType.category, "b"));
		cNode = new WalletItem(ItemType.category, "c");
		model.getItemsFlatList().add(cNode);
		dNode = new WalletItem(ItemType.item, "d");
		model.getItemsFlatList().add(dNode);
		eNode = new WalletItem(ItemType.item, "e");
		model.getItemsFlatList().add(eNode);
		fNode = new WalletItem(ItemType.category, "f");
		model.getItemsFlatList().add(fNode);
		gNode = new WalletItem(ItemType.item, "g");
		model.getItemsFlatList().add(gNode);

		hNode = new WalletItem(ItemType.category, "h-empty parent node");
		model.getItemsFlatList().add(hNode);


		model.buildTreeFromFlatList();


	}


	@Test
	public void testAddItem() {
		try {
			model.addItem(root, new WalletItem(ItemType.item, "root-child-item-1"));
			Assertions.fail("should not be able to add item to the root");
		} catch (Exception e) {
			//good
		}
		WalletItem rootChild1 = new WalletItem(ItemType.category, "root-child-cat-1");
		model.addItem(root, rootChild1);


		model.addItem(dNode.getParent(), new WalletItem(ItemType.item, "c-child-1"));
		WalletItem fChild1 = new WalletItem(ItemType.item, "f-child-1");
		model.addItem(gNode.getParent(), fChild1);

		WalletItem hChild = new WalletItem(ItemType.item, "hChild");
		model.addItem(hNode, hChild);

		Assertions.assertEquals(5, root.getChildren().size());
		Assertions.assertEquals(3, dNode.getParent().getChildren().size());
		Assertions.assertEquals(2, gNode.getParent().getChildren().size());
		Assertions.assertEquals(1, hNode.getChildren().size());


		//veriry the flat list

		model.buildFlatListFromTree();
		//last node now is root child 1
		Assertions.assertEquals(rootChild1, model.getItemsFlatList().get(model.getItemsFlatList().size() - 1));
		//fNode's last children is the  fChild1.
		Assertions.assertEquals(fChild1, fNode.getChildren().get(fNode.getChildren().size() - 1));
	}

	@Test
	public void testRemoveItem() {
		model.removeItem(gNode);
		Assertions.assertEquals(0, fNode.getChildren().size());

		model.removeItem(eNode);
		Assertions.assertEquals(1, cNode.getChildren().size());
		Assertions.assertEquals(dNode, model.getItemsFlatList().get(3));
		Assertions.assertEquals(fNode, model.getItemsFlatList().get(4));
	}

	@Test
	public void testWalkTree() {


		WalletItem root = model.getItemsFlatList().get(0);
		Assertions.assertEquals(4, root.getChildren().size());
		WalletItem cNode = model.getNodeByGUID(model.getItemsFlatList().get(2).getSysGUID());
		Assertions.assertEquals(2, cNode.getChildren().size());
		Assertions.assertEquals(cNode.getParent(), root);


		//to flat list again.
		model.buildFlatListFromTree();
		Assertions.assertEquals(model.getItemsFlatList().get(0).getName(), "root");
		Assertions.assertEquals(model.getItemsFlatList().get(1).getName(), "b");
		Assertions.assertEquals(model.getItemsFlatList().get(2).getName(), "c");
		Assertions.assertEquals(model.getItemsFlatList().get(3).getName(), "d");
		Assertions.assertEquals(model.getItemsFlatList().get(4).getName(), "e");
		Assertions.assertEquals(model.getItemsFlatList().get(5).getName(), "f");
		Assertions.assertEquals(model.getItemsFlatList().get(6).getName(), "g");

	}


	//add a node to tree and get flat list
	@Test
	public void testUpdateFlatList() {

		//to flat list again.
		model.buildFlatListFromTree();
		WalletItem root = model.getItemsFlatList().get(0);


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


		model.buildFlatListFromTree();

		Assertions.assertEquals(model.getItemsFlatList().get(0).getName(), "root");
		Assertions.assertEquals(model.getItemsFlatList().get(1).getName(), "b");
		Assertions.assertEquals(model.getItemsFlatList().get(2).getName(), "c");
		Assertions.assertEquals(model.getItemsFlatList().get(3).getName(), "d");
		Assertions.assertEquals(model.getItemsFlatList().get(4).getName(), "e");
		Assertions.assertEquals(model.getItemsFlatList().get(5).getName(), "c2");

		Assertions.assertEquals(model.getItemsFlatList().get(6).getName(), "f");
		Assertions.assertEquals(model.getItemsFlatList().get(7).getName(), "g");
		Assertions.assertEquals(model.getItemsFlatList().get(8).getName(), "h-empty parent node");
	}


	@Test
	public void whenSerializeAndDeserializeUsingJson(){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(model);
			System.out.println(jsonStr);
			WalletModel newModel =  mapper.readValue(jsonStr, WalletModel.class);
			Assertions.assertEquals(model, newModel);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
