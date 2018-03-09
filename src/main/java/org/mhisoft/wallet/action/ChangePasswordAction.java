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

import org.mhisoft.common.util.security.PBEEncryptor;
import org.mhisoft.common.util.security.HashingUtils;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.PasswordForm;

/**
 * Description: Change the password
 * needs to be prototype
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ChangePasswordAction implements Action {


	boolean oldPassVerified = false;


	PasswordForm.Callback callback = new PasswordForm.Callback() {
		@Override
		public void setResult(ActionResult result) {
			oldPassVerified = true;
		}
	};


	private void verifyOldPass() {
		oldPassVerified = false;
		final PasswordForm passwordForm = new PasswordForm("Enter the current password");

		passwordForm.showPasswordForm(ServiceRegistry.instance.getWalletForm(), new PasswordForm.PasswordFormActionListener(
						callback

				) {
					@Override
					public void actionPerformed(ActionEvent e) {
						//boolean createHash = ServiceRegistry.instance.getWalletModel().getPassHash() == null;
						PassCombinationVO passVO = passwordForm.getUserEnteredPassForVerification();

						if (passVO == null) {
							//user input is not good. try again.
						} else {
							VerifyPasswordAction verifyPasswordAction = ServiceRegistry.instance.getService(BeanType.prototype, VerifyPasswordAction.class);
							ActionResult result = verifyPasswordAction.execute(passVO,
									ServiceRegistry.instance.getWalletModel().getPassHash(),
									ServiceRegistry.instance.getWalletModel().getCombinationHash()
							);
							if (result.isSuccess()) {
								//close the password form
								passwordForm.exitPasswordForm();
								callback.setResult(new ActionResult(true));

							} else
								callback.setResult(new ActionResult(false));
						}
					}

				}
				,null

		);
	}


	private void enterNewPass() {

		/*now show password form to enter the password.*/
		final PasswordForm passwordForm2 = new PasswordForm("Enter a new password");
		passwordForm2.showPasswordForm(ServiceRegistry.instance.getWalletForm(), new PasswordForm.PasswordFormActionListener(null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				PassCombinationVO newPass = passwordForm2.getUserEnteredPassForVerification();

				try {
					String hash = HashingUtils.createHash(newPass.getPass());
					String combinationHash = HashingUtils.createHash(newPass.getCombination());
					WalletModel model = ServiceRegistry.instance.getWalletModel();
					model.setHash(hash, combinationHash);


					model.setPassVO(newPass);
					passwordForm2.exitPasswordForm();


//			Encryptor oldEnc = new  Encryptor(newPass);
//			FileContent fileContent = ServiceRegistry.instance.getWalletService().readFromFile(dataFile,  oldEnc  );
//			model.setItemsFlatList(fileContent.getWalletItems());
					PBEEncryptor newEnc = model.createNewEncryptor(newPass);

					//save the file with new password.
					ServiceRegistry.instance.getWalletService().saveVaultWithNewPass(  //
							WalletSettings.getInstance().getLastFile() //
							, model, newEnc);  //

					DialogUtils.getInstance().info("<html>The password has successfully been changed.<br>"
							+ "Please keep this in a safe place, it can't be recovered when lost:\n"
							+ passwordForm2.getUserInputPass() + ", combination:"
							+ passwordForm2.getCombinationDisplay());


					//reload
					LoadWalletAction loadWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, LoadWalletAction.class);
					loadWalletAction.execute();


				} catch (HashingUtils.CannotPerformOperationException e1) {
					e1.printStackTrace();
					DialogUtils.getInstance().error("An error occurred", "Failed to hash the password:" + e1.getMessage());
				}

			}
		},null);
	}

	@Override
	public ActionResult execute(Object... params) {

		if (ServiceRegistry.instance.getWalletModel().isModified()) {
			DialogUtils.getInstance().info("Please save the changes first.");
			return new ActionResult(false);
		}


		verifyOldPass();

		if (oldPassVerified) {
			enterNewPass();

		}
		return new ActionResult(true);
	}
}
