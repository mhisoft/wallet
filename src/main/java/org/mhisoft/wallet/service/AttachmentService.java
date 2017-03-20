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

package org.mhisoft.wallet.service;

import java.util.logging.Logger;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.mhisoft.common.util.ByteArrayHelper;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.model.FileAccessEntry;
import org.mhisoft.wallet.model.FileAccessTable;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class AttachmentService {

	private static final Logger logger = Logger.getLogger(AttachmentService.class.getName());


	public void write(final String outoutFIleName, final FileAccessTable t) {

		int currentPosition = 0;
		int uuidSize = -1, entrySize, headerSize, posStart;

		File out = null;
		FileOutputStream fileOut = null;
		try {
			//write it out
			out = new File(outoutFIleName);
			fileOut = new FileOutputStream(out);
			DataOutputStream dataOut = new DataOutputStream(fileOut);

			//write the total number of entries first
		   	/*#0*/
			dataOut.writeInt(t.getEntries().size());
			currentPosition = 0;


			//write the FAT
			for (int i = 0; i < t.getEntries().size(); i++) {
				FileAccessEntry item = t.getEntries().get(i);

				if (uuidSize == -1) {
					/*#1*/
					uuidSize = FileUtils.writeString(dataOut, item.getGUID()); //36+ 4 , UUID size total 40
					entrySize = uuidSize + 8 + 8;  //56
					headerSize = 4 + entrySize * t.getEntries().size();
					currentPosition = headerSize;
				} else
					uuidSize = FileUtils.writeString(dataOut, item.getGUID()); //36+ 4 , UUID size total 40

				/*#2*/
				item.setPosition(currentPosition);
				dataOut.write(ByteArrayHelper.longToBytes(item.getPosition()));

				/*#3*/
				dataOut.write(ByteArrayHelper.longToBytes(item.getSize()));

				//advance pos
				currentPosition += item.getSize();
			}
			fileOut.flush();
			fileOut.close();

			writeFileContents(outoutFIleName, t);


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOut != null)
				try {
					fileOut.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
		}

	}


	private void writeFileContents(final String outoutFIleName, final FileAccessTable t) throws IOException {
		//now write the file contents

		RandomAccessFile fileStore = new RandomAccessFile(outoutFIleName, "rw");
		//FileOutputStream fileStore = new FileOutputStream(outoutFIleName);

		for (int i = 0; i < t.getEntries().size(); i++) {
			FileAccessEntry item = t.getEntries().get(i);
			FileInputStream fin = new FileInputStream(item.getFile());

			// moves file pointer to position specified
			fileStore.seek(item.getPosition());
			// writing String to RandomAccessFile
			byte[] bytes = new byte[4096];

			int nRead, totalwrite = 0;

			while ((nRead = fin.read(bytes, 0, bytes.length)) != -1) {
				fileStore.write(bytes);
				totalwrite += nRead;
			}


			logger.info("wrote total " + totalwrite + " bytes for file:" + item.getFile().getName());
			if (totalwrite != item.getSize())
				throw new RuntimeException("Didn't write the full content, size expected to write " + item.getSize()
						+ "actual write bytes:" + totalwrite);


		}

		fileStore.close();
	}

	public byte[] read(String fileStoreDataFile, FileAccessEntry item) {
		try {
			RandomAccessFile fileStore = new RandomAccessFile(fileStoreDataFile, "rw");
			fileStore.seek(item.getPosition());
			byte[] bytes = new byte[Long.valueOf(item.getSize()).intValue()];
			fileStore.readFully(bytes);
			return bytes;


		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
