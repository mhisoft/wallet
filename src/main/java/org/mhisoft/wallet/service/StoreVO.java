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

import java.util.List;

import org.mhisoft.wallet.model.WalletItem;

/**
 * Description:  the value object holds the store data read from the vault file.
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class StoreVO {

	private FileContentHeader header;
	private List<WalletItem> walletItems;
	private int deletedEntriesInStore;

	public StoreVO() {
		header = new FileContentHeader();
	}

	public List<WalletItem> getWalletItems() {
		return walletItems;
	}

	public void setWalletItems(List<WalletItem> walletItems) {
		this.walletItems = walletItems;
	}

	public FileContentHeader getHeader() {
		return header;
	}

	public void setHeader(FileContentHeader header) {
		this.header = header;
	}

	public int getDeletedEntriesInStore() {
		return deletedEntriesInStore;
	}

	public void setDeletedEntriesInStore(int deletedEntriesInStore) {
		this.deletedEntriesInStore = deletedEntriesInStore;
	}

	public WalletItem getWalletItem(String sysGUID) {
		for (WalletItem walletItem : walletItems) {
			if (walletItem.getSysGUID().equals(sysGUID))  {
				return walletItem;
			}
		}
		return null;
	}

}
