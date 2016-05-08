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

import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class WalletSettings implements Serializable	 {

	private static final long serialVersionUID = 1L;
	public static final String userHome =System.getProperty("user.home") + File.separator;
	public static final String settingsFile =userHome + "WalletSettings.dat"  ;
	public static final String defaultWalletFile = userHome + "DefaultWallet.dat";


	//manage it in the Registry
//	public static WalletSettings instance ;
//
	public static WalletSettings getInstance() {
		return ServiceRegistry.instance.getWalletSettings();
	}


	private transient String passPlain;
	private int fontSize;


	public int getFontSize() {
		return fontSize==0?20:fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getPassPlain() {
		return passPlain;
	}

	public void setPassPlain(String passPlain) {
		this.passPlain = passPlain;
	}

	@Override
	public String toString() {
		return "WalletSettings{" +
				"passPlain='" + passPlain + '\'' +
				", fontSize=" + fontSize +
				'}';
	}
}
