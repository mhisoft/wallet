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

import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.Confirmation;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description: Action for closing the wallet
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class CloseWalletAction extends SaveWalletAction {


	@Override
	protected void save(String filename) {
		//save the wallet
		if (ServiceRegistry.instance.getWalletModel().isModified()) {
			if (DialogUtils.getConfirmation(ServiceRegistry.instance.getWalletForm().getFrame()
					, "Save the changes before close?") == Confirmation.YES) {
				WalletModel model = ServiceRegistry.instance.getWalletModel();
				model.buildFlatListFromTree();
				ServiceRegistry.instance.getWalletService().saveToFile(filename, model, model.getEncryptor());
				ServiceRegistry.instance.getWalletModel().setModified(false);
				DialogUtils.getInstance().info("Saved to file successfully.");
			}


		}
	}


	@Override
	public ActionResult execute(Object... params) {

		return super.execute(params);



	}

}
