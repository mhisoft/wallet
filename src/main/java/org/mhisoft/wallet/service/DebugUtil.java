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

package org.mhisoft.wallet.service;

import org.mhisoft.wallet.WalletMain;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Jul, 2017
 */
public class DebugUtil {


	public static void append(final String msg) {
		jreDebug();

		WalletForm form = ServiceRegistry.instance.getWalletForm();
		form.textAreaDebug.append(msg);
	}


	public static void jreDebug() {
		WalletForm form = ServiceRegistry.instance.getWalletForm();
		form.textAreaDebug.setText("");
		//if (WalletModel.debug) {
		form.textAreaDebug.append("\n");
		form.textAreaDebug.append(WalletMain.BUILD_DETAIL + "\n");
		form.textAreaDebug.append("Project home: https://github.com/mhisoft/eVault" + "\n");
		form.textAreaDebug.append("\n");
		form.textAreaDebug.append("java.home=" + System.getProperty("java.home") + "\n");
		form.textAreaDebug.append("java.specification.version=" + System.getProperty("java.specification.version") + "\n");
		form.textAreaDebug.append("java.vendor=" + System.getProperty("java.vendor") + "\n");
		form.textAreaDebug.append("java.vendor.url=" + System.getProperty("java.vendor.url") + "\n");
		form.textAreaDebug.append("java.version=" + System.getProperty("java.version") + "\n");
		form.textAreaDebug.append("user.home=" + System.getProperty("user.home") + "\n");
		form.textAreaDebug.append("\n");
	}



}
