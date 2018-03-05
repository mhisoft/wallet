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
import java.io.File;

import org.mhisoft.common.util.security.PBEEncryptor;
import org.mhisoft.wallet.model.FileAccessFlag;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.FileContentHeader;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.PasswordForm;
import org.mhisoft.wallet.view.ViewHelper;

/**
 * Description: ImportWalletAction
 * need to be prototype
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ImportWalletAction implements Action {


	@Override
	public ActionResult execute(Object... params) {

		String importFile = ViewHelper.chooseFilev1(null);
		if (importFile != null) {

			if (new File(importFile).isFile()) {
				FileContentHeader header = ServiceRegistry.instance.getWalletService().readHeader(importFile, true);
				String importFileHash = header.getPassHash();
				String combinationHash = header.getCombinationHash();

				WalletModel model = ServiceRegistry.instance.getWalletModel();

				//see if the current vault password can be used
				VerifyPasswordAction verifyPasswordAction = ServiceRegistry.instance.getService(BeanType.prototype, VerifyPasswordAction.class);
				PassCombinationVO passVO = model.getUserEnteredPassForVerification();
				ActionResult result = verifyPasswordAction.execute(passVO
						, importFileHash
						, combinationHash
						,Boolean.TRUE
				);
				if (result.isSuccess()) {

					_doImport(passVO, importFile, importFileHash);
					return new ActionResult(true);


				} else {


					//now show password form to enter the password.
					PasswordForm passwordForm = new PasswordForm("Opening file: " + importFile);
					passwordForm.showPasswordForm(ServiceRegistry.instance.getWalletForm()
							, new PasswordForm.PasswordFormActionListener(null) {
								@Override
								public void actionPerformed(ActionEvent e) {
									PassCombinationVO passVO = passwordForm.getUserEnteredPassForVerification();

									VerifyPasswordAction verifyPasswordAction = ServiceRegistry.instance.getService(BeanType.prototype,
											VerifyPasswordAction.class);
									ActionResult result = verifyPasswordAction.execute(passVO, importFileHash, combinationHash);
									if (result.isSuccess()) {
										//close the password form
										passwordForm.exitPasswordForm();

										_doImport(passVO, importFile, importFileHash);


									}
								}
							}, null


					);

					//hand off to the OK listener , actionPerformed() below to do VerifyPasswordAction

					return new ActionResult(true);

				}
			} else {
				DialogUtils.getInstance().error("Error", "Can not open file " + importFile);
			}


		}


		return new ActionResult(false);
	}

	private void _doImport(PassCombinationVO passVO, String importFile, String importFileHash) {
		try {
			ServiceRegistry.instance.getWalletModel().setImporting(true);
			doTheImport(importFile, passVO, importFileHash);
			//reload the view.
			ServiceRegistry.instance.getWalletModel().setModified(true);

			//reload the tree and trigger change node.
			ServiceRegistry.instance.getWalletForm().loadTree();
			DialogUtils.getInstance().info("Import successfully. It has not been saved yet. Make sure to save after validating the import.");
			ServiceRegistry.instance.getWalletModel().setImporting(false);
		} catch (Exception e1) {
			DialogUtils.getInstance().error(e1.getMessage());
		}
	}


	protected void doTheImport(String impFilename, PassCombinationVO importFilePass, String importFileHash) {

		PBEEncryptor encryptor = new PBEEncryptor(importFilePass.getPassAndCombination());
		WalletModel impModel = ServiceRegistry.instance.getWalletService().loadVaultIntoModel(impFilename, encryptor);

		WalletModel model = ServiceRegistry.instance.getWalletModel();
		WalletItem root = model.getRootItem();

		//refer to the imp model so that we have the encryptor and store file name
		//when need to save the current model.
		model.setImpModel(impModel);


		//prepare to be merged attachment entries , mark them as Merge
		for (WalletItem impItem : impModel.getItemsFlatList()) {
			if (impItem.getAttachmentEntry() != null) {
				impItem.getAttachmentEntry().setAccessFlag(FileAccessFlag.Merge);
			}
		}
		;


		//will change the tree structure
		try {
			int i = 1;
			while (i < impModel.getItemsFlatList().size()) {

				WalletItem impItem = impModel.getItemsFlatList().get(i);
				WalletItem modelItem = impItem.findItemInModel();
				if (modelItem != null) {
					if (!modelItem.isSame(impItem)) {
						modelItem.mergeFrom(impItem);
						modelItem.setName(modelItem.getName() + "(*)");
					}

					//merge the attachment, only when the target is empty
					if (modelItem.getAttachmentEntry() == null && impItem.getAttachmentEntry() != null) {
						modelItem.setAttachmentEntry(impItem.getAttachmentEntry());
						modelItem.getAttachmentEntry().setAccessFlag(FileAccessFlag.Merge);
						modelItem.setName(modelItem.getName() + "(*)");
					}

				} else {
					if (impItem.getType().equals(ItemType.category)) {
						//found a new category which does not have a match
						//add it and all its children
						root.addChild(impItem);
						impItem.setName(impItem.getName() + "(*)");
						//jump to next cat
						i++;
						while (i < impModel.getItemsFlatList().size()) {
							if (impModel.getItemsFlatList().get(i).getType() == ItemType.category) {
								i--; ////need to stay on this item i, after break i++ will be called.
								break;
							}
							i++;
						}


					} else {
						//it is an item, locate its parent and find a match in the current model
						WalletItem modelCat = impItem.getParent().findItemInModel();
						if (modelCat == null) {
							//not matches cat can't happen here
							throw new IllegalStateException("not matches cat can't happen here, impItem: " + impItem);
						}
						modelCat.addChild(impItem);
						impItem.setName(impItem.getName() + "(*)");

					}
				}

				i++;
			}

			//rebuild back the list
			model.buildFlatListFromTree();

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error(e.getMessage());
		}


	}


}
