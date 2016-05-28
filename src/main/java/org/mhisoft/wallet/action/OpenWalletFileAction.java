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


	@Override
	public ActionResult execute(Object... params) {

		String fileName = ViewHelper.chooseFile(null);
		if (fileName!=null) {

			WalletModel model = ServiceRegistry.instance.getWalletForm().getModel();

			if (new File(fileName).isFile()) {
				WalletSettings.getInstance().setLastFile(fileName);
				FileContentHeader header=ServiceRegistry.instance.getWalletService().readHeader(fileName, true);
				model.setPassHash(header.getPassHash());
				//now show password form to enter the password.
				PasswordForm passwordForm = new PasswordForm();
				passwordForm.showPasswordForm(ServiceRegistry.instance.getWalletForm(), null);

				//hand off to the OK listener and

				//VerifyPasswordAction

				//todo cancel the password prompt should not close the old wallet file

				return new ActionResult(true);

			}
			else {
				DialogUtils.getInstance().error("Error", "Can not open file " + fileName);
			}

		}



		return new ActionResult(false);
	}
}
