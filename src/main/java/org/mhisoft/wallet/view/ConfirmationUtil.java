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

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class ConfirmationUtil {

	public static  Confirmation getConfirmation(final JFrame frame, final String question, final Confirmation... options) {
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
}
