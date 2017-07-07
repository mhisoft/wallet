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
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.AlgorithmParameters;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.model.FileAccessEntry;
import org.mhisoft.wallet.model.FileAccessFlag;
import org.mhisoft.wallet.model.FileAccessTable;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class AttachmentService {

	private static final Logger logger = Logger.getLogger(AttachmentService.class.getName());


	public String getAttachmentFileName(String walletFileName) {
		String[] parts = FileUtils.splitFileParts(walletFileName);
		String attachmentFileName = parts[0] + File.separator + parts[1] + "_attachments." + parts[2];
		return attachmentFileName;
	}


	public void saveAttachments(final String filename, final WalletModel model, final Encryptor encryptor) {
		//iterate the model item's FileAccessEntry
		File f = new File(filename);
		if (!f.exists()) {
			newAttachmentStore(filename, model, encryptor);
		} else
			updateAttachmentStore(filename, model, encryptor);

	}


	public  void newAttachmentStore(final String filename, final WalletModel model, final Encryptor encryptor) {

		FileAccessTable t = new FileAccessTable();
		for (WalletItem item : model.getItemsFlatList()) {
			if (item.getAttachmentEntry() != null && item.getAttachmentEntry().getFileName() != null)
				t.addEntry(item.getAttachmentEntry());
		}

		DataOutputStream dataOut = null;
		if (t.getSize() > 0) {
			//new store
			//write it out
			try {
				dataOut = new DataOutputStream(new FileOutputStream(new File(filename)));

				//write the total number of entries first
				/*#0*/
				dataOut.writeInt(t.getEntries().size());
				//itemStartPos = 4;

				writeNewEntries(4, dataOut, t, encryptor);

				dataOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
				DialogUtils.getInstance().error("Error writing attachment entries.", e.getMessage());
			} finally {
				if (dataOut != null)
					try {
						dataOut.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
			}
		}
	}


	public void updateAttachmentStore(final String filename, final WalletModel model, final Encryptor encryptor) {


		FileAccessTable t = new FileAccessTable();
		for (WalletItem item : model.getItemsFlatList()) {

			if (item.getAttachmentEntry() != null &&
					(item.getAttachmentEntry().getAccessFlag()==FileAccessFlag.Create
							|| item.getAttachmentEntry().getAccessFlag()==FileAccessFlag.Update) )  {

			if ( item.getNewAttachmentEntry()!=null && item.getNewAttachmentEntry().getFile() != null )
				  t.addEntry(item.getNewAttachmentEntry());
			else if (item.getAttachmentEntry() != null && item.getAttachmentEntry().getFile() != null )
				t.addEntry(item.getAttachmentEntry());
			}
		}

		RandomAccessFile attachmentFileStore = null;
		if (t.getSize() > 0) {
			try {
				attachmentFileStore= new RandomAccessFile(filename, "rw");

				//write the total number of entries first
				int existingNumEntries = attachmentFileStore.readInt();

				//add to be appended ones
				existingNumEntries += t.getSize();
				attachmentFileStore.seek(0);
                attachmentFileStore.writeInt(existingNumEntries);

				//seek to the end
				long itemStartPos =  attachmentFileStore.length();
				attachmentFileStore.seek(itemStartPos);

				//append new entries to the end of the store.
				writeNewEntries(itemStartPos, attachmentFileStore, t, encryptor);

				//mark the deleted entries in the data store
				//todo DELETE access flag





			} catch (IOException e) {
				e.printStackTrace();
				DialogUtils.getInstance().error("Error writing attachment entries.", e.getMessage());
			} finally {
				if (attachmentFileStore != null)
					try {
						attachmentFileStore.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
			}
		}

		//now clear the access flag on the item
		for (WalletItem item : model.getItemsFlatList()) {
			if (item.getAttachmentEntry() != null && item.getAttachmentEntry().getFile() != null  //
					&& item.getAttachmentEntry().getAccessFlag()!=null ) {
				item.getAttachmentEntry().setAccessFlag(null);
			}
		}




	}


	public void compactAttachmentStore(final String filename) {
		//todo if delete and updated entries in the store is more than 30%, compact the store.
	}



	/*
		 total numbers of entries (int)
	     entry[0] :

			 1. UUID: 40          --------> start pos here
			 2 position (long) : 8

			 3. cipherParam size : int, 4 bytes
			 4. cipherParameters body : var size

			 5. size of the content: int, 4 bytes
			 6.encrypted contents: var size

			   			--------> next entry start pos here



	 */


	public void writeNewEntries(final long itemStartPos, DataOutput dataOut, final FileAccessTable t, final Encryptor encryptor) throws IOException {

		long pos = itemStartPos;


		//write it out
		//dataOut = new DataOutputStream(new FileOutputStream(new File(outoutFIleName)));

		//write the total number of entries first
		   	/*#0*/
		//dataOut.writeInt(t.getEntries().size());
		//itemStartPos = 4;


		//todo handle CREATE, UPDATE and DELETE.

		//write each entry
		for (int i = 0; i < t.getEntries().size(); i++) {


			FileAccessEntry fileAccessEntry = t.getEntries().get(i);

			//todo the one is to be deleted. we are appending the new and updated entries to the end.
			//deletion needs compact the store.

			if (fileAccessEntry.getAccessFlag()== FileAccessFlag.Delete) {
				/*todo save the  accessflag, mark for deletion later*/


				continue;
			}

			logger.fine("Write entry " + fileAccessEntry.getGUID() + "-" + fileAccessEntry.getFileName());
			logger.fine("\t " + fileAccessEntry.getAccessFlag());
			logger.fine("\t start pos:" + pos);

			fileAccessEntry.setPosition(pos);

			/* #1  UUID*/
			int uuidSize = FileUtils.writeString(dataOut, fileAccessEntry.getGUID());
			pos += 40;

		    /* #1a  accessflag */
			dataOut.writeInt(0);
			pos+=4;


			/* #2 header:pos of the attachment content*/
			dataOut.writeLong(fileAccessEntry.getPosition());
			pos += 8;


			byte[] fileContent = FileUtils.readFile(fileAccessEntry.getFile());
			Encryptor.EncryptionResult ret = encryptor.encrypt(fileContent);
			byte[] encrypted = ret.getEncryptedData();
			logger.fine("\t fileContent size:" + fileContent.length);
			logger.fine("\t encrypted size:" + encrypted.length);

			/*#3: cipherParameters size 4 bytes*/
			//have to write for each encryption because a random salt is used.
			byte[] cipherParameters = ret.getCipherParameters();
			//byte[] _intToBytes =  ByteArrayHelper.intToBytes(cipherParameters.length);
			dataOut.writeInt(cipherParameters.length); //length is 100 bytes
			pos += 4;

			/*#4: cipherParameters body*/
			dataOut.write(cipherParameters);
			pos += cipherParameters.length;


			/* #5 header:size of the attachment content*/
			//_intToBytes = ByteArrayHelper.intToBytes(encrypted.length);
			dataOut.writeInt(encrypted.length);
			pos += 4;


			/* content */
			dataOut.write(encrypted);
			pos += encrypted.length;

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


	public FileAccessTable read(String dataFile, final Encryptor encryptor) {
		FileAccessTable t = null;
		try {
			File fIn = new File(dataFile);
			if (!fIn.exists()) {
				return t;
			}

			RandomAccessFile fileIn = new RandomAccessFile(dataFile, "rw");
			int numberOfEntries = fileIn.readInt();
			t = new FileAccessTable();
			int pos = 4;
			int readBytes = 0;
			for (int i = 0; i < numberOfEntries; i++) {

				fileIn.seek(pos);

				/* UUIID and position */
				String UUID = FileUtils.readString(fileIn);
				FileAccessEntry fileAccessEntry = new FileAccessEntry(UUID);
				pos += 40;

				/* #1a  accessflag */
				fileAccessEntry.setAccessFlag(  FileAccessFlag.values[fileIn.readInt()] );
				pos += 4;

				/* #2 pos */
				fileAccessEntry.setPosition(fileIn.readLong());
				pos +=  8;

				/*#3: ciperParameters size 4 bytes*/
				int cipherParametersLength = fileIn.readInt();
				pos += 4;

			    /*#4: cipherParameters body*/
				byte[] _byteCiper = new byte[cipherParametersLength];
				readBytes = fileIn.read(_byteCiper);
				if (readBytes != cipherParametersLength)
					throw new RuntimeException("read " + readBytes + " bytes only, expected to read:" + _byteCiper);
				pos += _byteCiper.length;

				AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(Encryptor.ALGORITHM);
				algorithmParameters.init(_byteCiper);
				fileAccessEntry.setAlgorithmParameters(algorithmParameters);

				/*#5  size of the content (int): 4 bytes */
				int encSize = fileIn.readInt();
				pos += 4;
				fileAccessEntry.setPosOfContent(pos);
				fileAccessEntry.setEncSize(encSize);

				t.addEntry(fileAccessEntry);

				/* #6 file content */
				pos += encSize;


				/* delay read it on demand
				byte[] _encBytes =readFileContent(dataFile, pos, encSize ) ;
				byte[] fileContent = encryptor.decrypt(_encBytes, algorithmParameters);
				pos +=  fileContent.length;

				fileAccessEntry.setFileContent(fileContent);
				fileAccessEntry.setSize(fileContent.length); //decrypted size.
				*/

			}

			fileIn.close();

		} catch (Exception e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred in reading attachments.", e.getMessage());
		}


		return t;
	}

	/**
	 * Read and decrypt the file content based on the mark oin the entry.
	 * The fileAccessEntry file content and size will be set.
	 *
	 * @param fileStoreDataFile
	 * @param entry
	 * @param encryptor
	 * @return
	 */
	public byte[] readFileContent(String fileStoreDataFile, FileAccessEntry entry, Encryptor encryptor) {
		try {
			RandomAccessFile fileStore = new RandomAccessFile(fileStoreDataFile, "rw");
			fileStore.seek(entry.getPosOfContent());
			byte[] _encedBytes = new byte[entry.getEncSize()];
			fileStore.readFully(_encedBytes);
			byte[] fileContent = encryptor.decrypt(_encedBytes, entry.getAlgorithmParameters());

			entry.setFileContent(fileContent);
			entry.setSize(fileContent.length); //decrypted size.


			fileStore.close();
			return fileContent;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
