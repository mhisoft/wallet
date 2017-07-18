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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class DialogUtils {

	JFrame frame;
	static DialogUtils instance;

	public DialogUtils(JFrame frame) {
		this.frame = frame;
	}

	public static DialogUtils getInstance() {
		return instance;
	}

	public static DialogUtils create(JFrame frame) {
		instance = new DialogUtils(frame);
		return instance;
	}


	public static Confirmation getConfirmation(final JFrame frame, final String question, final Confirmation... options) {
		ViewHelper.setUIManagerFontSize();
		int dialogResult = JOptionPane.showConfirmDialog(frame, question, "Please confirm", JOptionPane.YES_NO_CANCEL_OPTION);
		if (JOptionPane.YES_OPTION == dialogResult) {
			return Confirmation.YES;
		} else if (JOptionPane.CANCEL_OPTION == dialogResult) {
			return Confirmation.QUIT;
		} else
			return Confirmation.NO;

		//todo support presend a check box to check Yes for all future confirmations
		//return  Confirmation.YES_TO_ALL
	}


	/**
	 * Display warning
	 *
	 * @param title
	 * @param message
	 */
	public void warn(final String title, final String message) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				//custom title, warning icon
				ViewHelper.setUIManagerFontSize();
				JOptionPane.showMessageDialog(frame,
						message, //"Eggs are not supposed to be green.",
						title, //"Inane warning",
						JOptionPane.WARNING_MESSAGE);
			}
		});

	}

	/**
	 * Display error
	 *
	 * @param title
	 * @param error
	 */
	public void error(final String title, final String error) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				//custom title, warning icon
				ViewHelper.setUIManagerFontSize();
				JOptionPane.showMessageDialog(frame,
						error, //"Eggs are not supposed to be green.",
						title, //"Inane warning",
						JOptionPane.ERROR_MESSAGE);
			}
		});


	}

	/**
	 * System error message
	 *
	 * @param error
	 */
	public void error(final String error) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				//custom title, warning icon
				ViewHelper.setUIManagerFontSize();
				JOptionPane.showMessageDialog(frame,
						error,
						"An system error occurred",
						JOptionPane.ERROR_MESSAGE);
			}
		});


	}

	/**
	 * Display message
	 *
	 * @param message
	 */
	public void info(final String message) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				//custom title, warning icon
				ViewHelper.setUIManagerFontSize();
				JOptionPane.showMessageDialog(frame,
						message //"Eggs are not supposed to be green.",
				);
			}
		});


	}

	public void showMessageModelDialog(final String message) {

		//custom title, warning icon
		ViewHelper.setUIManagerFontSize();
		JOptionPane.showMessageDialog(frame, message);

	}

}
