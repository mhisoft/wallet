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

import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class DataServiceFactory {


	public static DataService createDataService(int version) {

		if (version==10 || version==11)
			return ServiceRegistry.instance.getService(BeanType.singleton, DataServiceImplv10.class);
		else if (version==12)
			return ServiceRegistry.instance.getService(BeanType.singleton, DataServiceImplv12.class);
		else if (version==13)
			return ServiceRegistry.instance.getService(BeanType.singleton, DataServiceImplv13.class);
		else if (version==14)
			return ServiceRegistry.instance.getService(BeanType.singleton, DataServiceImplv14.class);
		else if (version==20)
			return ServiceRegistry.instance.getService(BeanType.singleton, DataServiceImplv20.class);

		throw new RuntimeException("version " + version + " not supported.");

	}

	/**
	 * Create a latest version of the data service.
	 * @return
	 */
	public static DataService createDataService() {
		return createDataService(WalletModel.LATEST_DATA_VERSION);
	}
}
