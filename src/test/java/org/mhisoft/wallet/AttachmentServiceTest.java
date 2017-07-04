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
import org.mhisoft.wallet.model.FileAccessEntry;
import org.mhisoft.wallet.model.FileAccessTable;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.AttachmentService;
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


	String guid1, guid2;

	@Test()
	public void testWriteFileAcccessTable() {
		new File("./target/classes/AttachmentServiceTest_testFileAcccessTable.dat").delete();

		FileAccessTable t = new FileAccessTable();
		FileAccessEntry fileEntry = t.addEntry();
		fileEntry.setFile(new File("./target/classes/1463467646_61.png"));
		guid1 = fileEntry.getGUID();

		FileAccessEntry fileEntry2 = t.addEntry();
		fileEntry2.setFile(new File("./target/classes/1463467888_13.png"));
		guid2 = fileEntry2.getGUID();

		Assert.assertEquals(t.getSize(), 2);

		WalletModel model = new WalletModel();
		model.initEncryptor(new PassCombinationVO("testPa!ss213%", "112233"));


		attachmentService.write("./target/classes/AttachmentServiceTest_testFileAcccessTable.dat", t, model.getEncryptor());

	}

	@Test(dependsOnMethods = {"testWriteFileAcccessTable"})
	public void testReadFileAccessTable() {
		try {

			String dataFile = "./target/classes/AttachmentServiceTest_testFileAcccessTable.dat";
			WalletModel model = new WalletModel();
			model.initEncryptor(new PassCombinationVO("testPa!ss213%", "112233"));

			FileAccessTable t = attachmentService.read(dataFile, model.getEncryptor());

			Assert.assertEquals(t.getSize(), 2);


			int i = 0;

			FileAccessEntry entry1 = t.getEntries().get(0);
			FileAccessEntry entry2 = t.getEntries().get(1);

			byte[] bytesEntry1 = attachmentService.readFileContent(dataFile, entry1, model.getEncryptor());
			byte[] orgin1 = FileUtils.readFile(new File("./target/classes/1463467646_61.png"));
			Assert.assertEquals(bytesEntry1, orgin1);

			byte[] bytesEntry2 = attachmentService.readFileContent(dataFile, entry2, model.getEncryptor());
			byte[] orgin2 = FileUtils.readFile(new File("./target/classes/1463467888_13.png"));
			Assert.assertEquals(bytesEntry2, orgin2);

			Assert.assertEquals(guid1,t.getEntries().get(0).getGUID() );
			Assert.assertEquals(guid2,t.getEntries().get(1).getGUID() );

			Assert.assertEquals(t.getEntries().get(0).getSize(), 366);
			Assert.assertEquals(t.getEntries().get(1).getSize(), 412);

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

}
