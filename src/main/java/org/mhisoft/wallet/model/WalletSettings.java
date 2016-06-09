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

import org.mhisoft.wallet.SystemSettings;
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
	public static final long DEFAULT_IDLE_TIMEOUT = 15; //min, default 15 min.


	//manage it in the Registry
//	public static WalletSettings instance ;
//
	public static WalletSettings getInstance() {
		return ServiceRegistry.instance.getWalletSettings();
	}


	private transient String passPlain;
	private int fontSize;
	private int dimensionX;
	private int dimensionY;
	private double dividerLocation;
	private String lastFile;
	private long idleTimeout; //in milli seconds


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

	public int getDimensionX() {
		return dimensionX==0?1200:dimensionX;
	}

	public void setDimensionX(int dimensionX) {
		this.dimensionX = dimensionX;
	}

	public int getDimensionY() {
		return dimensionY==0?800:dimensionY;
	}

	public void setDimensionY(int dimensionY) {
		this.dimensionY = dimensionY;
	}

	public double getDividerLocation() {
		return dividerLocation<=0 || dividerLocation>1.0?0.2:dividerLocation;
	}

	public void setDividerLocation(double dividerLocation) {
		this.dividerLocation = dividerLocation;
	}

	public String getLastFile() {
	//	return lastFile==null?WalletSettings.defaultWalletFile:lastFile;
		return lastFile;
	}

	public void setLastFile(String lastFile) {
		this.lastFile = lastFile;
	}

	public long getIdleTimeout() {  //in seconds
		if (SystemSettings.debug)
			return 3;
		else
			return idleTimeout<=0?DEFAULT_IDLE_TIMEOUT:idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	@Override
	public String toString() {
		return "WalletSettings{" +
				"passPlain='" + passPlain + '\'' +
				", fontSize=" + fontSize +
				", dimensionX=" + dimensionX +
				", dimensionY=" + dimensionY +
				", lastFile=" + lastFile +
				", idleTimeout=" + idleTimeout +
				'}';
	}
}
