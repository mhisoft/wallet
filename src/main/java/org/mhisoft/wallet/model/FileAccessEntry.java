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

import org.mhisoft.common.util.StringUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class FileAccessEntry {
	String GUID;     //40
	long position;   //8 bytes
	long size;       //8 bytes
	transient String accessFlag; //Add, Remove
	transient String fileName;
	transient File file;

	public static  int getHeaderBytes() {
		return 40 + 8 + 8;
	}


	public FileAccessEntry(String GUID) {
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
		setFile(new File(fileName)) ;
	}
}
