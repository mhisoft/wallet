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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mhisoft.common.util.ByteArrayHelper;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.model.FileAccessEntry;
import org.mhisoft.wallet.model.FileAccessTable;
import org.testng.Assert;
import org.testng.annotations.Test;



/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class AttachmentServiceTest {

	@Test()
	public void testWriteFileAcccessTable() {

		FileAccessTable t = new FileAccessTable();
		FileAccessEntry fileEntry =  t.addEntry();
		fileEntry.setFile( new File("./target/classes/1463467646_61.png"));

		FileAccessEntry fileEntry2 =  t.addEntry();
		fileEntry2.setFile( new File("./target/classes/1463467888_13.png"));

		Assert.assertEquals(t.getSize(), 2);


		int currentPosition=0;
		int uuidSize=-1, entrySize, headerSize, posStart;

		File out=null;
		FileOutputStream fileOut =null;
		try {
			//write it out
			out = new File("./target/classes/AttachmentServiceTest_testFileAcccessTable.dat");
			fileOut = new FileOutputStream(out);
			DataOutputStream dataOut = new DataOutputStream(fileOut);

			//write the total number of entries first
		   	/*#0*/
			dataOut.writeInt( t.getEntries().size() );
			currentPosition =0;

			for (int i = 0; i < t.getEntries().size(); i++) {
				FileAccessEntry item = t.getEntries().get(i);

				if (uuidSize==-1) {
					/*#1*/
					uuidSize = FileUtils.writeString(dataOut, item.getGUID()); //36+ 4 , UUID size total 40
					entrySize = uuidSize + 8 + 8;  //56
					headerSize = 4+ entrySize * t.getEntries().size();
					currentPosition  =headerSize;
				}
				else
					uuidSize = FileUtils.writeString(dataOut, item.getGUID()); //36+ 4 , UUID size total 40

				/*#2*/
				item.setPosition(  currentPosition );
				dataOut.write(ByteArrayHelper.longToBytes(item.getPosition()));

				/*#3*/
				dataOut.write(ByteArrayHelper.longToBytes(item.getSize()));

				//advance pos
				currentPosition +=  item.getSize();


			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
		}

	}

	@Test(dependsOnMethods = { "testWriteFileAcccessTable" })
	public void testReadFileAccessTable() {
		try {
			File fIn = new File("./target/classes/AttachmentServiceTest_testFileAcccessTable.dat");
			FileInputStream fileIn = new FileInputStream(fIn);
			DataInputStream dataIn = new DataInputStream(fileIn);

			int size = dataIn.readInt();
			Assert.assertEquals( size, 2);

			FileAccessTable t = new FileAccessTable();
			for (int i = 0; i < size; i++) {
				FileAccessEntry item = new FileAccessEntry(FileUtils.readString(fileIn));
				item.setPosition(dataIn.readLong());
				item.setSize(dataIn.readLong());

				t.addEntry(item);

			}

			Assert.assertEquals( t.getSize(), 2);
			Assert.assertEquals( t.getEntries().get(0).getSize(), 366 );
			Assert.assertEquals( t.getEntries().get(1).getSize(), 412 );

		} catch ( IOException e) {
			e.printStackTrace();
		}
	}

}
