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

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.StringUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public abstract class AbstractDataService implements  DataService {

	protected void writeString(DataOutputStream out, String str) throws IOException {
		if (str==null)
			throw new RuntimeException("input str is null");

		byte[] _byte = StringUtils.getBytes(str);
		//write size
		out.write(FileUtils.intToByteArray(_byte.length));
		out.write(_byte);

	}


	protected String readString(FileInputStream fileInputStream) throws IOException  {
		int numBytes = FileUtils.readInt(fileInputStream);
		byte[] _byte = new byte[numBytes];
		int readBytes = fileInputStream.read(_byte);
		if (readBytes!=numBytes)
			throw new RuntimeException("readString() failed, " + "read " + readBytes +" bytes only, expected to read:"+ numBytes);

		return StringUtils.bytesToString(_byte);

	}

	abstract protected FileContentHeader readHeader(FileContentHeader header, FileInputStream fileIN, DataInputStream dataIn  )
			throws IOException ;

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
