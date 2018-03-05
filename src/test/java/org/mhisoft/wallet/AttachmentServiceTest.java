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
import java.io.IOException;

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.security.HashingUtils;
import org.mhisoft.wallet.model.FileAccessEntry;
import org.mhisoft.wallet.model.FileAccessFlag;
import org.mhisoft.wallet.model.FileAccessTable;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.PassCombinationEncryptionAdaptor;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.AttachmentService;
import org.mhisoft.wallet.service.DataService;
import org.mhisoft.wallet.service.DataServiceFactory;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.service.WalletService;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */


//to set logger level
// -Djava.util.logging.config.file="logging.properties"


public class AttachmentServiceTest {

	AttachmentService attachmentService = new AttachmentService();
    WalletService walletService = new WalletService();

	String guid1, guid2;
	static int version =13;
	static int LATEST_VERSION =WalletModel.LATEST_DATA_VERSION;
	static String dir= "." +  File.separator + "target" + File.separator +"classes"+File.separator;
	static String storeFileName = dir + "AttachmentServiceTest.dat";
	static String attStoreFile = dir +"AttachmentServiceTest_attachments.dat";
	static String file1 = "."+ File.separator +"target"+ File.separator +"classes"+ File.separator +"1463467646_61.png";
	static String file2 = "."+ File.separator +"target"+ File.separator +"test-classes" + File.separator +"LICENSE";


	private void saveAttachments(int version) throws HashingUtils.CannotPerformOperationException {
		new File(storeFileName).delete();
		new File(attStoreFile).delete();

		FileAccessTable t = new FileAccessTable();
		FileAccessEntry fileEntry = t.addEntry();
		fileEntry.setFileName(file1);
		guid1 = fileEntry.getGUID();

		FileAccessEntry fileEntry2 = t.addEntry();
		fileEntry2.setFileName(file2);
		guid2 = fileEntry2.getGUID();

		Assert.assertEquals(t.getSize(), 2);

		WalletModel model = new WalletModel();
		model.setDataFileVersion(version);
		String hash = HashingUtils.createHash("testPa!ss213%");
		model.setPassHash(hash);
		String combinationHash = HashingUtils.createHash("112233");
		model.setCombinationHash(combinationHash);
		model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%", "112233"));

		WalletItem cat1 = new WalletItem(ItemType.category, "cat1");
		model.getItemsFlatList().add(cat1);

		WalletItem item1 = new WalletItem(ItemType.item, "item1");
		item1.setSysGUID(guid1);
		item1.setAttachmentEntry(fileEntry);
		model.getItemsFlatList().add(item1);
		WalletItem item2 = new WalletItem(ItemType.item, "item2");
		item2.setSysGUID(guid2);
		item2.setAttachmentEntry(fileEntry2);
		model.getItemsFlatList().add(item2);


		//save vault
		System.out.println("save store version:"+ version);
		DataService ds = DataServiceFactory.createDataService(version);
		ds.saveToFile(storeFileName,model, model.getEncryptor() );
		attachmentService.saveAttachments(attStoreFile, model, model.getEncryptor());
	}

	@Test()
	public void testWriteFileAcccessTable() {
		try {
			saveAttachments(WalletModel.LATEST_DATA_VERSION);
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}

	}

	@Test(dependsOnMethods = {"testWriteFileAcccessTable"})
	public void testReadFileAccessTable() {
		try {

			String dataFile = attStoreFile;
			WalletModel model = new WalletModel();
			model.setDataFileVersion(version);
			model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%", "112233"));

			FileAccessTable t = attachmentService.read(dataFile, model.getEncryptor());

			Assert.assertEquals(t.getSize(), 2);


			int i = 0;

			FileAccessEntry entry1 = t.getEntries().get(0);
			FileAccessEntry entry2 = t.getEntries().get(1);

			byte[] bytesEntry1 = attachmentService.readFileContent(14, dataFile, entry1, model.getEncryptor());
			byte[] orgin1 = FileUtils.readFile(new File("./target/classes/1463467646_61.png"));
			Assert.assertEquals(bytesEntry1, orgin1);

			byte[] bytesEntry2 = attachmentService.readFileContent(LATEST_VERSION, dataFile, entry2, model.getEncryptor());
			byte[] orgin2 = FileUtils.readFile(new File("./target/test-classes/LICENSE"));
			Assert.assertEquals(bytesEntry2, orgin2);

			Assert.assertEquals(guid1,t.getEntries().get(0).getGUID() );
			Assert.assertEquals(guid2,t.getEntries().get(1).getGUID() );

			Assert.assertEquals(t.getEntries().get(0).getSize(), new File(file1).length());
			Assert.assertEquals(t.getEntries().get(1).getSize(), new File(file2).length());

			Assert.assertEquals(t.getEntries().get(0).getFileName(), "1463467646_61.png");
			//Assert.assertEquals(t.getEntries().get(1).getFileName(), "LICENSE");

//
//			FileOutputStream out = new FileOutputStream(
//					//parts[0]+parts[1]     +"_test_rewritten." + parts[2]
//					"./target/classes/testReadFileAccessTable_" + i + ".png"
//			);
//			out.write(bytes);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* just upgrade, attachment entries in the model is not modified */

	@Test
	public void testUpgradeStore() throws  IOException {
		try {
			saveAttachments(13);
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}

		WalletModel model = new WalletModel();
		model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%", "112233"));
		model = walletService.loadVaultIntoModel(storeFileName, model.getEncryptor() );

		Assert.assertEquals(model.getItemsFlatList().size(), 3);
		Assert.assertNotNull(model.getItemsFlatList().get(1).getAttachmentEntry());
		Assert.assertNotNull(model.getItemsFlatList().get(2).getAttachmentEntry());

		Assert.assertTrue(model.getItemsFlatList().get(1).getAttachmentEntry().getEncSize()>0);
		Assert.assertTrue(model.getItemsFlatList().get(2).getAttachmentEntry().getEncSize()>0);


		//opened a old version file, need to save to v13 version on close. .
		if (model.getCurrentDataFileVersion()<WalletModel.LATEST_DATA_VERSION) {
			ServiceRegistry.instance.getWalletModel().setModified(true);
			//the data file version in the model is the old version. do not set to latest.
			//upgrade will happen if not the latest.
			//model.setDataFileVersion(WalletModel.LATEST_DATA_VERSION);
		}


		//now save to last store version
		walletService.saveVault(storeFileName,model, model.getEncryptor()  );

		//read back to verify
		model = walletService.loadVaultIntoModel(storeFileName, model.getEncryptor() );
		Assert.assertEquals(model.getCurrentDataFileVersion(), WalletModel.LATEST_DATA_VERSION);
		Assert.assertEquals(model.getItemsFlatList().size(), 3);
		Assert.assertNotNull(model.getItemsFlatList().get(1).getAttachmentEntry());
		Assert.assertNotNull(model.getItemsFlatList().get(2).getAttachmentEntry());

		Assert.assertTrue(model.getItemsFlatList().get(1).getAttachmentEntry().getEncSize()>0);
		Assert.assertTrue(model.getItemsFlatList().get(2).getAttachmentEntry().getEncSize()>0);


		FileAccessTable t = attachmentService.read(attStoreFile, model.getEncryptor());
		FileAccessEntry entry1 = t.getEntries().get(0);
		FileAccessEntry entry2 = t.getEntries().get(1);


		byte[] bytesEntry1 = attachmentService.readFileContent(WalletModel.LATEST_DATA_VERSION, attStoreFile, entry1, model.getEncryptor());
		byte[] orgin1 = FileUtils.readFile(new File("./target/classes/1463467646_61.png"));
		Assert.assertEquals(bytesEntry1, orgin1);



	}


		/*  attachment entries in the model is  modified */

	@Test
	public void testUpgradeStore_s2() throws IOException {
		try {
			saveAttachments(13);
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}

		WalletModel model = new WalletModel();
		model.initEncryptor(new PassCombinationEncryptionAdaptor("testPa!ss213%", "112233"));
		model = walletService.loadVaultIntoModel(storeFileName, model.getEncryptor() );

		//opened a old version file, need to save to v13 version on close. .
		if (model.getCurrentDataFileVersion()<WalletModel.LATEST_DATA_VERSION) {
			ServiceRegistry.instance.getWalletModel().setModified(true);
			//the data file version in the model is the old version. do not set to latest.
			//upgrade will happen if not the latest.
			//model.setDataFileVersion(WalletModel.LATEST_DATA_VERSION);
		}


		//delete the item1 attachment
		model.getItemsFlatList().get(1).getAttachmentEntry().setAccessFlag(FileAccessFlag.Delete);

		//now save to last store version
		walletService.saveVault(storeFileName,model, model.getEncryptor()  );

		//read back to verify
		model = walletService.loadVaultIntoModel(storeFileName, model.getEncryptor() );
		Assert.assertEquals(model.getItemsFlatList().size(), 3);

		Assert.assertNull(model.getItemsFlatList().get(1).getAttachmentEntry());
		Assert.assertNotNull(model.getItemsFlatList().get(2).getAttachmentEntry());

		Assert.assertTrue(model.getItemsFlatList().get(2).getAttachmentEntry().getEncSize()>0);


		//validate the file content

		FileAccessEntry entry1 = model.getItemsFlatList().get(1).getAttachmentEntry();
		FileAccessEntry entry2 = model.getItemsFlatList().get(2).getAttachmentEntry();

		byte[] bytesEntry2 = attachmentService.readFileContent(LATEST_VERSION, attStoreFile, entry2, model.getEncryptor());
		byte[] orgin2 = FileUtils.readFile(new File("./target/test-classes/LICENSE"));
		Assert.assertEquals(bytesEntry2, orgin2);


	}


}
