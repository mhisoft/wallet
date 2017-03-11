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

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.PasswordForm;

/**
 * Description: Change the password
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ChangePasswordAction implements Action {

	public static void main(String[] args) {
		ChangePasswordAction action = new ChangePasswordAction();
		//action.changeDataFilePass();
	}



	private void startByGettingNewPass() {
		//now show password form to enter the password.
		PasswordForm passwordForm = new PasswordForm("Enter a new password");
		passwordForm.showPasswordForm(ServiceRegistry.instance.getWalletForm(), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PassCombinationVO newPass = passwordForm.getUserEnteredPassForVerification();

				try {
					String hash = HashingUtils.createHash(newPass.getPass());
					String combinationHash = HashingUtils.createHash(newPass.getCombination());
					WalletModel model = ServiceRegistry.instance.getWalletModel();
					model.setPassHash(hash);
					model.setCombinationHash(combinationHash);

					model.setPassPlain(newPass);
					passwordForm.exitPasswordForm();


//			Encryptor oldEnc = new  Encryptor(newPass);
//			FileContent fileContent = ServiceRegistry.instance.getWalletService().readFromFile(dataFile,  oldEnc  );
//			model.setItemsFlatList(fileContent.getWalletItems());
					Encryptor newEnc = model.createNewEncryptor(newPass) ;

					//save the file with new password.
					ServiceRegistry.instance.getWalletService().saveToFile(  //
							WalletSettings.getInstance().getLastFile() //
							, model, newEnc);  //

					DialogUtils.getInstance().info("<html>The password has successfully been changed.<br>"
							+"Please keep this in a safe place, it can't be recovered when lost:\n"
									+ passwordForm.getUserInputPass() + ", combination:"
									+ passwordForm.getCombinationDisplay())   ;


				} catch (HashingUtils.CannotPerformOperationException e1) {
					e1.printStackTrace();
					DialogUtils.getInstance().error("An error occurred", "Failed to hash the password:" + e1.getMessage());
				}

			}
		});

	}



	@Override
	public ActionResult execute(Object... params) {

		startByGettingNewPass();

		return null;
	}
}
