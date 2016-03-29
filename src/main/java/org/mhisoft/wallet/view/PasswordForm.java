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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


	public PasswordForm() {
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pass = verifyUserInput();
				if (pass != null) {
					//todo verify with hash.
					Encryptor.createInstance(pass);
					dialog.dispose();


					DialogUtils.getInstance().info("Please keep this in a safe place, it can't be recovered\n" + fldPassword.getText()+",dial:"//
							 + spinner1.getValue()+"-"+ spinner2.getValue()+"-"+ spinner3.getValue() //
					);


				}
			}
		});
	}

	public void showPasswordForm(JFrame parentFrame ) {
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

	private String verifyUserInput() {


		if (fldPassword.getPassword().length<8) {
			DialogUtils.getInstance().info("The password is too short.");
			return null;
		}


		if ( spinner1.getValue()==spinner2.getValue() && spinner2.getValue()==spinner3.getValue()) {
			DialogUtils.getInstance().info("Three dials can not be  the same.");
			return null;
		}

		//todo more rules

		return spinner2.getValue().toString()+fldPassword.getText()+spinner1.getValue().toString()+spinner3.getValue().toString();


	}


}
