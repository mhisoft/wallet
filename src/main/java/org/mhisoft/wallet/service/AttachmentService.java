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
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import org.apache.commons.io.IOUtils;
import org.mhisoft.common.util.CompressionUtil;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.StringUtils;
import org.mhisoft.common.util.security.PBEEncryptor;
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

	static DecimalFormat intDF = new DecimalFormat("###,###");


	public String getAttachmentFileName(String walletFileName) {
		String[] parts = FileUtils.splitFileParts(walletFileName);
		String attachmentFileName = parts[0] + File.separator + parts[1] + "_attachments." + parts[2];
		return attachmentFileName;
	}


	public void saveAttachments(final String filename, final WalletModel model, final PBEEncryptor encryptor) {
		//iterate the model item's FileAccessEntry
		boolean deleted = false;
		File f = new File(filename);
		if (!f.exists()) {
			/* create new store */
			newAttachmentStore(filename, model, encryptor);
		} else {

			FileAccessTable t = new FileAccessTable();
			double deleteCount = 0, totalCount = 0;
			for (WalletItem item : model.getItemsFlatList()) {
				if (item.getAttachmentEntry() != null) {
					totalCount++;

					if (item.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Delete
							|| item.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Update)
						//these are new deleted attachments
						deleteCount++;

				}
			}

			//delete count need to add the orphan records (marked as DELETE) in the attachment store.
			deleteCount += model.getDeletedEntriesInStore();

			if (deleteCount == totalCount) {
				//remove it.
				deleteAttachmentStore(filename);
				deleted = true;
			} else if (deleteCount / totalCount > 0.3) {
				/* transfer to a new store */
				logger.fine("\n");
				logger.fine("compactAttachmentStore");
				compactAttachmentStore(filename, model, encryptor);
			} else {
				/* append to existing store */
				logger.fine("\n");
				logger.fine("appendAttachmentStore");
				appendAttachmentStore(filename, model, encryptor);
			}
		}


		FileAccessTable t = null;
		if (!deleted) {
			logger.fine("file size: " + intDF.format(new File(filename).length()));
			//Refresh / Reload the wallet item file access entries after save.
			t = read(filename, encryptor);
			if (t!=null)
				model.setDeletedEntriesInStore(t.getDeletedEntries());

		}


		//reset the item attachment entries.
		//drive from item.
		for (WalletItem item : model.getItemsFlatList()) {
			item.setAttachmentEntry(t == null ? null : t.getEntry(item.getSysGUID()));
			if (item.getAttachmentEntry() != null)
				item.getAttachmentEntry().setAccessFlag(FileAccessFlag.None);
			item.setNewAttachmentEntry(null);
		}


	}


	/**
	 * Read the attachments from the old attachment store and write to the new attachment store.
	 * It is really copy attachments from one store to another.
	 * use case: export, change password;
	 *     store version change
	 *
	 *     DELETED	entries are not transfered.
	 *
	 * @param oldStoreName      The old store name, needed to read the attachment content out.
	 * @param model             The old model, need the encryptor from it to read old attachment for transfer.
	 * @param expModel          the new store model, items list is from the new model.
	 * @param expModelEncryptor The encryptor for writing the new attachment store.
	 * @param resetModelAttachmentEntries  do not reset for upgrade scenario.
	 */

	public boolean transferAttachmentStore(final String oldStoreName, String newStoreName
			, final WalletModel model
			, final WalletModel expModel
			, final PBEEncryptor expModelEncryptor
			, final boolean resetModelAttachmentEntries) {

		logger.fine("transferAttachmentStore()");

		File newFile = new File(newStoreName);
		if (newFile.exists()) {
			if (!newFile.delete()) {
				DialogUtils.getInstance().error("Can't delete the tmp file:" + newStoreName);
				return false;
			}
		}

		FileAccessTable t = new FileAccessTable();
		double deleteCount = 0, totalCount = 0;
		for (WalletItem item : expModel.getItemsFlatList()) {
			if (item.getAttachmentEntry() != null) {
				totalCount++;

				if (item.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Delete
						|| item.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Update)
					//these are new deleted attachments
					deleteCount++;
				else if ( item.getAttachmentEntry().getFileName() != null)
					t.addEntry(item.getAttachmentEntry());

			}

		}

		//delete count need to add the orphan records (marked as DELETE) in the attachment store.
		deleteCount += expModel.getDeletedEntriesInStore();

		if (deleteCount == totalCount) {
			//all deleted or updated.
			//nothing new need to be transfered.
			return false;
		}



		DataOutputStream dataOut = null;
		if (t.getSize() > 0) {
			//new store
			//write it out
			try {
				dataOut = new DataOutputStream(new FileOutputStream(new File(newStoreName)));

				//write the total number of entries first
				/*#0*/
				dataOut.writeInt(t.getEntries().size());
				//itemStartPos = 4;

				writeFileEntries(expModel, true, oldStoreName, 4, dataOut, t, model.getEncryptorForRead(), expModelEncryptor);

				dataOut.flush();
				dataOut.close();


			} catch (IOException e) {
				e.printStackTrace();
				DialogUtils.getInstance().error("transferAttachmentStore failed:", e.getMessage());
			} finally {
				if (dataOut != null)
					try {
						dataOut.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
			}


		}

		if (resetModelAttachmentEntries) {
			//now clear the access flag on the item
			for (WalletItem item : model.getItemsFlatList()) {
				if (item.getAttachmentEntry() != null && item.getAttachmentEntry().getFile() != null  //
						&& item.getAttachmentEntry().getAccessFlag() != null) {
					item.getAttachmentEntry().setAccessFlag(FileAccessFlag.None);
				}
			}
		}

		return t.getSize()>0;


	}


	/**
	 * Create a new attachment store.
	 *
	 * @param filename
	 * @param model
	 * @param encryptor
	 */
	public void newAttachmentStore(final String filename, final WalletModel model, final PBEEncryptor encryptor) {

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

				writeFileEntries(model, false, null, 4, dataOut, t, model.getEncryptorForRead(), encryptor);

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


			//now clear the access flag on the item
			for (WalletItem item : model.getItemsFlatList()) {
				if (item.getAttachmentEntry() != null && item.getAttachmentEntry().getFile() != null  //
						&& item.getAttachmentEntry().getAccessFlag() != null) {
					item.getAttachmentEntry().setAccessFlag(FileAccessFlag.None);
				}
			}

		}
	}


	/**
	 * Append to the existing store for Merged and Created/Updated attachments.
	 * @param filename
	 * @param model
	 * @param encryptor
	 */
	protected void appendAttachmentStore(final String filename, final WalletModel model, final PBEEncryptor encryptor) {

		FileAccessTable t = new FileAccessTable();
		for (WalletItem item : model.getItemsFlatList()) {
			if (item.getAttachmentEntry() == null)
				continue;
			if (item.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Merge) {
				t.addEntry(item.getAttachmentEntry());
			} else if (FileAccessFlag.Create == item.getAttachmentEntry().getAccessFlag()
					|| FileAccessFlag.Update == item.getAttachmentEntry().getAccessFlag()) {

				if (item.getNewAttachmentEntry() != null && item.getNewAttachmentEntry().getFile() != null) {
					t.addEntry(item.getNewAttachmentEntry());
				} else if (item.getAttachmentEntry().getFile() != null) {
					t.addEntry(item.getAttachmentEntry());
				}
			}
		}


		RandomAccessFile attachmentFileStore = null;
		try {
			attachmentFileStore = new RandomAccessFile(filename, "rw");


			if (t.getSize() > 0) {

				//write the total number of entries first
				int entriesCount = attachmentFileStore.readInt();

				//add to be appended ones
				entriesCount += t.getSize();
				attachmentFileStore.seek(0);
				attachmentFileStore.writeInt(entriesCount);

				//seek to the end
				long itemStartPos = attachmentFileStore.length();
				attachmentFileStore.seek(itemStartPos);

				//append new entries to the end of the store.
				writeFileEntries(model, false, filename, itemStartPos, attachmentFileStore, t, model.getEncryptorForRead(), encryptor);


			}


			/*marked the deleted entries **/
			for (WalletItem item : model.getItemsFlatList()) {
				if (item.getAttachmentEntry() == null)
					continue;
				if (FileAccessFlag.Delete == item.getAttachmentEntry().getAccessFlag() //the attachment is deleted
						//the entry had the content saved in the store, now it is replaced. the new content is appended to the end of the file.
						// the file entry at the old position needs to be marked as DELETE.
						|| (FileAccessFlag.Update == item.getAttachmentEntry().getAccessFlag()
						&& item.getAttachmentEntry().getEncSize() > 0)
						&& item.getAttachmentEntry().getPosition() > 0) {

					attachmentFileStore.seek(item.getAttachmentEntry().getPosition() + 40);
					attachmentFileStore.writeInt(FileAccessFlag.Delete.ordinal());
				}
			}

			attachmentFileStore.close();
			attachmentFileStore = null;


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


	} //appendAttachmentStore


	protected void deleteAttachmentStore(final String oldStorefName) {
		new File(oldStorefName).delete();
	}


	/**
	 * abandon the old store and transfer everthing to the wnew store.
	 * The same model for both old and new store.
	 *
	 * @param oldStorefName
	 * @param model
	 * @param encryptor
	 */
	protected void compactAttachmentStore(final String oldStorefName, final WalletModel model, final PBEEncryptor encryptor) {
		String newStoreName = oldStorefName + ".tmp";
		File newFile = new File(newStoreName);
		if (newFile.exists()) {
			if (!newFile.delete()) {
				DialogUtils.getInstance().error("Can't delete the tmp file:" + newStoreName);
			}
		}

		FileAccessTable t = new FileAccessTable();
		for (WalletItem item : model.getItemsFlatList()) {
			if (item.getAttachmentEntry() == null)
				continue;

			if (item.getAttachmentEntry().getAccessFlag() == FileAccessFlag.None) {
				//no change , need to transfer to the new file.
				t.addEntry(item.getAttachmentEntry());
			} else if (item.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Merge) {
				t.addEntry(item.getAttachmentEntry());
			} else if (FileAccessFlag.Create == item.getAttachmentEntry().getAccessFlag()
					|| FileAccessFlag.Update == item.getAttachmentEntry().getAccessFlag()) {
				if (item.getAttachmentEntry() != null) {
					if (item.getAttachmentEntry().getNewEntry() != null)
						t.addEntry(item.getNewAttachmentEntry());
					else
						item.getAttachmentEntry();
				}
			}
		}


		RandomAccessFile attachmentFileStore = null;
		if (t.getSize() > 0) {
			try {
				attachmentFileStore = new RandomAccessFile(newStoreName, "rw");
				attachmentFileStore.seek(0);
				attachmentFileStore.writeInt(t.getSize());

				writeFileEntries(model, true, oldStorefName, 4, attachmentFileStore, t, model.getEncryptorForRead(), encryptor);


				attachmentFileStore.close();
				attachmentFileStore = null;


				//now do the swap of the store to the new one.
				new File(oldStorefName).delete();
				newFile.renameTo(new File(oldStorefName));


			} catch (IOException e) {
				e.printStackTrace();
				DialogUtils.getInstance().error("compactAttachmentStore() failed", e.getMessage());
			} finally {
				if (attachmentFileStore != null)
					try {
						attachmentFileStore.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
			}
		} else {
			//need to handle all images are deleted
			//just remove the old store.
			new File(oldStorefName).delete();
		}


	}



	/*
		 total numbers of entries (int)
	     entry[0] :

			  0:   	UUID: 40          --------> start pos here
			 40:   	access flag : 4
			 44:   	position (long) : 8
			 52:   	cipherParam size : int, 4 bytes
			 56:   	cipherParameters body : var size C
			       	filename (without path) encrypted string F
		       	   	size of the content: int, 4 bytes
	       			encrypted contents: var size

			   			--------> next entry start pos here



	 */


	/**
	 * @param transferStoreMode a new store will be created because the old one has t
	 * @param oldStoreFileName  provide along with the transferStoreMode. for read the contents to be transfered to the new store  when doing compacting.
	 * @param itemStartPos      start position for the data output file.
	 * @param dataOut           data output file/stream
	 * @param t                 the  FileAccessTable contains the entries to write.
	 * @param encryptor         The encryptor
	 * @throws IOException
	 */
	protected void writeFileEntries(WalletModel model,
			boolean transferStoreMode
			, String oldStoreFileName,
			final long itemStartPos, DataOutput dataOut, final FileAccessTable t
			, final PBEEncryptor oldEncryptorForRead
			, final PBEEncryptor encryptor) throws IOException {

		long pos = itemStartPos;


		//write it out
		//dataOut = new DataOutputStream(new FileOutputStream(new File(outoutFIleName)));

		//write the total number of entries first
		/*#0*/
		//dataOut.writeInt(t.getEntries().size());
		//itemStartPos = 4;
		String impAttachmentStoreName = null;
		if (model.getImpModel() != null)
			impAttachmentStoreName = getAttachmentFileName(model.getImpModel().getVaultFileName());


		//write each entry
		for (int i = 0; i < t.getEntries().size(); i++) {


			FileAccessEntry fileAccessEntry = t.getEntries().get(i);

			//not handleing the deleted entries.
			if (fileAccessEntry.getAccessFlag() == FileAccessFlag.Delete) {
				continue;
			}

			logger.fine("------------------");
			logger.fine("Write entry: " + fileAccessEntry.getGUID() + "-" + fileAccessEntry.getFileName());
			logger.fine("\t access flag: " + fileAccessEntry.getAccessFlag());
			logger.fine("\t start pos: " + pos);

			fileAccessEntry.setPosition(pos);


			/*  UUID*/
			int uuidSize = FileUtils.writeString(dataOut, fileAccessEntry.getGUID());
			pos += 40;

		    /*  accessflag */
			dataOut.writeInt(0);
			pos += 4;


			/* position */
			dataOut.writeLong(fileAccessEntry.getPosition());
			pos += 8;



			/* write filename encrypted */
			String strFName = FileUtils.getFileNameWithoutPath(fileAccessEntry.getFileName());
			logger.fine("\t write file name: " + strFName);
			byte[] _byteFileName = StringUtils.getBytes(strFName);
			pos = writeEncryptedContent(_byteFileName, encryptor, dataOut, pos);
			logger.fine("\t pos: " + pos);

			/* Attachment body */
			byte[] attachmentBytes;

			if (fileAccessEntry.getAccessFlag() == FileAccessFlag.Merge && fileAccessEntry.getEncSize() > 0) {
				if (impAttachmentStoreName == null)
					throw new IOException("impAttachmentStoreName is not set.");
				//depends on the import model version, it might or might not be compressed
				//test case: import an old version data file with attachments.
				byte[] _impBytes = readCompressedFileContent(  model.getImpModel().getCurrentDataFileVersion(),
						impAttachmentStoreName, fileAccessEntry, model.getImpModel().getEncryptor());
				if  (model.getImpModel().getCurrentDataFileVersion()>=WalletModel.LATEST_DATA_VERSION) {
					//it is compressed.
					attachmentBytes = _impBytes;
				}
				else {
					//not compressed, need to compress it.
					attachmentBytes = CompressionUtil.getCompressedBytes(new ByteArrayInputStream(_impBytes));
				}

			}
			else if (transferStoreMode && fileAccessEntry.getAccessFlag() == FileAccessFlag.None && fileAccessEntry.getEncSize() > 0) {
				// no change, this is an transfer to the new store. need to read the filecontent from the old store.
				// could be upgrade scenario as well.
				// should be able to skip the compression-decompression process.
				byte[] _bytes =readCompressedFileContent( model.getCurrentDataFileVersion(),
						oldStoreFileName, fileAccessEntry, oldEncryptorForRead);

				if  (model.getCurrentDataFileVersion()<WalletModel.LATEST_DATA_VERSION) {
					//it was not compressed.
					attachmentBytes = CompressionUtil.getCompressedBytes(new ByteArrayInputStream(_bytes));
					logger.fine("\t transfer from old store. compress contennt. before "+ _bytes.length +", after:" + attachmentBytes.length);
				}
				else
					attachmentBytes = _bytes;

			} else {
				/* for CREATE OR UPDATE or a new store */
				//read from file
				//since v14 , start to compress the contents.
				File f = fileAccessEntry.getFile();
				FileInputStream sourceInputStream = new FileInputStream(f);
				if (model.getCurrentDataFileVersion()<WalletModel.LATEST_DATA_VERSION) {
					//for testing, write the old version format. un-compressed
					attachmentBytes = FileUtils.readFile(sourceInputStream);
					logger.fine("\t write entry by reading from file, not compressed: " + fileAccessEntry.getFile()
							+ ", original size:" + fileAccessEntry.getFile().length());
				}
				else {
					attachmentBytes = CompressionUtil.getCompressedBytes(sourceInputStream);
					logger.fine("\t write entry by reading from file: " + fileAccessEntry.getFile()
							+ ", original size:" + fileAccessEntry.getFile().length()
							+ ", compressed size:" + attachmentBytes.length
					);
				}
			}

			//encrypted the compressed bytes and write out.
			pos = writeEncryptedContent(attachmentBytes, encryptor, dataOut, pos);

		}

	}


	/**
	 * Write the file content to the dataout, which is the attachment store.
	 * @param content
	 * @param encryptor
	 * @param dataOut
	 * @param pos
	 * @return
	 * @throws IOException
	 */
	//return pos
	private long writeEncryptedContent(byte[] content, final PBEEncryptor encryptor, DataOutput dataOut, long pos) throws IOException {

		logger.fine("writeEncryptedContent");
		PBEEncryptor.EncryptionResult ret = encryptor.encrypt(content);
		byte[] _byteEncrypted = ret.getEncryptedData();

		/*  cipherParameters size 4 bytes*/
		byte[] cipherParameters = ret.getCipherParameters();
		dataOut.writeInt(cipherParameters.length); //length is 100 bytes
		//logger.fine("\t cipherParameters length: " + cipherParameters.length);
		pos += 4;

		/* cipherParameters body*/
		dataOut.write(cipherParameters);
		pos += cipherParameters.length;

		/*  size of the  content*/
		logger.fine("\t size of the content: " + _byteEncrypted.length);
		dataOut.writeInt(_byteEncrypted.length);
		pos += 4;

		/*   content */
		dataOut.write(_byteEncrypted);
		pos += _byteEncrypted.length;

		return pos;

	}


	class ReadContentVO {
		long pos;
		AlgorithmParameters algorithmParameters;

	}

	private ReadContentVO readCipherParameter(RandomAccessFile fileIn, long pos) throws IOException, NoSuchAlgorithmException {
		ReadContentVO ret = new ReadContentVO();
		ret.pos = pos;

		/*#3: ciperParameters size 4 bytes*/
		int cipherParametersLength = fileIn.readInt();
		ret.pos += 4;
		//logger.fine("read cipherParametersLength:" + cipherParametersLength);

		/*#4: cipherParameters body*/
		byte[] _byteCiper = new byte[cipherParametersLength];
		int readBytes = fileIn.read(_byteCiper);
		if (readBytes != cipherParametersLength)
			throw new RuntimeException("read " + readBytes + " bytes only, expected to read:" + _byteCiper);
		ret.pos += _byteCiper.length;

		ret.algorithmParameters = AlgorithmParameters.getInstance(PBEEncryptor.ALGORITHM);
		ret.algorithmParameters.init(_byteCiper);

		return ret;

	}

	public FileAccessTable read(String dataFile, final PBEEncryptor encryptor) {
		logger.fine("\n\nread attachment file:" + dataFile);
		FileAccessTable t = null;
		RandomAccessFile fileIn=null;
		try {
			File fIn = new File(dataFile);
			if (!fIn.exists()) {
				return t;
			}

			fileIn = new RandomAccessFile(dataFile, "rw");
			int numberOfEntries = fileIn.readInt();
			t = new FileAccessTable();
			long pos = 4;
			int readBytes = 0;
			for (int i = 0; i < numberOfEntries; i++) {

				fileIn.seek(pos);

				/* UUIID and position */
				String UUID = FileUtils.readString(fileIn);
				FileAccessEntry fileAccessEntry = new FileAccessEntry(UUID);
				pos += 40;
				logger.fine("Read entry, UUID:" + UUID);

				/*  accessflag */
				fileAccessEntry.setAccessFlag(FileAccessFlag.values[fileIn.readInt()]);
				pos += 4;
				logger.fine("\t access flag:" + fileAccessEntry.getAccessFlag());

				/*  pos */
				fileAccessEntry.setPosition(fileIn.readLong());
				pos += 8;
				logger.fine("\t position:" + fileAccessEntry.getPosition());



				/* read filename */
				ReadContentVO vo = readCipherParameter(fileIn, pos);
				pos = vo.pos;
				int encSize_FileName = fileIn.readInt();
				pos += 4;
				byte[] _encedBytes = new byte[encSize_FileName];
				fileIn.readFully(_encedBytes);
				pos += encSize_FileName;
				byte[] byte_filename = encryptor.decrypt(_encedBytes, vo.algorithmParameters);
				fileAccessEntry.setFileName(StringUtils.bytesToString(byte_filename));
				logger.fine("\t file name:" + fileAccessEntry.getFileName());

				/* attachment */
				vo = readCipherParameter(fileIn, pos);
				pos = vo.pos;
				fileAccessEntry.setAlgorithmParameters(vo.algorithmParameters);

				/*#5  size of the content (int): 4 bytes */
				int encSize = fileIn.readInt();
				pos += 4;

				fileAccessEntry.setPosOfContent(pos);
				fileAccessEntry.setEncSize(encSize);
				logger.fine("\t encSize:" + encSize);

				if (fileAccessEntry.getAccessFlag() != FileAccessFlag.Delete)
					t.addEntry(fileAccessEntry);
				else {
					logger.fine("\tentries is marked as deleted.");
					t.deletedEntries++;
				}

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
			fileIn=null;

		} catch (Exception e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred in reading attachments.", e.getMessage());
		}
		finally {
			if (fileIn!=null)
				try {
					fileIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}


		DebugUtil.append("Attachment Store total entries:" + t.getSize()
				+ "\n" + "Orphan records :" + t.deletedEntries
		);


		return t;
	}

	protected byte[] readCompressedFileContent(final int storeVersion, String fileStoreDataFile, FileAccessEntry entry, PBEEncryptor encryptor) {
		return _readFileContent(false, storeVersion, fileStoreDataFile, entry, encryptor );
	}


	/**
	 * Read and decrypt the file content based on the mark on the entry.
	 * It is also deflated if was compressed.
	 * The fileAccessEntry file content and size will be set.
	 *
	 * @param fileStoreDataFile
	 * @param entry
	 * @param encryptor
	 * @return
	 */

	public byte[] readFileContent(final int storeVersion, String fileStoreDataFile, FileAccessEntry entry, PBEEncryptor encryptor) {
		return _readFileContent(true, storeVersion, fileStoreDataFile, entry, encryptor );
	}


	private byte[] _readFileContent(boolean decompress,
			final int storeVersion, String fileStoreDataFile, FileAccessEntry entry, PBEEncryptor encryptor) {


		try {
			RandomAccessFile fileStore = new RandomAccessFile(fileStoreDataFile, "rw");
			fileStore.seek(entry.getPosOfContent());
			byte[] _encedBytes = new byte[entry.getEncSize()];
			fileStore.readFully(_encedBytes);
			byte[] fileContent = encryptor.decrypt(_encedBytes, entry.getAlgorithmParameters());

			//after decrypt, deflate the compressed data
			if (decompress && storeVersion>=WalletModel.LATEST_DATA_VERSION) {
				GZIPInputStream decompressedStream = new GZIPInputStream(
								new ByteArrayInputStream(fileContent));
				fileContent = IOUtils.toByteArray(decompressedStream);
			}


			entry.setFileContent(fileContent);
			entry.setSize(fileContent.length); //decrypted size.


			fileStore.close();
			return fileContent;

		} catch (IOException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Can't read " + fileStoreDataFile + ":" + e.toString());
		}
		return null;
	}



	//re-read the attachments from file and refresh the model items.
	public void reloadAttachments(final String vaultFileName, final WalletModel model) {
		String attachmetStoreName = getAttachmentFileName(vaultFileName);
		FileAccessTable t = read(attachmetStoreName, model.getEncryptor());

		// reset first
		for (WalletItem walletItem : model.getItemsFlatList()) {
			walletItem.setAttachmentEntry(null);
			walletItem.setNewAttachmentEntry(null);
		}


		//walk through the  FileAccessTable
		if ( t!=null && t.getEntries()!=null) {
			for (FileAccessEntry fileAccessEntry : t.getEntries()) {

				WalletItem walletItem = model.findItem(fileAccessEntry.getGUID());
				if (walletItem != null) {
					walletItem.setAttachmentEntry(fileAccessEntry);
					walletItem.setNewAttachmentEntry(null);
				}
			}
		}
	}


}
