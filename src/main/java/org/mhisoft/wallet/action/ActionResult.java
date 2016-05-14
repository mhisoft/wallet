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

package org.mhisoft.wallet.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ActionResult implements Serializable {

	boolean success;
	List<ActionError> errors;
	Map<String, Object> resultlMap;

	public ActionResult(boolean success) {
		this.success = success;
	}

	public void addError(int code, String errorMsg) {
		if (errors==null)
			errors = new ArrayList<>();
		ActionError error = new ActionError(code, errorMsg);
		errors.add(error);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<ActionError> getErrors() {
		return errors;
	}

	public void setErrors(List<ActionError> errors) {
		this.errors = errors;
	}

	public Map<String, Object> getResultlMap() {
		return resultlMap;
	}

	public void setResultlMap(Map<String, Object> resultlMap) {
		this.resultlMap = resultlMap;
	}
}
