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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public abstract class AbstractDataService implements  DataService {



	abstract protected FileContentHeader readHeader(FileContentHeader header, FileInputStream fileIN, DataInputStream dataIn  )
			throws IOException ;

	abstract protected void saveHeader(DataOutputStream dataOut, final WalletModel model) throws IOException ;


	//read the first int
	//if the header stores it, file pos will advanced a int.
	//for the old 10 version, where there is no int stored inthe file, pos remains at zero.
	protected int readVersion(FileInputStream fileIN, DataInputStream dataIn  )
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

		return version;
	}

	/**
	 * Read the file header info and close it.
	 * @param filename
	 * @return
	 */
	@Override
	public  FileContentHeader readHeader(final String filename, boolean closeAfterRead)  throws IOException {
		FileInputStream fileIN =null;
		FileContentHeader header = new FileContentHeader();
		try {

			//don't read the whole file
			//byte[] bytesWholeFile = FileUtils.readFile(filename);
			fileIN = new FileInputStream(new File(filename));
			DataInputStream dataIn = new DataInputStream(fileIN);
			readHeader(header, fileIN, dataIn);

		} finally {
			if (closeAfterRead) {
				if (fileIN != null)
					try {
						fileIN.close();
					} catch (IOException e) {
						//
					}
			}
		}
		return header;

	}
}
