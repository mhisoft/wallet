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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.NewVaultDialog;
import org.mhisoft.wallet.view.PasswordForm;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description: Action for creating a new Vault.
 *
 * @author Tony Xue
 * @since July, 2016
 */
public class NewWalletAction implements Action {

	//pick a file
	String newVaultfn ;

	@Override
	public ActionResult execute(Object... params) {

		NewVaultDialog.display(new NewVaultDialog.NewVaultCallback() {
			@Override
			public void onOK(String fileName) {
				newVaultfn = fileName;
			}

			@Override
			public void onCancel() {
				newVaultfn =null;
			}
		});


		if (newVaultfn==null)
			return new ActionResult(false);

		/*close and save the current file is needed. */
		CloseWalletAction closeWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CloseWalletAction.class);
		ActionResult r = closeWalletAction.execute(Boolean.FALSE); //close the wallet file quietly  ?

		if (r.isSuccess()) {

//
//			//Open old wallet file or create new password.
//			// new file name to be saved.
//			String fname = "eVault-" + System.currentTimeMillis();
//			newVaultfn = WalletSettings.userHome+fname +".dat";
//			//newVaultfn = ViewHelper.chooseFile(null);
//			if ( new File(newVaultfn).isFile()) {   //file does not exist
//				DialogUtils.getInstance().error("The file exists. Use a new file name.");
//				return new ActionResult(false);
//			}



			/*Delegate to the password form the create password*/
			WalletForm form = ServiceRegistry.instance.getWalletForm();
			PasswordForm passwordForm = new PasswordForm("Creating a new wallet");
			passwordForm.showPasswordForm(form, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					PassCombinationVO pass = passwordForm.getUserEnteredPassForVerification();

					if (pass == null) {
						//user input is not good. try again.
					}
					else {

						//create an empty tree with one root.
						WalletModel model  = ServiceRegistry.instance.getWalletModel();
						String[] parts  = FileUtils.splitFileParts(newVaultfn) ;

						model.setupEmptyWalletData(parts[1]);


						CreateWalletAction createWalletAction = ServiceRegistry.instance.getService(
								BeanType.prototype, CreateWalletAction.class);
						createWalletAction.execute(pass, passwordForm, newVaultfn);
					}

				}
			});

			return new ActionResult(true);
		}
		return new ActionResult(false);
	}


}
