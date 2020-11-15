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
    PassCombinationVO passVO2;

    WalletModel expModel;


    public void parepareSourceVault(String eVaultFileExp) {

        try {
            model.setVaultFileName("test_vault_001.dat");
            ServiceRegistry.instance.getWalletForm().setModel(model);
            PassCombinationVO passVO = new PassCombinationEncryptionAdaptor("testPa!ss213%", "112233");
            String hash = HashingUtils.createHash(passVO.getPass());
            String combinationHash = HashingUtils.createHash(passVO.getCombination());
            model.setHash(hash, combinationHash);
            model.initEncryptor(passVO);

            passVO2 = new PassCombinationEncryptionAdaptor("testPa!ss213%_new", "030405");
            expModel = new WalletModel();
            expModel.initEncryptor(passVO2);

            new File(eVaultFileExp).delete();


        } catch (HashingUtils.CannotPerformOperationException e) {
            e.printStackTrace();
        }
        finally {
            new File(eVaultFileExp).delete();
        }
    }


    @Test
    public void testExportEntry() {

        String eVaultFileExp = "test_vault_001_exported_1.dat";
        parepareSourceVault(eVaultFileExp);

        walletService.exportItem(cNode, passVO2, eVaultFileExp);

        try {
            // read it back
            String hash2 = HashingUtils.createHash(passVO2.getPass());
            String combinationHash2 = HashingUtils.createHash(passVO2.getCombination());
            expModel.setHash(hash2, combinationHash2);

            StoreVO fc = walletService.loadVault(eVaultFileExp, expModel.getEncryptor());

            if (!new File(eVaultFileExp).delete())


            model.getItemsFlatList().clear();
            model.setItemsFlatList(fc.getWalletItems());
            model.buildTreeFromFlatList();


			/*

			    export
			       c
			         --dNode
			         --eNode
			 */

            Assertions.assertEquals(4, fc.getWalletItems().size());
            Assertions.assertTrue(fc.getWalletItems().get(1).isSame(cNode));
            Assertions.assertTrue(fc.getWalletItems().get(2).isSame(dNode));
            Assertions.assertTrue(fc.getWalletItems().get(3).isSame(eNode));
            Assertions.assertTrue(dNode.getParent().isSame(cNode));
            Assertions.assertTrue(eNode.getParent().isSame(cNode));

        } catch (HashingUtils.CannotPerformOperationException e) {
            e.printStackTrace();
        }
        finally {
            new File(eVaultFileExp).delete();
        }


    }

    @Test
    public void exportRoot() {

        String eVaultFileExp = "test_vault_001_exported_2.dat";

        parepareSourceVault(eVaultFileExp);

        walletService.exportItem(root, passVO2, eVaultFileExp);

        try {
            // read it back
            String hash2 = HashingUtils.createHash(passVO2.getPass());
            String combinationHash2 = HashingUtils.createHash(passVO2.getCombination());
            expModel.setHash(hash2, combinationHash2);

            StoreVO fc = walletService.loadVault(eVaultFileExp, expModel.getEncryptor());

            model.getItemsFlatList().clear();
            model.setItemsFlatList(fc.getWalletItems());
            model.buildTreeFromFlatList();


		/*
		     root
		        --b
		        --c
		        	--d
		            --e
		        --f
		        	--g

		 */
            Assertions.assertEquals(7, fc.getWalletItems().size()); //root and dnote
            Assertions.assertTrue(fc.getWalletItems().get(0).isSame(root));
            Assertions.assertTrue(fc.getWalletItems().get(1).isSame(bNode));
            Assertions.assertTrue(fc.getWalletItems().get(2).isSame(cNode));
            Assertions.assertTrue(fc.getWalletItems().get(3).isSame(dNode));
            Assertions.assertTrue(fc.getWalletItems().get(4).isSame(eNode));
            Assertions.assertTrue(fc.getWalletItems().get(5).isSame(fNode));
            Assertions.assertTrue(fc.getWalletItems().get(6).isSame(gNode));

            Assertions.assertEquals(3, fc.getWalletItems().get(0).getChildren().size()); //root has three children
            Assertions.assertNull( fc.getWalletItems().get(1).getChildren()); //bnode
            Assertions.assertEquals(2, fc.getWalletItems().get(2).getChildren().size()); //cnode has 2 children
            Assertions.assertEquals(1, fc.getWalletItems().get(5).getChildren().size()); //fnode has 1 children


        } catch (HashingUtils.CannotPerformOperationException e) {
            e.printStackTrace();
        }
        finally {
            new File(eVaultFileExp).delete();
        }

    }

}
