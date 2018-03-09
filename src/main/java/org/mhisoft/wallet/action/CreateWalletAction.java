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
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.PasswordForm;

/**
 * Description:   action for creating the password.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class CreateWalletAction implements Action {

	@Override
	public ActionResult execute(Object... params) {
		PassCombinationVO passVO = (PassCombinationVO) params[0];
		PasswordForm passwordForm = (PasswordForm) params[1];
		String fileName=null;
		if (params.length>=3)
			fileName = (String) params[2];

		if (createPassword(passVO)) {
			//exit the password form here
			passwordForm.exitPasswordForm();
			DialogUtils.getInstance().info("Please keep this in a safe place, it can't be recovered\n"
					+ passwordForm.getUserInputPass() + ", combination:"
					+ passwordForm.getCombinationDisplay());

			//proceed to load wallet
			LoadWalletAction loadWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, LoadWalletAction.class);
			loadWalletAction.execute(passVO, ServiceRegistry.instance.getWalletModel().getPassHash(), fileName);
		}

		return new ActionResult(true);

	}

	//create the hash and save to file.
	protected void createHash(PassCombinationVO passVO) {
		try {
			String hash = HashingUtils.createHash(passVO.getPass());
			String combinationHash = HashingUtils.createHash(passVO.getCombination());
			ServiceRegistry.instance.getWalletModel().setPassHash(hash);
			ServiceRegistry.instance.getWalletModel().setCombinationHash(combinationHash);
			ServiceRegistry.instance.getWalletModel().setPassVO(passVO);


		} catch (HashingUtils.CannotPerformOperationException e1) {
			e1.printStackTrace();
			DialogUtils.getInstance().error("An error occurred", "Failed to hash the password:" + e1.getMessage());
		}
	}


	public boolean createPassword(PassCombinationVO pass) {
		ServiceRegistry.instance.getWalletModel().setPassVO(pass);
		createHash(pass);
		return true;
	}


}
