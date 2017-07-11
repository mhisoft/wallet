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

import java.util.ArrayList;
import java.util.List;

/**
 * Description:ImageAccessTable
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class FileAccessTable {
	List<FileAccessEntry> entries = new ArrayList<>();
	public  int deletedEntries=0;

	/**
	 * add entry and return the GUID is the newly created entry.
	 * @return
	 */
	public FileAccessEntry addEntry() {
		FileAccessEntry entry = new FileAccessEntry(null);
		entries.add(entry);
		return entry;
	}

	public void addEntry(FileAccessEntry entry) {
		entries.add(entry);
	}


	public  FileAccessEntry getEntry(String GUID) {
		for (int i = 0; i < entries.size(); i++) {
			FileAccessEntry fileAccessEntry = entries.get(i);
			if (fileAccessEntry.getGUID().equals(GUID))
				return fileAccessEntry;
		}
		return null;
	}


	public int getSize() {
		return entries.size();
	}

	public List<FileAccessEntry> getEntries() {
		return entries;
	}

	public int getDeletedEntries() {
		return deletedEntries;
	}

	public void setDeletedEntries(int deletedEntries) {
		this.deletedEntries = deletedEntries;
	}
}
