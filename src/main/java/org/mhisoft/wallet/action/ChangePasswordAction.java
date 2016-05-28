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

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.FileContent;
import org.mhisoft.wallet.service.ServiceRegistry;

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

	private void changeDataFilePass(String oldPass, String newPass, String dataFile) {

		try {
			WalletModel model = ServiceRegistry.instance.getWalletModel();

			Encryptor.createInstance(oldPass);
			FileContent fileContent = ServiceRegistry.instance.getWalletService().readFromFile(dataFile, Encryptor.getInstance());
			model.setItemsFlatList(fileContent.getWalletItems());


			Encryptor.createInstance(newPass);
			model.setPassHash(HashingUtils.createHash(newPass));
			ServiceRegistry.instance.getWalletService().saveToFile(dataFile, model);



		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}


	}


	@Override
	public ActionResult execute(Object... params) {


		return null;
	}
}
