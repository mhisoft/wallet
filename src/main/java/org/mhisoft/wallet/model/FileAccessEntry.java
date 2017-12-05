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

package org.mhisoft.wallet.model;

import java.io.File;
import java.io.Serializable;
import java.security.AlgorithmParameters;

import org.mhisoft.common.util.StringUtils;

/**
 * Description:    FileAccessEntry
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class FileAccessEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	String GUID;     //40
	long position;   // file entry start position.

	transient byte[] fileContent;    //store the file content after reading from data file.
	long size;       //8 bytes file content size
	String fileName;
	File file;
	FileAccessFlag accessFlag;

	FileAccessEntry newEntry;
	transient AlgorithmParameters algorithmParameters;   //for the content.
	long posOfContent;
	int encSize;


	public static int getHeaderBytes() {
		return 40 + 8 + 8;
	}


	public FileAccessEntry(String GUID) {
		this.accessFlag=FileAccessFlag.None;

		if (GUID == null)
			this.GUID = StringUtils.getGUID();
		else
			this.GUID = GUID;
	}

	public String getGUID() {
		return GUID;
	}

	public void setGUID(String GUID) {
		this.GUID = GUID;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		setSize(file.length());
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		if (fileName!=null)
			setFile(new File(fileName));
		else
			setFile(null);
	}

	public FileAccessFlag getAccessFlag() {
		return accessFlag;
	}

	public void setAccessFlag(FileAccessFlag accessFlag) {
		this.accessFlag = accessFlag;
	}

	public FileAccessEntry getNewEntry() {
		return newEntry;
	}

	public void setNewEntry(FileAccessEntry newEntry) {
		this.newEntry = newEntry;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public long getPosOfContent() {
		return posOfContent;
	}

	public void setPosOfContent(long posOfContent) {
		this.posOfContent = posOfContent;
	}

	public int getEncSize() {
		return encSize;
	}

	public void setEncSize(int encSize) {
		this.encSize = encSize;
	}

	public AlgorithmParameters getAlgorithmParameters() {
		return algorithmParameters;
	}

	public void setAlgorithmParameters(AlgorithmParameters algorithmParameters) {
		this.algorithmParameters = algorithmParameters;
	}


	@Override
	public String toString() {
		return "FileAccessEntry{" +
				"GUID='" + GUID + '\'' +
				", position=" + position +
				", size=" + size +
				", fileName='" + fileName + '\'' +
				", accessFlag=" + accessFlag +
				", posOfContent=" + posOfContent +
				", encSize=" + encSize +
				'}';
	}
}
