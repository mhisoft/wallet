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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.mhisoft.common.util.StringUtils;
import org.mhisoft.wallet.SystemSettings;
import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.action.CreateWalletAction;
import org.mhisoft.wallet.action.LoadWalletAction;
import org.mhisoft.wallet.action.VerifyPasswordAction;
import org.mhisoft.wallet.model.PasswordValidator;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class PasswordForm implements ActionListener {
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
	private JLabel labelMsg;
	private JButton button1;
	JDialog dialog;

	String title;

	WalletForm walletForm;

	List<Component> componentsList = new ArrayList<>();


	PasswordValidator passwordValidator = ServiceRegistry.instance.getService(BeanType.singleton, PasswordValidator.class);

	Object spinner1Value, spinner2Value, spinner3Value;


	public PasswordForm(String title) {
		passwordValidator = new PasswordValidator();
		this.title = title;
		init();

	}

	class SpinnerFocusAdapter extends FocusAdapter {

		JSpinner spinner;

		public SpinnerFocusAdapter(JSpinner spinner) {
			this.spinner = spinner;
		}

		/* The jspinner focus listener events*/
		public void focusGained(FocusEvent e) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					if (spinner1 == spinner && spinner1Value != null) {
						//spinner1.setValue(spinner1Value);
						//System.out.println("focus gained on spinner 1");
						JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
						editor.getTextField().setText(spinner1.getValue().toString());


					} else if (spinner2 == spinner && spinner2Value != null) {
						//	spinner2.setValue(spinner2Value);
						JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
						editor.getTextField().setText(spinner2.getValue().toString());
						//System.out.println("focus gained on spinner 2");
					} else if (spinner3 == spinner && spinner3Value != null) {
						//spinner3.setValue(spinner3Value);
						JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
						editor.getTextField().setText(spinner3.getValue().toString());
						//System.out.println("focus gained on spinner 3");
					}
				}
			});


		}

		public void focusLost(FocusEvent e) {
			if (spinner1 == spinner) {
				spinner1Value = spinner1.getValue();
				//System.out.println("focus lost on spinner 1");
			} else if (spinner2 == spinner) {
				spinner2Value = spinner2.getValue();
				//System.out.println("focus lost on spinner 2");
			} else if (spinner3 == spinner) {
				spinner3Value = spinner3.getValue();
				//System.out.println("focus lost on spinner 3");
			}

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
					editor.getTextField().setText("*");
				}
			});


		}
	}


	// TODO: place custom component creation code here
	private void createUIComponents() {

		spinner1 = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
		spinner2 = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
		spinner3 = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));


		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner1.getEditor();
		editor.getTextField().addFocusListener(new SpinnerFocusAdapter(spinner1));

		JSpinner.DefaultEditor editor2 = (JSpinner.DefaultEditor) spinner2.getEditor();
		editor2.getTextField().addFocusListener(new SpinnerFocusAdapter(spinner2));

		JSpinner.DefaultEditor editor3 = (JSpinner.DefaultEditor) spinner3.getEditor();
		editor3.getTextField().addFocusListener(new SpinnerFocusAdapter(spinner3));

	}

	private class IndexedFocusTraversalPolicy extends
			FocusTraversalPolicy {

		private ArrayList<Component> components =
				new ArrayList<Component>();

		public void addIndexedComponent(Component component) {
			components.add(component);
		}

		@Override
		public Component getComponentAfter(Container aContainer,
				Component aComponent) {
			int atIndex = components.indexOf(aComponent);
			int nextIndex = (atIndex + 1) % components.size();
			return components.get(nextIndex);
		}

		@Override
		public Component getComponentBefore(Container aContainer,
				Component aComponent) {
			int atIndex = components.indexOf(aComponent);
			int nextIndex = (atIndex + components.size() - 1) %
					components.size();
			return components.get(nextIndex);
		}

		@Override
		public Component getFirstComponent(Container aContainer) {
			return components.get(0);
		}

		@Override
		public Component getLastComponent(Container aContainer) {
			return components.get(components.size());
		}

		@Override
		public Component getDefaultComponent(Container aContainer) {
			return spinner1;
		}
	}

	//entry point

	/**
	 * @param walletForm
	 * @param actionListener optional action listener. if not provided, the one in this class will be used.
	 */
	public void showPasswordForm(WalletForm walletForm, ActionListener actionListener) {
		this.walletForm = walletForm;
		dialog = new JDialog(walletForm.frame, "Please enter password", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().add(mainPanel);
		dialog.setPreferredSize(new Dimension(800, 400));

//		final Container contentPane = dialog.getContentPane();
		dialog.getRootPane().setDefaultButton(btnOk);

		dialog.pack();


		// Put client property
		fldPassword.putClientProperty("JPasswordField.cutCopyAllowed", true);
		if (title == null)
			labelMsg.setText("Creating New Wallet");
		else {
			labelMsg.setText(title);
		}


		if (actionListener != null)
			btnOk.addActionListener(actionListener);
		else
			btnOk.addActionListener(this);


//		IndexedFocusTraversalPolicy policy = new IndexedFocusTraversalPolicy();
//		policy.addIndexedComponent(spinner1);
//		policy.addIndexedComponent(spinner2);
//		policy.addIndexedComponent(spinner3);
//		policy.addIndexedComponent(fldPassword);
//		policy.addIndexedComponent(btnCancel);
//		policy.addIndexedComponent(btnOk);
//		dialog.setFocusTraversalPolicy(policy);


		//componentsList = ViewHelper.getAllComponents(dialog);
		//ViewHelper.setFontSize(componentsList, WalletSettings.getInstance().getFontSize());

		dialog.setLocationRelativeTo(walletForm.frame);
		dialog.setVisible(true);
		spinner1.requestFocusInWindow();

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

//		KeyListener keyListener = new KeyListener() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//
//			}
//
//			@Override
//			public void keyPressed(KeyEvent e) {
//
//				// e.getSource()==btnCancel evals to true
//				if (e.getKeyCode()==KeyEvent.VK_ENTER){
//					dialog.dispose();
//				}
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//
//			}
//		}    ;

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
//				walletForm.exit();
			}
		});


	}


	public String getUserEnterPassword() {

		if (SystemSettings.debug && !StringUtils.hasValue(fldPassword.getText())) {
			return "12Abc12334&5AB1310";
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
				CreateWalletAction createWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CreateWalletAction.class);
				createWalletAction.execute(pass, this);
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
