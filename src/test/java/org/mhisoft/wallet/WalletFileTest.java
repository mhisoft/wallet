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
import org.mhisoft.common.util.security.HashingUtils;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.PassCombinationEncryptionAdaptor;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.*;

import static org.junit.Assert.assertEquals;

/**
 * Description: WalletModelTest
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletFileTest {
	WalletModel model;
	WalletItem root;
	WalletItem bNode;
	WalletItem eNode;
	WalletItem fNode;
	WalletItem gNode;
	WalletItem cNode;
	WalletItem dNode;

	//latest
	DataService dataServicev12 = DataServiceFactory.createDataService(12);
	DataService dataServicev13 = DataServiceFactory.createDataService(13);


	WalletService walletService;

	@Before
	public  void setup() {

		/*
		     root
		        --b
		        --c
		        	--d
		            --e
		        --f
		        	--g

		 */

		model = new WalletModel();
		walletService = ServiceRegistry.instance.getService(BeanType.singleton, WalletService.class)  ;



		//root node
		root = new WalletItem(ItemType.category, "root");
		model.getItemsFlatList().add(root);
		bNode = new WalletItem(ItemType.category, "b");
		model.getItemsFlatList().add(bNode);
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

	}





	@Test
	public void testSaveFile() {
		try {
			model.getItemsFlatList().clear();
			model.setupTestData();
			String hash = HashingUtils.createHash("testPa!ss213%");
			model.setPassHash(hash);
			model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%", "112233"));
			//protype model is used in test
			ServiceRegistry.instance.getWalletForm().setModel(model);
			DataServiceFactory.createDataService(11).saveToFile("testv11.dat", model, model.getEncryptor());
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
			String combinationHash = HashingUtils.createHash("034509");
			model.setCombinationHash(combinationHash);
			model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%", "034509"));


			//walletService.saveToFile("test_v12.dat", model, model.getEncryptor());
			DataService dataServicev12 = DataServiceFactory.createDataService(12);
			dataServicev12.saveToFile("test_v12.dat", model, model.getEncryptor());

			StoreVO storeVO = walletService.loadVault("test_v12.dat" , model.getEncryptor());
			model.setItemsFlatList(storeVO.getWalletItems());
			model.setDeletedEntriesInStore(storeVO.getDeletedEntriesInStore());
			assertEquals(7, model.getItemsFlatList().size());
			assertEquals(hash, storeVO.getHeader().getPassHash());
		} catch (HashingUtils.CannotPerformOperationException | WalletServiceException e) {
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
			model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%","112233"));

			DataService dataServicev10 = DataServiceFactory.createDataService(10);
			//save
			dataServicev10.saveToFile("test_v10.dat", model, model.getEncryptor());
			//read
			StoreVO storeVO = dataServicev10.readFromFile("test_v10.dat",model.getEncryptor());

			model.setItemsFlatList(storeVO.getWalletItems());
			assertEquals(7, model.getItemsFlatList().size());
			assertEquals(hash, storeVO.getHeader().getPassHash());
		} catch (HashingUtils.CannotPerformOperationException | WalletServiceException e) {
			e.printStackTrace();
		}


	}


	public void testReadFilev11() {
		try {
			model.initEncryptor(new PassCombinationEncryptionAdaptor("12Abc12334&5AB1310","112233"));

			DataService dataServicev11 = DataServiceFactory.createDataService(11);
			StoreVO storeVO = dataServicev11.readFromFile("test_DefaultWallet_v11.dat",model.getEncryptor());
			model.setItemsFlatList(storeVO.getWalletItems());


			DataService dataServicev12 = DataServiceFactory.createDataService(12);
			//save
			String hash = HashingUtils.createHash("testPa!ss213%");
			model.setPassHash(hash);
			dataServicev12.saveToFile("test_DefaultWallet_v12.dat", model, model.getEncryptor());

		} catch (Exception e) {
			e.printStackTrace();
		}


	}




	//read from v10 format and write to v12 format.
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
			model.setDataFileVersion(12);
			model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%","112233"));
			model.setPassHash(hash);
			DataService dataServicev10 = DataServiceFactory.createDataService(10);

			//latest
			DataService dataServicev12 = DataServiceFactory.createDataService(12);


			dataServicev10.saveToFile("test_v10.dat", model, model.getEncryptorForRead()); //v12 encryptor
			StoreVO storeVO = dataServicev10.readFromFile("test_v10.dat",model.getEncryptorForRead());

			//now save to v12 format
			model.setItemsFlatList(storeVO.getWalletItems());
			dataServicev12.saveToFile("test_v12.dat", model, model.getEncryptorForRead());

			//verify by reding it
			storeVO = dataServicev12.readFromFile("test_v12.dat", model.getEncryptorForRead());
			model.setItemsFlatList(storeVO.getWalletItems());

			model.setItemsFlatList(storeVO.getWalletItems());
			assertEquals(7, model.getItemsFlatList().size());
			assertEquals(hash, storeVO.getHeader().getPassHash());
		} catch (HashingUtils.CannotPerformOperationException | WalletServiceException  e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testFileConversionV12toV13() {
		try {
			File f = new File("test_v12.dat");
			f.delete();

			model.getItemsFlatList().clear();
			model.setupTestData();
			//protype model is used in test
			ServiceRegistry.instance.getWalletForm().setModel(model);

			PassCombinationVO passVO = new PassCombinationEncryptionAdaptor("testPa!ss213%","112233") ;
			String hash = HashingUtils.createHash(passVO.getPass());
			model.setDataFileVersion(12);
			model.initEncryptor(passVO);
			model.setPassHash(hash);


			//save to v12 format
			dataServicev12.saveToFile("test_v12.dat", model, model.getEncryptorForRead()); //v12 encryptor
			//read the file using v12 service impl
			StoreVO storeVO = dataServicev12.readFromFile("test_v12.dat",model.getEncryptorForRead());
			assertEquals(7, model.getItemsFlatList().size());
			assertEquals(hash, storeVO.getHeader().getPassHash());

			//write to v13 file format.
			model.setDataFileVersion(13);
			model.initEncryptor(passVO);
			hash = HashingUtils.createHash(passVO.getPassAndCombination());
			model.setPassHash(hash);
			String combinationHash = HashingUtils.createHash("112233");
			model.setCombinationHash(combinationHash);
			dataServicev13.saveToFile("test_v13.dat", model, model.getEncryptor());
			//read it back.
			storeVO = dataServicev13.readFromFile("test_v13.dat",model.getEncryptorForRead());
			assertEquals(7, model.getItemsFlatList().size());
			assertEquals(hash, storeVO.getHeader().getPassHash());
			assertEquals(combinationHash, storeVO.getHeader().getCombinationHash());

			 f = new File("test_v13.dat");
			f.delete();


		} catch (HashingUtils.CannotPerformOperationException | WalletServiceException  e) {
			e.printStackTrace();
		}

	}


}
