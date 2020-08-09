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

package org.mhisoft.wallet.model;

import java.util.Arrays;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.mhisoft.common.util.Serializer;
import org.mhisoft.common.util.security.JceEncryption;

/**
 * Description: PassCombination Encryption Adaptor
 * encrypts the  PassCombinationVO internally
 *
 * @author Tony Xue
 * @since Dec, 2017
 */
public final class PassCombinationEncryptionAdaptor extends PassCombinationVO {
	private transient Serializer<PassCombinationVO> sesrializer = new Serializer<PassCombinationVO>();
	private transient byte[] encryptedPass;
	private transient JceEncryption engine;

	public PassCombinationEncryptionAdaptor clone() {
		PassCombinationEncryptionAdaptor clone = new PassCombinationEncryptionAdaptor();
		clone.engine = this.engine;
		if (encryptedPass!=null)
			clone.encryptedPass = Arrays.copyOf(encryptedPass, encryptedPass.length);
		return clone;
	}

	public PassCombinationEncryptionAdaptor() {
		super();
		engine = JceEncryption.createEngine();
	}

	public PassCombinationEncryptionAdaptor(String pass, String combination)  {
		    this();
			PassCombinationVO internal = new PassCombinationVO();
			internal.setPass( pass );
			internal.setCombination(combination);
			saveEncryptInternal(internal);
	}

	private final  PassCombinationVO getInternal() {
		try {
			if (encryptedPass ==null)
				return null;
			byte[] b = engine.decrypt(encryptedPass);
			PassCombinationVO internal  = sesrializer.deserialize(b);
			return internal;
		} catch (GeneralSecurityException | IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveEncryptInternal(final PassCombinationVO internal) {
		try {
			byte[] b= sesrializer.serialize(internal);
			this.encryptedPass = engine.encrypt(b);
		} catch (IOException | GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPass() {
		PassCombinationVO vo = getInternal();
		return vo==null? null : vo.getPass();
	}

	@Override
	public void setPass(String pass) {
		PassCombinationVO internal =getInternal();
		if (internal==null)
			internal = new PassCombinationVO();
		internal.setPass(pass);
		saveEncryptInternal(internal);
	}

	@Override
	public String getCombination() {
		PassCombinationVO vo = getInternal();
		return vo==null? null : vo.getCombination();
	}

	@Override
	public String getSpinner1() {
		PassCombinationVO vo = getInternal();
		return vo==null? null : vo.getSpinner1();
	}


	@Override
	public String getSpinner2() {
		PassCombinationVO vo = getInternal();
		return vo==null? null : vo.getSpinner2();
	}


	@Override
	public String getSpinner3() {
		PassCombinationVO vo = getInternal();
		return vo==null? null : vo.getSpinner3();
	}


	@Override
	public void setCombination(String spinner1, String spinner2, String spinner3) {
		PassCombinationVO internal =getInternal();
		if (internal==null)
			internal = new PassCombinationVO();
		internal.setCombination(spinner1, spinner2, spinner3);
		saveEncryptInternal(internal);

	}

	@Override
	public String getPassAndCombination() {
		PassCombinationVO vo = getInternal();
		return vo.getPass()+(vo.getCombination()==null?"":vo.getCombination());
	}


}
