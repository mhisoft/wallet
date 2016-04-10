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

package org.mhisoft.wallet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mhisoft.wallet.model.PasswordValidator;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class PasswordValidatorTest {
	private static PasswordValidator passwordValidator;

	@BeforeClass
	public static void initData(){
		passwordValidator = new PasswordValidator();
	}


	@Test
	public void ValidPasswordTest() {
		String[] passwords =new String[] { "mkyong1A@", "mkYOn12$", "mkyoNg12*", "tonY34KL."}  ;
		for(String temp : passwords){
			boolean valid = passwordValidator.validate(temp);
			System.out.println("Password is valid : " + temp + " , " + valid);
			Assert.assertEquals(true, valid);
		}

	}

	@Test
	public void InValidPasswordTest() {
		String[] passwords =new String[] {
				"mY1A@","mkyong12@",
				"mkyonG$$","MKYONG12$", "{}P:}{P}P"
		}  ;

		for(String temp : passwords){
			boolean valid = passwordValidator.validate(temp);
			System.out.println("Password is valid : " + temp + " , " + valid);
			Assert.assertEquals(false, valid);
		}
	}
}
