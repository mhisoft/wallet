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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import org.mhisoft.common.event.EventDispatcher;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.common.util.ReflectionUtil;
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
	WalletModel model;
	WalletForm form;
	DisplayMode currentMode= DisplayMode.view;


	//for setting with reflection
	Map<String, FiledObject> fields = new HashMap<>();

	//for show/hide display
	//List<JComponent> itemList = new ArrayList<>();

	public DisplayMode getDisplayMode() {
		return currentMode;
	}

	public void setDisplayMode(DisplayMode mode) {
		EventDispatcher.instance.dispatchEvent(new MHIEvent(EventType.ViewModeChangeEvent, "setDisplayMode", mode));
		this.currentMode = mode;

	}

	class FiledObject {
		ItemType type;
		//JTextComponent  ,JComponent
		JComponent fld;
		JLabel labelFld;

		public FiledObject(ItemType type, JLabel label, JComponent fld) {
			this.type = type;
			this.fld = fld;
			this.labelFld = label;
		}
	}


	public ItemDetailView(WalletModel model, WalletForm form) {
		this.form = form;
		this.model = model;
		fields.put("name", new FiledObject(ItemType.category, form.labelName, form.fldName));
		fields.put("URL", new FiledObject(ItemType.item, form.labelURL, form.fldURL));
		fields.put("userName", new FiledObject(ItemType.item, form.labelUsername, form.fldUserName));
		fields.put("accountNumber", new FiledObject(ItemType.item, form.labelAccount, form.fldAccountNumber));
		fields.put("password", new FiledObject(ItemType.item, form.labelPassword, form.fldPassword));
		fields.put("notes", new FiledObject(ItemType.category, form.labelNotes, form.fldNotes));
		fields.put("pin", new FiledObject(ItemType.item, form.labelPin, form.fldPin));
		fields.put("expMonth", new FiledObject(ItemType.item, form.labelExpMonth, form.fldExpMonth));
		fields.put("expYear", new FiledObject(ItemType.item, form.labelExpYear, form.fldExpYear));
		fields.put("accountType", new FiledObject(ItemType.item, form.labelAccountType, form.fldAccountType));
		fields.put("phone", new FiledObject(ItemType.item, form.labelPhone, form.fldPhone));
		fields.put("detail1", new FiledObject(ItemType.item, form.labelDetail1, form.fldDetail1));
		fields.put("detail2", new FiledObject(ItemType.item, form.labelDetail2, form.fldDetail2));
		fields.put("detail3", new FiledObject(ItemType.item, form.labelDetail3, form.fldDetail3));
		fields.put("cvc", new FiledObject(ItemType.item, form.labelCVC, form.fldCVC));
		//fields.put("imageLabel", new FiledObject(ItemType.item, form.imageLabel, form.imageLabel));


	}

	//clear all the fields as the model is closed
	public void closeView() {
		for (Map.Entry<String, FiledObject> entry : fields.entrySet()) {
			if (entry.getValue().fld instanceof JTextComponent) {
				JTextComponent fld = (JTextComponent) entry.getValue().fld;
				fld.setText(null);
				fld.setEditable(false);
			}


		}

		form.closeView();

	}


	public void displayWalletItemDetails(final WalletItem item) {
		displayWalletItemDetails(item, this.getDisplayMode());

	}

	public void displayWalletItemDetails(final WalletItem item, DisplayMode displayMode) {

		setDisplayMode(displayMode);   //fire events

		if (displayMode != DisplayMode.view) {
			//coming to edit and add model
			//don't mark model changed yet if data is not changed.
			//model.setModified(true);
			item.setLastViewdDate(new Timestamp(System.currentTimeMillis()));
		}



		/*set model value to fileds and set visibility*/
		try {
			for (Map.Entry<String, FiledObject> entry : fields.entrySet()) {
				if (entry.getValue().fld instanceof JTextComponent) {
					JTextComponent fld = (JTextComponent) entry.getValue().fld;
					fld.setText((String) ReflectionUtil.getFieldValue(item, entry.getKey()));
					fld.setEditable(displayMode != DisplayMode.view);
				}

				JComponent fld = entry.getValue().fld;
				if (entry.getValue().type == ItemType.item) {
					fld.setVisible(item.getType() == ItemType.item);
					entry.getValue().labelFld.setVisible(item.getType() == ItemType.item);
				} else
					fld.setVisible(true);

			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();

		}


		form.labelLastModified.setText( item.getLastModifiedDate()==null?"NA": item.getLastModifiedDate().toString() );
		form.displayAttachment();


		form.btnTogglePasswordView.setVisible(item.getType() == ItemType.item);
		form.btnLaunchURL.setVisible(item.getType() == ItemType.item);

		if (displayMode == DisplayMode.edit || displayMode == DisplayMode.add) {
			form.fldName.requestFocus();
		}


	}

	public void editDetailAction() {
		if (model.getCurrentItem() != null) {
			displayWalletItemDetails(model.getCurrentItem(), DisplayMode.edit);
		}

	}

	public void cancelEditAction() {
		if (model.getCurrentItem() != null) {
			model.setModified(false);
			displayWalletItemDetails(model.getCurrentItem(), DisplayMode.view);
		}
	}

	public boolean isModified() {
		//need a clone of the current Item.
		WalletItem newItem = model.getCurrentItem().clone();
		if (newItem == null)
			return false;


		// reflect the current item detail into the newItem
		try {
			for (Map.Entry<String, FiledObject> entry : fields.entrySet()) {
				if (entry.getValue().fld instanceof JTextComponent) {
					ReflectionUtil.setFieldValue(newItem, entry.getKey()
							, ((JTextComponent) entry.getValue().fld).getText());
				}
			}

			boolean b = model.getCurrentItem().isSame(newItem);
			return !b;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred", e.getMessage());
		}
		return false;
	}


	/**
	 * Update the data from form to the model's currentItem
	 */
	public void updateToModel() {
		if (model.getCurrentItem() != null) {

			try {
				for (Map.Entry<String, FiledObject> entry : fields.entrySet()) {
					if (entry.getValue().fld instanceof JTextComponent) {
						ReflectionUtil.setFieldValue(model.getCurrentItem(), entry.getKey()
								, ((JTextComponent) entry.getValue().fld).getText());
					}
				}

				model.setModified(true);
				model.getCurrentItem().setLastModifiedDate(new Timestamp(System.currentTimeMillis()));


			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				DialogUtils.getInstance().error("Error occurred", e.getMessage());
			}

			displayWalletItemDetails(model.getCurrentItem(), DisplayMode.view);
		}
	}


}
