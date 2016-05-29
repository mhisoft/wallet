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
import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.DataService;
import org.mhisoft.wallet.service.DataServiceFactory;
import org.mhisoft.wallet.service.FileContent;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.service.WalletService;

import junit.framework.Assert;

/**
 * Description: WalletModelTest
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletFileTest {
	WalletModel model;
	WalletItem root;
	WalletItem eNode;
	WalletItem fNode;
	WalletItem gNode;
	WalletItem cNode;
	WalletItem dNode;


	WalletService walletService;

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
		walletService = ServiceRegistry.instance.getService(BeanType.singleton, WalletService.class)  ;



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
		model.buildTreeFromFlatList();

		 Encryptor.createInstance("testit&(9938447");
	}





	@Test
	public void testSaveFile() {
		try {
			model.getItemsFlatList().clear();
			model.setupTestData();
			String hash = HashingUtils.createHash("testPa!ss213%");
			model.setPassHash(hash);
			//protype model is used in test
			ServiceRegistry.instance.getWalletForm().setModel(model);
			DataServiceFactory.createDataService(11).saveToFile("testv11.dat", model);
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReadFilev12() {
		try {
			File f = new File("test_v12.dat");
			f.delete();

			model.getItemsFlatList().clear();
			model.setupTestData();
			//protype model is used in test
			ServiceRegistry.instance.getWalletForm().setModel(model);

			String hash = HashingUtils.createHash("testPa!ss213%");
			model.setPassHash(hash);
			Encryptor.createInstance("testPa!ss213%");


			walletService.saveToFile("test_v12.dat", model);
			FileContent fileContent = walletService.readFromFile("test_v12.dat", Encryptor.getInstance());

			model.setItemsFlatList(fileContent.getWalletItems());
			Assert.assertEquals(7, model.getItemsFlatList().size());
			Assert.assertEquals(hash, fileContent.getHeader().getPassHash());
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}


	}
	@Test
	public void testReadFilev10() {
		try {
			File f = new File("test_v10.dat");
			f.delete();

			model.getItemsFlatList().clear();
			model.setupTestData();
			//protype model is used in test
			ServiceRegistry.instance.getWalletForm().setModel(model);

			String hash = HashingUtils.createHash("testPa!ss213%");
			model.setPassHash(hash);
			Encryptor.createInstance("testPa!ss213%");

			DataService dataServicev10 = DataServiceFactory.createDataService(10);
			//save
			dataServicev10.saveToFile("test_v10.dat", model);
			//read
			FileContent fileContent = dataServicev10.readFromFile("test_v10.dat", Encryptor.getInstance());

			model.setItemsFlatList(fileContent.getWalletItems());
			Assert.assertEquals(7, model.getItemsFlatList().size());
			Assert.assertEquals(hash, fileContent.getHeader().getPassHash());
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}


	}


	public void testReadFilev11() {
		try {
			Encryptor.createInstance("12Abc12334&5AB1310");

			DataService dataServicev11 = DataServiceFactory.createDataService(11);
			FileContent fileContent = dataServicev11.readFromFile("test_DefaultWallet_v11.dat",Encryptor.getInstance());
			model.setItemsFlatList(fileContent.getWalletItems());


			DataService dataServicev12 = DataServiceFactory.createDataService(12);
			//save
			String hash = HashingUtils.createHash("testPa!ss213%");
			model.setPassHash(hash);
			dataServicev12.saveToFile("test_DefaultWallet_v12.dat", model);

		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	//read from v10 format and write to v11 format.
	@Test
	public void testReadOldVersoinFile() {
		try {
			File f = new File("test_v10.dat");
			f.delete();
			f = new File("test_v12.dat");
			f.delete();

			model.getItemsFlatList().clear();
			model.setupTestData();
			//protype model is used in test
			ServiceRegistry.instance.getWalletForm().setModel(model);

			String hash = HashingUtils.createHash("testPa!ss213%");
			Encryptor.createInstance("testPa!ss213%");
			model.setPassHash(hash);
			DataService dataServicev10 = DataServiceFactory.createDataService(10);

			//latest
			DataService dsLatest = DataServiceFactory.createDataService();


			dataServicev10.saveToFile("test_v10.dat", model);
			FileContent fileContent = dataServicev10.readFromFile("test_v10.dat",Encryptor.getInstance());

			//now save to v11 format
			model.setItemsFlatList(fileContent.getWalletItems());
			dsLatest.saveToFile("test_v12.dat", model);

			//verify by reding it
			fileContent = dsLatest.readFromFile("test_v12.dat", Encryptor.getInstance());
			model.setItemsFlatList(fileContent.getWalletItems());

			model.setItemsFlatList(fileContent.getWalletItems());
			Assert.assertEquals(7, model.getItemsFlatList().size());
			Assert.assertEquals(hash, fileContent.getHeader().getPassHash());
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}


	}


}
