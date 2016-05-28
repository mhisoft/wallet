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

import java.util.ArrayList;
import java.util.List;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.action.CreatePasswordAction;
import org.mhisoft.wallet.action.LoadWalletAction;
import org.mhisoft.wallet.action.VerifyPasswordAction;
import org.mhisoft.wallet.model.PasswordValidator;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class PasswordForm implements  ActionListener {
	private JPanel mainPanel;
	private JPasswordField fldPassword;
	private JSpinner spinner1;
	private JButton btnCancel;
	private JButton btnOk;
	private JSpinner spinner2;
	private JSpinner spinner3;
	private JLabel labelPassword;
	private JLabel labelSafeCombination;
	private JLabel labelInst1;
	private JLabel labelInst2;
	private JLabel labelInst3;
	JDialog dialog;

	WalletForm walletForm;

	List<Component> componentsList = new ArrayList<>();


	PasswordValidator passwordValidator = ServiceRegistry.instance.getService(BeanType.singleton, PasswordValidator.class);


	public PasswordForm() {
		passwordValidator = new PasswordValidator();
		init();
	}


	//entry point

	/**
	 *
	 * @param walletForm
	 * @param actionListener  optional action listener. if not provided, the one in this class will be used.
	 */
	public void showPasswordForm(WalletForm walletForm, ActionListener actionListener) {
		this.walletForm = walletForm;
		dialog = new JDialog(walletForm.frame, "Please enter password", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().add(mainPanel);
		dialog.setPreferredSize(new Dimension(800, 400));
		dialog.pack();
		dialog.setLocationRelativeTo(walletForm.frame);
		// Put client property
		fldPassword.putClientProperty("JPasswordField.cutCopyAllowed",true);

		SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 99, 1);
		SpinnerModel spinnerMode2 = new SpinnerNumberModel(1, 1, 99, 1);
		SpinnerModel spinnerMode3 = new SpinnerNumberModel(1, 1, 99, 1);
		spinner1.setModel(spinnerModel);
		spinner2.setModel(spinnerMode2);
		spinner3.setModel(spinnerMode3);


		componentsList = ViewHelper.getAllComponents(dialog);
		//ViewHelper.setFontSize(componentsList, WalletSettings.getInstance().getFontSize());

		dialog.setVisible(true);

		if (actionListener!=null)
			btnOk.addActionListener(actionListener);
	}


	public void exitPasswordForm() {
		dialog.dispose();
	}


	public String getUserInputPass() {
		return fldPassword.getText();
	}


	public String getCombinationDisplay() {
		return spinner1.getValue() + "-" + spinner2.getValue() + "-" + spinner3.getValue();
	}


	private void init() {

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
//				walletForm.exit();
			}
		});


		btnOk.addActionListener(this);
	}


	public String getUserEnterPassword() {

		if(WalletModel.debug) {
			return  "12Abc12334&5AB1310";
		}

		if (!passwordValidator.validate(fldPassword.getText())) {
			DialogUtils.getInstance().info("Please use a password following the above rules.");
			return null;
		}
		if (spinner1.getValue() == spinner2.getValue() && spinner2.getValue() == spinner3.getValue()) {
			DialogUtils.getInstance().info("Cant' use the same nubmers for the combinations.");
			return null;
		}
		//


		return spinner2.getValue().toString() + fldPassword.getText() + spinner1.getValue().toString() + spinner3.getValue().toString();


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean createHash = ServiceRegistry.instance.getWalletModel().getPassHash() == null;
		String pass = getUserEnterPassword();

		if (pass == null) {
			//user input is not good. try again.
		} else {
			if (createHash) {
				//user password is no good, did not pass validation.
				CreatePasswordAction createPasswordAction = ServiceRegistry.instance.getService(BeanType.prototype, CreatePasswordAction.class);
				createPasswordAction.execute(pass, this);
			} else {
				VerifyPasswordAction verifyPasswordAction = ServiceRegistry.instance.getService(BeanType.prototype, VerifyPasswordAction.class);
				ActionResult result = verifyPasswordAction.execute(pass, ServiceRegistry.instance.getWalletModel().getPassHash());
				if (result.isSuccess()) {
					//clsoe the password form
					exitPasswordForm();

					//load the wallet
					LoadWalletAction loadWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, LoadWalletAction.class);
					loadWalletAction.execute(pass);
				}
			}
		}
	}
}
