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

import java.util.ArrayList;
import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.AlgorithmParameters;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.Serializer;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class DataServiceImplv10 extends AbstractDataService {

	private static int FIXED_RECORD_LENGTH =2000;
	private static int DATA_VERSION =10; //

	public int getVersion() {
		return DATA_VERSION;
	}


	protected FileContentHeader readHeader(FileContentHeader header, FileInputStream fileIN, DataInputStream dataIn  )
			throws IOException {


		int version;

		try {
			version = dataIn.readInt();
		} catch (Exception e) {
			// no version in this file.
			//old version
			version =10;
			FileChannel fc = fileIN.getChannel();
			fc.position(0);// set the file pointer to byte position 0;
		}

		if (version<0 || version>50) {
			//wrong integer
			version =10;
			FileChannel fc = fileIN.getChannel();
			fc.position(0);// set the file pointer to byte position 0;

		}

		//header.setVersion(version);
		header.setPassHash(readString(fileIN));
		header.setNumberOfItems(FileUtils.readInt(fileIN));
		//header.setItemSize(dataIn.readInt());

		return header;
	}

	//   need Encryptor to be intialized first.
	@Override
	public FileContent readFromFile(final String filename, final Encryptor encryptor) {
		//ByteArrayInputStream input = null;
		//byte[] readBuf = new byte[DELIMITER_bytes.length];
		FileContent ret  = new FileContent();
		List<WalletItem> walletItems = new ArrayList<>();
		ret.setWalletItems(walletItems);

		Serializer<WalletItem> serializer  = new Serializer<WalletItem>();
		int readBytes = 0;
		try {
			//Encryptor encryptor = Encryptor.createInstance("testit&(9938447");


			//don't read the whole file
			//byte[] bytesWholeFile = FileUtils.readFile(filename);
			FileInputStream fileIn = new FileInputStream( new File(filename));
			DataInputStream dataIn = new DataInputStream(fileIn);

			readHeader(ret.getHeader(), fileIn, dataIn);


			int k = 0;
			while (k < ret.getHeader().getNumberOfItems()) {

                /*#3: ciperParameters size 4 bytes*/
				int cipherParametersLength  = FileUtils.readInt(fileIn);

			    /*#4: cipherParameters body*/
				byte[] _byteCiper = new byte[cipherParametersLength];
				readBytes = fileIn.read(_byteCiper);
				if (readBytes!=cipherParametersLength)
					throw new RuntimeException("read " + readBytes +" bytes only, expected to read:"+ _byteCiper);

				AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(Encryptor.ALGORITHM);
				algorithmParameters.init(_byteCiper);

				/*#5: item body*/
				int objectSize =FIXED_RECORD_LENGTH;
				byte[] _byteItem = new byte[objectSize];
				readBytes = fileIn.read(_byteItem);
				if(readBytes==objectSize) {
					_byteItem = FileUtils.trimByteArray(_byteItem);
					byte[] byteItem = encryptor.decrypt(_byteItem, algorithmParameters);
					WalletItem item = serializer.deserialize(byteItem);
					walletItems.add(item);
					k++;
				}
				else {
					throw new RuntimeException("read " + readBytes +" bytes only, expected  objectSize:"+ objectSize);
				}

			}
		} catch (Exception e) {
			//end
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred in readFromFile()", e.getMessage());
		}
		ServiceRegistry.instance.getWalletModel().setModified(false);
		return ret;


	}


	@Override
	public void saveToFile(final String filename, final WalletModel model) {
		FileOutputStream stream = null;
		try {

			//final WalletModel model = ServiceRegistry.instance.getWalletModel();

			stream = new FileOutputStream(filename);
			DataOutputStream dataOut = new DataOutputStream(stream);

			Serializer<WalletItem> serializer  = new Serializer<WalletItem>();

			//dataOut.writeInt(DATA_VERSION);
			writeString( dataOut, model.getPassHash() );
			dataOut.write(FileUtils.intToByteArray(model.getItemsFlatList().size()));

			int i=0;
			byte[] cipherParameters;
			for (WalletItem item : model.getItemsFlatList()) {
				byte[] _byteItem = serializer.serialize(item);
				byte[] enc = Encryptor.getInstance().encrypt(_byteItem);
				cipherParameters = Encryptor.getInstance().getCipherParameters();
				/*#3: cipherParameters size 4 bytes*/
				dataOut.write(FileUtils.intToByteArray(cipherParameters.length));

				/*#4: cipherParameters body*/
				dataOut.write(cipherParameters);

				byte[] byteItem = FileUtils.padByteArray(enc, FIXED_RECORD_LENGTH);

				/*#5: item body*/
				//write the object byte stream
				dataOut.write(byteItem);
				i++;
			}

		} catch ( IOException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred when saveToFile()", e.getMessage());

		} finally {
			if (stream!=null)
				try {
					stream.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
		}
	}

}
