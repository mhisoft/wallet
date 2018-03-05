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

import java.util.logging.Logger;
import java.io.File;

import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.FileContentHeader;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.PasswordForm;
import org.mhisoft.wallet.view.ViewHelper;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class OpenWalletFileAction implements Action {

	private static final Logger logger = Logger.getLogger(OpenWalletFileAction.class.getName());

	@Override
	public ActionResult execute(Object... params) {
		String fileName;

		if (params.length>0)
			fileName = (String)params[0];
		else {
			//String[] parts = FileUtils.splitFileParts(WalletSettings.getInstance().getLastFile());
			fileName = ViewHelper.chooseFilev1();  //last file's directory.
		}


		if (fileName != null) {

			WalletModel model = ServiceRegistry.instance.getWalletForm().getModel();
			model.reset();

			if (new File(fileName).isFile()) {
				WalletSettings.getInstance().setLastFile(fileName);
				WalletSettings.getInstance().addRecentFile(fileName);

				//read header and populate the model with hashes.
				FileContentHeader header = ServiceRegistry.instance.getWalletService().readHeader(fileName, true);
				model.setDataFileVersion(header.getVersion());
				model.setPassHash(header.getPassHash());
				model.setCombinationHash(header.getCombinationHash());
				//now show password form to enter the password.
				PasswordForm passwordForm = new PasswordForm("Opening file:"+ fileName);
				passwordForm.showPasswordForm(ServiceRegistry.instance.getWalletForm(), null,null);
				logger.info("Open file: " + fileName);
				logger.info("\t version: " + model.getCurrentDataFileVersion());

				//hand off to the OK listener and

				//VerifyPasswordAction

				//todo cancel the password prompt should not close the old wallet file

				return new ActionResult(true);

			} else {
				DialogUtils.getInstance().error("Error", "Can not open file " + fileName);
			}

		}


		return new ActionResult(false);
	}
}
