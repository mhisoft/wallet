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
import org.mhisoft.wallet.service.FileContent;
import org.mhisoft.wallet.service.IdleTimerService;
import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description: Action for loading the wallet.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class LoadWalletAction implements Action {

	@Override
	public ActionResult execute(Object... params) {
		//PassCombinationVO pass = (PassCombinationVO) params[0];

		String fileName=null;
		if (params.length>=3)
			fileName = (String) params[2];

		if (fileName==null)
		   fileName = WalletSettings.getInstance().getLastFile();
		else {
			WalletSettings.getInstance().setLastFile(fileName); //save will use this
		}



		WalletModel model  = ServiceRegistry.instance.getWalletModel();


		if (new File(fileName).isFile()) {
			//read tree from the existing file
			WalletSettings.getInstance().setLastFile(fileName);
			WalletSettings.getInstance().addRecentFile(fileName);

			model.initEncryptor(model.getPassVOForEncryptor());
			FileContent fileContent = ServiceRegistry.instance.getWalletService().readFromFile(fileName,
					model.getEncryptorForRead());
			model.setItemsFlatList(fileContent.getWalletItems());
			model.setPassHash(fileContent.getHeader().getPassHash());
			model.setCombinationHash(fileContent.getHeader().getCombinationHash());
			//opened a old version file, need to save to v13 version on close. .
			if (model.getDataFileVersion()<=13)
				ServiceRegistry.instance.getWalletModel().setModified(true);


		} else {
			//new file, needs to be saved on close.
			ServiceRegistry.instance.getWalletModel().setModified(true);
			model.initEncryptor(model.getPassVOForEncryptor());
		}
		ServiceRegistry.instance.getWalletForm().loadTree();
		ServiceRegistry.instance.getWalletForm().loadOptionsIntoView();

		//start the idle count down timer
		IdleTimerService.instance.start();


		return new ActionResult(true);

	}

}
