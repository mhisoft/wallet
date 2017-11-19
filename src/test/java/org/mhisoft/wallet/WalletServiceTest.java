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

package org.mhisoft.wallet;

import java.io.File;

import org.junit.Test;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.FileContent;
import org.mhisoft.wallet.service.ServiceRegistry;

import static org.junit.Assert.assertEquals;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Nov, 2017
 */
public class WalletServiceTest extends WalletFileTest {


	@Test
	public void testExportEntry() {
		String eVaultFileExp = "test_vault_001_exported.dat";
		File f=null;
		try {


			model.getItemsFlatList().clear();
			model.setupTestData();
			ServiceRegistry.instance.getWalletForm().setModel(model);
			PassCombinationVO passVO = new PassCombinationVO("testPa!ss213%", "112233") ;
			String hash = HashingUtils.createHash(passVO.getPass());
			String combinationHash = HashingUtils.createHash(passVO.getCombination());
			model.setHash(hash, combinationHash);
			model.initEncryptor(passVO);


			PassCombinationVO passVO2 = new PassCombinationVO("testPa!ss213%_new","030405") ;

			//save to the export vault.
			f = new File(eVaultFileExp);
			f.delete();
			walletService.exportItem(dNode, passVO2, eVaultFileExp  );


		   //rest read it back

			WalletModel expModel = new WalletModel();
			String hash2 = HashingUtils.createHash(passVO2.getPass());
			String combinationHash2 = HashingUtils.createHash(passVO2.getCombination());
			expModel.setHash(hash2, combinationHash2);
			expModel.initEncryptor(passVO2);
			FileContent fc  = walletService.readFromFile(eVaultFileExp, expModel.getEncryptor() );
			assertEquals(2, fc.getWalletItems().size()); //root and dnote
			assertEquals(fc.getWalletItems().get(1), dNode);


		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}
		finally {
			if (f!=null)
				f.delete();
		}

	}

}
