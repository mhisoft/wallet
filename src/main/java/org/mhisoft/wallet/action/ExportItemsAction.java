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

import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.PasswordForm;
import org.mhisoft.wallet.view.VaultNameDialog;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description: Action for creating a new Vault.
 *
 * @author Tony Xue
 * @since July, 2016
 */
public class ExportItemsAction implements Action {

	//pick a file
	String newVaultFileName;

	@Override
	public ActionResult execute(Object... params) {

		//validate


		WalletItem sourceItem = ServiceRegistry.instance.getWalletModel().getCurrentItem();

		if (sourceItem == null) {
			DialogUtils.getInstance().error("Error", "Select the item to export.");
			return new ActionResult(false);
		}

		/*reuse the same export vault in the session*/
		if (ServiceRegistry.instance.getWalletModel().getExportVaultFileName() != null) {

			ServiceRegistry.instance.getWalletService().exportItem(sourceItem
					, ServiceRegistry.instance.getWalletModel().getExportVaultPass()
					, ServiceRegistry.instance.getWalletModel().getExportVaultFileName()
			);
			return new ActionResult(true);
		}

		else {


			VaultNameDialog.display("Exporting" , "Export item '"+ sourceItem.getName() + "' to this eVault:",
					"eVault-export-" + System.currentTimeMillis()
					, new VaultNameDialog.NewVaultCallback() {
						@Override
						public void onOK(String fileName) {
							newVaultFileName = fileName;
						}

						@Override
						public void onCancel() {
							newVaultFileName = null;
						}
					});


			if (newVaultFileName == null)
				return new ActionResult(false);


			//2. for a new vault , get the password
	  	    /*Delegate to the password form the create password*/
			WalletForm form = ServiceRegistry.instance.getWalletForm();
			PasswordForm passwordForm = new PasswordForm("Enter the password for the target Vault:" + newVaultFileName);
			passwordForm.showPasswordForm(form,
					new PasswordForm.PasswordFormActionListener(null) {

						/*when Ok button is clicked*/
						@Override
						public void actionPerformed(ActionEvent e) {

							PassCombinationVO pass = passwordForm.getUserEnteredPassForVerification();

							if (pass == null) {
								//user input is not good. try again.
							}
							else {

								//close the password form
								passwordForm.exitPasswordForm();

								ServiceRegistry.instance.getWalletService().exportItem(sourceItem, pass, newVaultFileName);
								ServiceRegistry.instance.getWalletModel().setExportVaultFileName(newVaultFileName);
								ServiceRegistry.instance.getWalletModel().setExportVaultPass(pass);
								form.cancelEdit();

							}

						}
					}, null);

			return new ActionResult(true);
		}
	}


}
