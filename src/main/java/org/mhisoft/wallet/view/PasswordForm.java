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

import java.util.List;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.AlgorithmParameters;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.wallet.model.PasswordValidator;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletSettings;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class PasswordForm {
	private JPanel mainPanel;
	private JPasswordField fldPassword;
	private JSpinner spinner1;
	private JButton btnCancel;
	private JButton btnOk;
	private JSpinner spinner2;
	private JSpinner spinner3;
	private JLabel labelPassword;
	private JLabel labelSafeCombination;
	JDialog dialog;

	PasswordValidator passwordValidator;


	public PasswordForm() {
		passwordValidator = new PasswordValidator();
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});


		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				boolean createHash;

				String pass = getUserEnterPassword();
				if (createHash && pass != null) {

					WalletSettings.instance.setPassPlain(pass);

					createHash(pass);

					Encryptor.createInstance(pass);
					dialog.dispose();


					DialogUtils.getInstance().info("Please keep this in a safe place, it can't be recovered\n" + fldPassword.getText() + ",dial:"//
							+ spinner1.getValue() + "-" + spinner2.getValue() + "-" + spinner3.getValue() //
					);


				}
			}
		});
	}

	public void showPasswordForm(JFrame parentFrame) {
		dialog = new JDialog(parentFrame, "Please enter password", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().add(mainPanel);
		dialog.setPreferredSize(new Dimension(800, 300));
		dialog.pack();
		dialog.setLocationRelativeTo(parentFrame);

		SpinnerModel spinnerModel = new SpinnerNumberModel(10, 10, 99, 1);
		SpinnerModel spinnerMode2 = new SpinnerNumberModel(10, 10, 99, 1);
		SpinnerModel spinnerMode3 = new SpinnerNumberModel(10, 10, 99, 1);
		spinner1.setModel(spinnerModel);
		spinner2.setModel(spinnerMode2);
		spinner3.setModel(spinnerMode3);


		dialog.setVisible(true);
	}

	// an action listener to be used when an action is performed
	// (e.g. button is pressed)
	class MyActionListener implements ActionListener {

		//close and dispose of the window.
		public void actionPerformed(ActionEvent e) {
			System.out.println("disposing the window..");
			dialog.setVisible(false);
			dialog.dispose();
		}
	}

	private String getUserEnterPassword(boolean create) {
		if (create) {
			if (!passwordValidator.validate(fldPassword.getText())) {
				DialogUtils.getInstance().info("Please use a password following the above rules.");
				return null;
			}
			if (spinner1.getValue() == spinner2.getValue() && spinner2.getValue() == spinner3.getValue()) {
				DialogUtils.getInstance().info("Three dials can not all be the same.");
				return null;
			}
			//
			return spinner2.getValue().toString() + fldPassword.getText() + spinner1.getValue().toString() + spinner3.getValue().toString();
		} else {
			//verify with existing pass todo
			String userInput = spinner2.getValue().toString() + fldPassword.getText() + spinner1.getValue().toString() + spinner3.getValue().toString();

		}

		return null;


	}

	private void createHash(String pass) {
		try {
			String hash = HashingUtils.createHash(pass);
			WalletSettings.instance.setHash(hash);
			saveSettingsToFile(WalletSettings.instance);

		} catch (HashingUtils.CannotPerformOperationException e1) {
			e1.printStackTrace();
			DialogUtils.getInstance().error("An error occurred", "Failed to hash the password:" + e1.getMessage());
		}
	}


	String settingsFile = "walletsettings.dat";

	private void saveSettingsToFile(WalletSettings settings) {
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(settingsFile));
			outputStream.writeObject(settings);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					//
				}
		}
	}


}
