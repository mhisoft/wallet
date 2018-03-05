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
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description:  Verifying the password against the stored hash which were obtained from the header.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class VerifyPasswordAction implements Action {

	@Override
	public ActionResult execute(Object... params)    {
		PassCombinationVO passVO = (PassCombinationVO)params[0];
		String hash = (String)params[1];
		String combinationHash = (String)params[2];
		boolean quiet=false;
		if (params.length>3)
			quiet= ((Boolean)params[3]).booleanValue();
		try {
			boolean verify = HashingUtils.verifyPassword(passVO.getPass(), hash );
			if (!verify) {
				if (!quiet)
				DialogUtils.getInstance().warn("Error", "Can not confirm your password. Please try again.");
				return new ActionResult(false);
			}

			//v12 data file has no combination Hash
			if (ServiceRegistry.instance.getWalletModel().getCurrentDataFileVersion()>=13  //v13 data file can't have null combinations.
					) {
				 verify = HashingUtils.verifyPassword(passVO.getCombination(), combinationHash);
				if (!verify) {
					if (!quiet)
					DialogUtils.getInstance().warn("Error", "Can not confirm your password. Please try again.");
					return new ActionResult(false);
				}
			}

			return new ActionResult(true);

		} catch (HashingUtils.CannotPerformOperationException | HashingUtils.InvalidHashException e) {
			DialogUtils.getInstance().error("An error occured:" + e.getMessage());
			return new ActionResult(false);
		}
	}


}
