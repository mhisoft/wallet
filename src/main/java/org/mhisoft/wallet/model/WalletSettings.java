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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.io.File;
import java.io.Serializable;

import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class WalletSettings implements Serializable {

	private static final Logger logger = Logger.getLogger(WalletSettings.class.getName());


	private static final long serialVersionUID = 1L;
	public static final String userHome = System.getProperty("user.home") + File.separator;
	public static final String fileExt = ".dat";
	public static final String settingsFile = userHome + "eVaultSettings.dat";
	public static final String defaultWalletFile = userHome + "eVault-default.dat";
	public static final long DEFAULT_IDLE_TIMEOUT = 15; //min, default 15 min.
	public static final long RECENT_FILES_LIST_SIZE = 6; //min, default 15 min.


	//manage it in the Registry
//	public static WalletSettings instance ;
//
	public static WalletSettings getInstance() {
		return ServiceRegistry.instance.getWalletSettings();
	}



	private int fontSize;
	private int dimensionX;
	private int dimensionY;
	private double dividerLocation;
	private String lastFile;
	private long idleTimeout; //in milli seconds
	private LinkedList<String> recentFiles ;
	private boolean treeExpanded;

	private String recentOpenDir; //remember the recent open dir so file choose can default to it.


	public int getFontSize() {
		return fontSize == 0 ? 20 : fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}



	public int getDimensionX() {
		return dimensionX == 0 ? 1200 : dimensionX;
	}

	public void setDimensionX(int dimensionX) {
		this.dimensionX = dimensionX;
	}

	public int getDimensionY() {
		return dimensionY == 0 ? 800 : dimensionY;
	}

	public void setDimensionY(int dimensionY) {
		this.dimensionY = dimensionY;
	}

	public double getDividerLocation() {
		return dividerLocation <= 0 || dividerLocation > 1.0 ? 0.2 : dividerLocation;
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



	public String getAttachmentStoreFileName() {
		if (lastFile!=null)
			return ServiceRegistry.instance.getAttachmentService().getAttachmentFileName(lastFile);
		return null;
	}

	public long getIdleTimeout() {  //in seconds
//		if (SystemSettings.isDevMode)
//			return 3;
//		else
			return idleTimeout <= 0 ? DEFAULT_IDLE_TIMEOUT : idleTimeout;

	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public boolean isTreeExpanded() {
		return treeExpanded;
	}

	public void setTreeExpanded(boolean treeExpanded) {
		this.treeExpanded = treeExpanded;
	}

	public List<String> getRecentFiles() {
		if (recentFiles==null)
			recentFiles = new LinkedList<>();

		return recentFiles;
	}


	public void moveFront(String fileName)  {
		recentFiles.remove(fileName) ;
		recentFiles.addFirst(fileName);
		ServiceRegistry.instance.getWalletForm().refreshRecentFilesMenu();
	}


	public void addRecentFile(String fileName) {
		if (fileName==null)
			return;

		if (recentFiles==null)
			recentFiles = new LinkedList<>();

		if (recentFiles.contains(fileName))
			return;  //already in the list

		if (recentFiles.size() >= RECENT_FILES_LIST_SIZE) {
			recentFiles.removeLast();
		}

		recentFiles.addFirst(fileName);

		//add to the current menu
		//todo use event to make it loose coupled?
		WalletForm form  = ServiceRegistry.instance.getWalletForm();
		if (form!=null)
			form.addRecentFile(fileName);

//		if (SystemSettings.debug) {
//			logger.log(Level.FINE, this.toString());
//		}

	}

	public String getRecentOpenDir() {
		return recentOpenDir==null?userHome:recentOpenDir;
	}

	public File getRecentOpenDirFile() {
		return recentOpenDir==null?new File(userHome):new File(recentOpenDir);
	}

	public void setRecentOpenDir(String recentOpenDir) {
		this.recentOpenDir = recentOpenDir;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("WalletSettings{");
		sb.append(", fontSize=").append(fontSize);
		sb.append(", dimensionX=").append(dimensionX);
		sb.append(", dimensionY=").append(dimensionY);
		sb.append(", dividerLocation=").append(dividerLocation);
		sb.append(", lastFile='").append(lastFile).append('\'');
		sb.append(", idleTimeout=").append(idleTimeout);
		sb.append(", recentFiles=").append(recentFiles);
		sb.append(", treeExpanded=").append(treeExpanded);
		sb.append(", recentOpenDir=").append(recentOpenDir);
		sb.append('}');
		return sb.toString();
	}

}
