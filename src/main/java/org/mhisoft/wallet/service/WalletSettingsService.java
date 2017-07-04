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

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JSplitPane;

import org.mhisoft.wallet.SystemSettings;
import org.mhisoft.wallet.model.WalletSettings;

/**
 * Description: service for the settings.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class WalletSettingsService {
	//user.dir --> app launch dir


	/**
	 * Save the settings to file
	 * @param settings
	 */
	public void saveSettingsToFile(WalletSettings settings) {
		ObjectOutputStream outputStream = null;
		try {

			if (SystemSettings.isDevMode)
				settings.setIdleTimeout(-1);

			outputStream = new ObjectOutputStream(new FileOutputStream(WalletSettings.settingsFile));
			outputStream.writeObject(settings);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					//
				}
		}
	}

	public WalletSettings readSettingsFromFile() {
			ObjectInputStream stream = null;
			try {

				stream = new ObjectInputStream(new FileInputStream(WalletSettings.settingsFile));
				WalletSettings settings = (WalletSettings) stream.readObject();
				ServiceRegistry.instance.registerSingletonService(settings);

				return settings;
			} catch (FileNotFoundException e) {
				 return null;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (stream != null)
					try {
						stream.close();
					} catch (IOException e) {
						//
					}
			}
	}


	public void updateAndSavePreferences() {
		//save the settings
		Dimension d = ServiceRegistry.instance.getWalletForm().getFrame().getSize();
		WalletSettings settings = ServiceRegistry.instance.getWalletSettings();
		settings.setDimensionX(d.width);
		settings.setDimensionY(d.height);

		//calculate proportion
		JSplitPane split = ServiceRegistry.instance.getWalletForm().getSplitPanel();
		double p = Double.valueOf(split.getDividerLocation()).doubleValue() / Double.valueOf(split.getWidth() - split.getDividerSize());
		settings.setDividerLocation(Double.valueOf(p * 100 + 0.5).intValue() / Double.valueOf(100));

		ServiceRegistry.instance.getWalletSettingsService().saveSettingsToFile(settings);
	}


}
