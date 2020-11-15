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
import java.util.Objects;
import java.util.Vector;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import org.mhisoft.wallet.SystemSettings;
import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.action.CreateWalletAction;
import org.mhisoft.wallet.action.LoadWalletAction;
import org.mhisoft.wallet.action.VerifyPasswordAction;
import org.mhisoft.wallet.model.PassCombinationEncryptionAdaptor;
import org.mhisoft.wallet.model.PassCombinationVO;
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
@SuppressWarnings("rawtypes")
public class PasswordForm implements ActionListener {
	private JPanel mainPanel;
	private JPasswordField fldPassword;
	private JButton btnCancel;
	private JButton btnOk;
	private JLabel labelPassword;
	private JLabel labelSafeCombination;
	private JLabel labelInst1;
	private JLabel labelInst2;
	private JLabel labelInst3;
	private JLabel labelMsg;
	private JButton button1;
	private JComboBox<Item> comboBox1;
	private JComboBox<Item> comboBox2;
	private JComboBox<Item> comboBox3;
	JDialog dialog;

	String title;

	WalletForm walletForm;

	List<Component> componentsList = new ArrayList<>();


	PasswordValidator passwordValidator = ServiceRegistry.instance.getService(BeanType.singleton, PasswordValidator.class);

	transient Item spinner1Item, spinner2Item, spinner3Item;


	public PasswordForm(String title) {
		passwordValidator = new PasswordValidator();
		this.title = title;
		init();

	}


	class Item {
		Integer value;
		String text;

		public Item(Integer value, String text) {
			this.value = value;
			this.text = text;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return getText();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Item item = (Item) o;
			return Objects.equals(value, item.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}
	}


	/* place custom component creation code here*/
	private void createUIComponents() {

		Vector<Item> model = new Vector<>();
		model.addElement( new Item(-1, "*")); //pos zero item is -1 and shows *

		//pos 1 to 99
		for (int i = 1; i <=99; i++) {
			model.addElement( new Item(i, Integer.valueOf(i).toString()));
		}
		ComboBoxModel<Item> model1 = new DefaultComboBoxModel<>(model);
		ComboBoxModel<Item> model2 = new DefaultComboBoxModel<>(model);
		ComboBoxModel<Item> model3 = new DefaultComboBoxModel<>(model);
		comboBox1 = new JComboBox<>(model1);
		comboBox2 = new JComboBox<>(model2);
		comboBox3 = new JComboBox<>(model3);

		comboBox1.addFocusListener(new MyFocusAdapter(comboBox1));
		comboBox2.addFocusListener(new MyFocusAdapter(comboBox2));
		comboBox3.addFocusListener(new MyFocusAdapter(comboBox3));

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
			return comboBox1;
		}
	}


	public interface Callback {
		void setResult(ActionResult result);

	}

	public static abstract class PasswordFormActionListener implements ActionListener {
		private Callback callback;

		public PasswordFormActionListener(Callback callback) {
			this.callback = callback;
		}

	}

	/**
	 *
	 */
	public static class PasswordFormCancelActionListener extends PasswordFormActionListener {
		PasswordForm passwordForm;

		public PasswordFormCancelActionListener(Callback callback, PasswordForm passwordForm) {
			super(callback);
			this.passwordForm = passwordForm;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			passwordForm.exitPasswordForm();
		}
	}


	public PasswordFormCancelActionListener defaultCancelListener = new PasswordFormCancelActionListener(null, this);

	/**
	 * @param walletForm
	 * @param okListener optional action listener. if not provided, the one in this class will be used.
	 */
	public void showPasswordForm(WalletForm walletForm, PasswordFormActionListener okListener, PasswordFormActionListener cancelListener) {
		this.walletForm = walletForm;
		dialog = new JDialog(walletForm.frame, this.title != null ? this.title : "Please enter password", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().add(mainPanel);
		dialog.setPreferredSize(new Dimension(800, 400));

		dialog.getRootPane().setDefaultButton(btnOk);


		dialog.pack();


		// Put client property
		fldPassword.putClientProperty("JPasswordField.cutCopyAllowed", true);
		if (title == null)
			labelMsg.setText("Creating New Wallet");
		else {
			labelMsg.setText(title);
		}


		if (okListener != null)
			btnOk.addActionListener(okListener);
		else {
			btnOk.addActionListener(this);
		}

		if (cancelListener != null)
			btnCancel.addActionListener(cancelListener);
		else {
			btnCancel.addActionListener(defaultCancelListener);
		}


		dialog.setLocationRelativeTo(walletForm.frame);
		dialog.setVisible(true);


		comboBox1.requestFocusInWindow();

	}


	public void exitPasswordForm() {
		dialog.dispose();
	}


	public String getUserInputPass() {
		return new String(fldPassword.getPassword());
	}


	public String getCombinationDisplay() {
		return spinner1Item.getValue() + "-" + spinner2Item.getValue() + "-" + spinner3Item.getValue();
	}


	private void init() {
		//
	}


	//set the user entered pass and combination to the PassCombinationVO
	public PassCombinationVO getUserEnteredPassForVerification() {
		Integer safeValue1, safeValue2 , safeValue3;


		if (!SystemSettings.isDevMode) {
			 safeValue1 = spinner1Item.getValue();
			 safeValue2 = spinner2Item.getValue();
			 safeValue3 = spinner3Item.getValue();

			if (safeValue1.equals(safeValue2) && safeValue2.equals(safeValue3)) {
				DialogUtils.getInstance().info("Cant' use the same numbers for the safe combination.");
				return null;
			}

			if (!passwordValidator.validate( String.valueOf(fldPassword.getPassword()))) {
				DialogUtils.getInstance().info("Please use a password following the above rules.");
				return null;
			}
		}
		else {
			//dev mode
			safeValue1 =1;
			safeValue2 = 2;
			safeValue3 = 3;
		}


		PassCombinationVO passVO = new PassCombinationEncryptionAdaptor();
		WalletModel model = ServiceRegistry.instance.getWalletModel();
		//set the raw data only, do not add logic here. or later we can't get the raw pass
		passVO.setCombination(safeValue1.toString(), safeValue2.toString(), safeValue3.toString());
		if (SystemSettings.isDevMode) {
			passVO.setPass("Test123!");
		} else {
			passVO.setPass(String.valueOf(fldPassword.getPassword()));
		}

		model.setPassVO(passVO);
		return model.getUserEnteredPassForVerification();

	}

	/**
	 * Default ok button listener
	 *
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean createHash = ServiceRegistry.instance.getWalletModel().getPassHash() == null;
		PassCombinationVO passVO = getUserEnteredPassForVerification();

		if (passVO == null) {
			//user input is not good. try again.
		} else {
			if (createHash) {
				//user password is no good, did not pass validation.
				CreateWalletAction createWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CreateWalletAction.class);
				createWalletAction.execute(passVO, this);
			} else {
				VerifyPasswordAction verifyPasswordAction = ServiceRegistry.instance.getService(BeanType.prototype, VerifyPasswordAction.class);
				ActionResult result = verifyPasswordAction.execute(passVO,
						ServiceRegistry.instance.getWalletModel().getPassHash(),
						ServiceRegistry.instance.getWalletModel().getCombinationHash()
				);
				if (result.isSuccess()) {
					//close the password form
					exitPasswordForm();

					//load the wallet
					LoadWalletAction loadWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, LoadWalletAction.class);
					loadWalletAction.execute(passVO);
				}
			}
		}
	}

	/**
	 * class for capture fcocus related actions on the safe combination controls
	 * in order to mask the control.
	 */
	class MyFocusAdapter extends FocusAdapter {

		JComboBox ctl;

		public MyFocusAdapter(JComboBox ctl) {
			this.ctl = ctl;
		}

		public void focusLost(FocusEvent e) {
			Item ctlValue =  (Item) ctl.getSelectedItem();
//			if (SystemSettings.debug)
//				System.out.println("focus lost on comboBox1" + ctl.getName() + ", value" + ctlValue);

			if (ctlValue != null && ctlValue.getValue() >= 1) {
				if (comboBox1 == ctl) {
					spinner1Item = ctlValue;
				} else if (comboBox2 == ctl) {
					spinner2Item = ctlValue;
				} else if (comboBox3 == ctl) {
					spinner3Item = ctlValue;
				}
			}


			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ctl.setSelectedIndex(0); // position zero is reserved with "*"
				}
			});
		}

		/*
		The focus listener events
		on focus, we need to reselect the value saved in the spinner1 to 3. 
		*/
		public void focusGained(FocusEvent e) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					if (comboBox1 == ctl && spinner1Item != null) {
//						if (SystemSettings.debug)
//							System.out.println("focus gained on comboBox1");

						comboBox1.setSelectedItem(spinner1Item);
					} else if (comboBox2 == ctl && spinner2Item != null) {
//						if (SystemSettings.debug)
//							System.out.println("focus gained on comboBox2");

						comboBox2.setSelectedItem(spinner2Item);
					} else if (comboBox3 == ctl && spinner3Item != null) {
//						if (SystemSettings.debug)
//							System.out.println("focus gained on comboBox3");
						comboBox3.setSelectedItem(spinner3Item);
					}

				}
			});


		}


	}


}
