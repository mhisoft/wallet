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

package org.mhisoft.wallet.action;

import java.awt.Dimension;

import javax.swing.JSplitPane;

import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description: save the wallet changes to file
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class SaveWalletAction implements Action {


	protected void save(String fileName) {
		//save the wallet
//		if (ServiceRegistry.instance.getWalletModel().isModified()) {
		WalletModel model = ServiceRegistry.instance.getWalletModel();
		model.buildFlatListFromTree();
		ServiceRegistry.instance.getWalletService().saveToFile(fileName, model, model.getEncryptor());
		ServiceRegistry.instance.getWalletModel().setModified(false);
		//DialogUtils.getInstance().info("Saved successfully.");
		ServiceRegistry.instance.getWalletForm().setMessage("Saved successfully.");

		ServiceRegistry.instance.getWalletForm().displayWalletItemDetails(model.getCurrentItem());

//		}
	}

	@Override
	public ActionResult execute(Object... params) {

		String fileName = WalletSettings.getInstance().getLastFile();

		save(fileName);


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

		return new ActionResult(true);


	}

}
