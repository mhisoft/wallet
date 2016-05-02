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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:  PasswordValidator
 *
 * @author Tony Xue
 * @since Mar, 2016
 */

//use it as singleton.
public class PasswordValidator{

	private Pattern pattern;
	private Matcher matcher;

	/*
	(			# Start of group
  (?=.*\d)		#   must contains one digit from 0-9
  (?=.*[a-z])		#   must contains one lowercase characters
  (?=.*[A-Z])		#   must contains one uppercase characters
  (?=.*[@#$%])		#   must contains one special symbols in the list "@#$%"

  ~`!@#$%^&*()-_+={}[]|\;:"<>,./?

              .		#     match anything with previous condition checking
                {6,20}	#        length at least 6 characters and maximum of 20
)			# End of group
	 */
	private static final String PASSWORD_PATTERN ="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\Q~`!@#$%^&*()-_+={}[]|\\;:\"<>,./?\\E]).{8,99})";

	public PasswordValidator(){
		pattern = Pattern.compile(PASSWORD_PATTERN);
	}

	/**
	 * Validate password with regular expression
	 * @param password password for validation
	 * @return true valid password, false invalid password
	 */
	public boolean validate(final String password){

		matcher = pattern.matcher(password);
		return matcher.matches();

	}
}