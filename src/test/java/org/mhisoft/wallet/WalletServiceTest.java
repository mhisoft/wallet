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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mhisoft.common.util.security.HashingUtils;
import org.mhisoft.wallet.model.PassCombinationEncryptionAdaptor;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.service.StoreVO;

import java.io.File;


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
			model.setVaultFileName("test_vault_001.dat");
			ServiceRegistry.instance.getWalletForm().setModel(model);
			PassCombinationVO passVO = new PassCombinationEncryptionAdaptor("testPa!ss213%", "112233") ;
			String hash = HashingUtils.createHash(passVO.getPass());
			String combinationHash = HashingUtils.createHash(passVO.getCombination());
			model.setHash(hash, combinationHash);
			model.initEncryptor(passVO);


			PassCombinationVO passVO2 = new PassCombinationEncryptionAdaptor("testPa!ss213%_new","030405") ;
			WalletModel expModel = new WalletModel();
			expModel.initEncryptor(passVO2);

			//save to the export vault.
			f = new File(eVaultFileExp);
			f.delete();


			walletService.exportItem(dNode, passVO2, eVaultFileExp );
			walletService.exportItem(eNode, passVO2, eVaultFileExp );


		   //rest read it back


			String hash2 = HashingUtils.createHash(passVO2.getPass());
			String combinationHash2 = HashingUtils.createHash(passVO2.getCombination());
			expModel.setHash(hash2, combinationHash2);

			StoreVO fc  = walletService.loadVault(eVaultFileExp, expModel.getEncryptor() );

			model.getItemsFlatList().clear();
			model.setItemsFlatList(fc.getWalletItems());
			model.buildTreeFromFlatList();

			/*

			    export
			       c
			         --dNode
			         --eNode
			 */
			Assertions.assertEquals(4, fc.getWalletItems().size()); //root and dnote
			Assertions.assertEquals(fc.getWalletItems().get(1), cNode);
			Assertions.assertEquals(fc.getWalletItems().get(2), dNode);
			Assertions.assertEquals(fc.getWalletItems().get(3), eNode);
			Assertions.assertEquals(dNode.getParent(), cNode);
			Assertions.assertEquals(eNode.getParent(), cNode);

		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
		}
		finally {
			if (f!=null)
				f.delete();
		}

	}

}
