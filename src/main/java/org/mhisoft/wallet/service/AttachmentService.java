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

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.model.FileAccessEntry;
import org.mhisoft.wallet.model.FileAccessTable;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class AttachmentService {

	private static final Logger logger = Logger.getLogger(AttachmentService.class.getName());




	public void createNewDataStore(final String outoutFIleName, final FileAccessTable t) {

	}


	public void addNewFileToDataStore(DataOutputStream dataOut, FileAccessEntry item ) {

	}


	public void saveAttachments(WalletModel model) {
		//iterate the model item's FileAccessEntry


	}





	/**
	 *
	 * @param outoutFIleName
	 * @param t
	 */
	public void write(final String outoutFIleName, final FileAccessTable t) {

		int itemStartPos = 0;
		int uuidSize = -1, entrySize, headerSize, posStart;

		File out = null;
		//FileOutputStream fileOut = null;
		DataOutputStream dataOut=null;
		try {
			//write it out
			dataOut = new DataOutputStream(new FileOutputStream(new File(outoutFIleName)));

			//write the total number of entries first
		   	/*#0*/
			dataOut.writeInt(t.getEntries().size());
			itemStartPos = 4 ;


			//write the FAT
			for (int i = 0; i < t.getEntries().size(); i++) {
				FileAccessEntry item = t.getEntries().get(i);

				itemStartPos += FileAccessEntry.getHeaderBytes();
				item.setPosition(itemStartPos);


				/*#1 UUID*/
				uuidSize = FileUtils.writeString(dataOut, item.getGUID());


				/*#2 pos of the attachment content*/
				dataOut.writeLong(item.getPosition());


				/*#3 size of the attachment content*/
				dataOut.writeLong(item.getSize());


				/* write the file data */
				writeFileData (item, dataOut);

				//advance pos
				itemStartPos += item.getSize();
			}
			dataOut.flush();
			dataOut.close();



		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataOut != null)
				try {
					dataOut.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
		}

	}


	private int writeFileData(FileAccessEntry item, DataOutputStream dataOut) throws IOException {
		byte[] bytes = new byte[4096];

		int nRead, totalwrite = 0;

		FileInputStream fin = new FileInputStream(item.getFile());

		while ((nRead = fin.read(bytes, 0, bytes.length)) != -1) {
			dataOut.write(bytes, 0, nRead);
			totalwrite += nRead;
		}


		logger.info("wrote total " + totalwrite + " bytes for file:" + item.getFile().getName());
		if (totalwrite != item.getSize())
			throw new RuntimeException("Didn't write the full content, size expected to write " + item.getSize()
					+ "actual write bytes:" + totalwrite);

		return totalwrite;



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



	public FileAccessTable read(String dataFile) {
		FileAccessTable t =null;
		try {
			File fIn = new File(dataFile);
			//FileInputStream fileIn = new FileInputStream(fIn);
			//DataInputStream dataIn = new DataInputStream(fileIn);

			RandomAccessFile raFile = new RandomAccessFile(dataFile, "rw");


			int size = raFile.readInt();

			t = new FileAccessTable();

			int pos = 4;

			for (int i = 0; i < size; i++) {

				raFile.seek(pos);

				String UUID = FileUtils.readString(raFile);
				FileAccessEntry item = new FileAccessEntry(UUID);
				item.setPosition(raFile.readLong());
				item.setSize(raFile.readLong());

				t.addEntry(item);

				//byte[] bytes =readFileContent(dataFile, item) ;

				//advance to the next item pos , header start
				pos+= FileAccessEntry.getHeaderBytes() + item.getSize();


			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}

	public byte[] readFileContent(String fileStoreDataFile, FileAccessEntry item) {
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
