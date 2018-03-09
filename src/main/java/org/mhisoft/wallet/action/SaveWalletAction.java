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

import org.mhisoft.common.util.security.HashingUtils;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.DisplayMode;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description: save the wallet changes to file
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class SaveWalletAction implements Action {


	protected boolean saveVault(String fileName) {
		//save the wallet
		WalletModel model = ServiceRegistry.instance.getWalletModel();
		model.buildFlatListFromTree();

		try {
			if (model.getCurrentDataFileVersion() < WalletModel.LATEST_DATA_VERSION) {
				//data conversion.
				//to be saved to the latest v13 version , prepare the new hashes.
				// and need the combination hash set
				//model.setDataFileVersion(WalletModel.LATEST_DATA_VERSION);
				String combinationHash = HashingUtils.createHash(model.getPassVO().getCombination());
				model.setCombinationHash(combinationHash);
				model.setPassHash(HashingUtils.createHash(model.getPassVO().getPass()));
				model.initEncryptor(model.getPassVO());

			}
		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("An error occurred", "Failed to save:" + e.getMessage());
		}


		ServiceRegistry.instance.getWalletService().saveVault(fileName, model, model.getEncryptor());
		ServiceRegistry.instance.getWalletModel().setModified(false);
		//DialogUtils.getInstance().info("Saved successfully.");
		ServiceRegistry.instance.getWalletForm().showMessage("Saved successfully.");

		WalletForm form = ServiceRegistry.instance.getWalletForm();
		form.displayWalletItemDetails(model.getCurrentItem(), DisplayMode.view);

		return true;

	}


	@Override
	public ActionResult execute(Object... params) {
		String fileName = WalletSettings.getInstance().getLastFile();
		if (saveVault(fileName)) {
			ServiceRegistry.instance.getWalletSettingsService().updateAndSavePreferences();
			return new ActionResult(true);
		} else
			return new ActionResult(false);


	}

}
