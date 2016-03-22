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

package org.mhisoft.wallet.view;

import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class ItemDetailView {
	WalletModel model ;
	WalletForm form;

	public ItemDetailView(WalletModel model, WalletForm form ) {
		this.form = form;
		this.model = model;
	}

	public void displayWalletItemDetails(final WalletItem item) {
		if (item.getType() == ItemType.category) {
			form.fldName.setText(item.getName());
			//todo hide all other fields
			form.btnAddNode.setVisible(true);

			//todo only allow to delete if there are no items under
			form.btnDeleteNode.setVisible(true);


		} else {

			form.btnAddNode.setVisible(false);
			form.btnDeleteNode.setVisible(true);


			form.fldName.setText(item.getName());
			form.fldURL.setText(item.getURL());
			form.fldUserName.setText(item.getUserName());
			form.fldPassword.setText(item.getPassword());
			form.fldAccountNumber.setText(item.getAccountNumber());
			form.fldNotes.setText(item.getNotes());
		}

	}

}
