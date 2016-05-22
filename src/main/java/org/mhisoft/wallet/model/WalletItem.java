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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Serializable;

import org.mhisoft.common.util.StringUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sysGUID;
	private ItemType type;
	private String name;
	private String URL;
	private String userName;
	private String accountNumber;
	private String expirationYear;
	private String expirationMonth;
	private String password;
	private String notes;
	private Timestamp createdDate;
	private Timestamp lastViewdDate;
	private Timestamp lastModifiedDate;


	private String pin;
	private String expMonth;
	private String expYear;
	private String accountType;
	private String phone;
	private String detail1;
	private String detail2;
	private String detail3;


	private transient WalletItem parent;
	private transient List<WalletItem> children;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WalletItem that = (WalletItem) o;

		return sysGUID.equals(that.sysGUID);

	}

	@Override
	public int hashCode() {
		return sysGUID.hashCode();
	}

	public String getSysGUID() {
		return sysGUID;
	}

	public void setSysGUID(String sysGUID) {
		this.sysGUID = sysGUID;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getLastViewdDate() {
		return lastViewdDate;
	}

	public void setLastViewdDate(Timestamp lastViewdDate) {
		this.lastViewdDate = lastViewdDate;
	}

	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public WalletItem(ItemType type, String name) {
		this.sysGUID = StringUtils.getGUID();
		this.type = type;
		this.name = name;
		this.createdDate = new Timestamp(System.currentTimeMillis());
	}

	public WalletItem getParent() {
		return parent;
	}

	public void setParent(WalletItem parent) {
		this.parent = parent;
	}

	public List<WalletItem> getChildren() {
		return children;
	}

	public void setChildren(List<WalletItem> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isSame(final WalletItem item) {
		if (item == null)
			return false;
		return this.toStringJson().equals(item.toStringJson());
	}


	public String toStringJson() {
		return "WalletItem{" +
				"sysGUID='" + sysGUID + '\'' +
				", type=" + type +
				", name='" + name + '\'' +
				", URL='" + URL + '\'' +
				", userName='" + userName + '\'' +
				", accountNumber='" + accountNumber + '\'' +
				", expirationYear='" + expirationYear + '\'' +
				", expirationMonth='" + expirationMonth + '\'' +
				", password='" + password + '\'' +
				", notes='" + notes + '\'' +
				", createdDate=" + createdDate +
				", lastViewdDate=" + lastViewdDate +
				", lastModifiedDate=" + lastModifiedDate +
				", pin='" + pin + '\'' +
				", expMonth='" + expMonth + '\'' +
				", expYear='" + expYear + '\'' +
				", accountType='" + accountType + '\'' +
				", phone='" + phone + '\'' +
				", detail1='" + detail1 + '\'' +
				", detail2='" + detail2 + '\'' +
				", detail3='" + detail3 + '\'' +
				'}';
	}


	public boolean hasChildren() {
		return getChildren() != null && getChildren().size() > 0;
	}

	public void addChild(final WalletItem childItem) {
		if (this.getType() != ItemType.category)
			throw new RuntimeException("Can't add a child to a none category node. parent: " + this.toString()
					+ ", child:" + childItem.toString());
		if (this.children == null)
			this.children = new ArrayList<>();
		this.children.add(childItem);
		childItem.parent = this;
	}


	public void removeChild(final WalletItem childItem) {
		if (this.getType() != ItemType.category)
			throw new RuntimeException("Not a  category node: " + this.toString());
		if (children != null) {
			this.children.remove(childItem);
			childItem.setParent(null);
		}
	}


	private boolean isJavaPatternMatch(Pattern p, String input) {
		if (input != null) {
			Matcher m = p.matcher(input);
			return m.find();
		}
		return false;
	}


	public boolean isMatch(String filter) {
		//SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		String regex = ".*" + filter + ".*";
		Pattern p = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE);

		return
				isJavaPatternMatch(p, name)
						|| isJavaPatternMatch(p, URL)
						|| isJavaPatternMatch(p, accountNumber)
						|| isJavaPatternMatch(p, expirationMonth)
						|| isJavaPatternMatch(p, expirationYear)
						|| isJavaPatternMatch(p, notes)
						|| isJavaPatternMatch(p, detail1)
						|| isJavaPatternMatch(p, detail2)
						|| isJavaPatternMatch(p, detail3)
						|| isJavaPatternMatch(p, phone)

				;
	}

}
