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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.Serializable;

import org.mhisoft.common.util.ReflectionUtil;
import org.mhisoft.common.util.Serializer;
import org.mhisoft.common.util.StringUtils;
import org.mhisoft.wallet.service.ServiceRegistry;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletItem implements Serializable, Comparable<WalletItem> {
	private static final long serialVersionUID = 1L;
	private String sysGUID;
	private ItemType type;
	private String name;
	private String URL;
	private String userName;
	private String accountNumber;
	private String password;
	private String expMonth;
	private String expYear;
	private String pin;
	private String cvc;
	private String accountType;
	private String phone;
	private String detail1;
	private String detail2;
	private String detail3;


	private String notes;
	private Timestamp createdDate;
	private Timestamp lastViewdDate;
	private Timestamp lastModifiedDate;


	//the item is serialized when writting to the vault.
	// do not include the attachment entries.
	private transient WalletItem parent;
	private transient List<WalletItem> children;
	private transient FileAccessEntry attachmentEntry;    //the attachment entry.
	private transient FileAccessEntry newAttachmentEntry; //not null when current one is replaced by a new one.

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

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getCvc() {
		return cvc;
	}

	public void setCvc(String cvc) {
		this.cvc = cvc;
	}

	public String getExpMonth() {
		return expMonth;
	}

	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}

	public String getExpYear() {
		return expYear;
	}

	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDetail1() {
		return detail1;
	}

	public void setDetail1(String detail1) {
		this.detail1 = detail1;
	}

	public String getDetail2() {
		return detail2;
	}

	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}

	public String getDetail3() {
		return detail3;
	}

	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}

	public WalletItem() {
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


	/**
	 * Compare the content
	 *
	 * @param impItem
	 * @return
	 */
	public boolean isSame(final WalletItem impItem) {
		if (impItem == null)
			return false;
		boolean isSame =  this.toStringJson().equals(impItem.toStringJson());
		if (isSame) {
			if (!this.name.equals(impItem.getName())) {
				if (this.lastModifiedDate.after(impItem.getLastModifiedDate()))
					//keep the current name. so no changes.
					return true;
				else
					return false;

			}
			else
				return true;

		}
		return false;
	}

	@Override
	public int compareTo(WalletItem o) {
		//make it sort by name
		return this.name.compareTo(o.getName());
	}


	public boolean compareContent(WalletItem that) {
		if (this == that) return true;
		if (that == null || getClass() != that.getClass()) return false;
		return sysGUID.equals(that.sysGUID) &&
				type == that.type &&
				Objects.equals(name, that.name) &&
				Objects.equals(URL, that.URL) &&
				Objects.equals(userName, that.userName) &&
				Objects.equals(accountNumber, that.accountNumber) &&
				Objects.equals(password, that.password) &&
				Objects.equals(expMonth, that.expMonth) &&
				Objects.equals(expYear, that.expYear) &&
				Objects.equals(pin, that.pin) &&
				Objects.equals(cvc, that.cvc) &&
				Objects.equals(accountType, that.accountType) &&
				Objects.equals(phone, that.phone) &&
				Objects.equals(detail1, that.detail1) &&
				Objects.equals(detail2, that.detail2) &&
				Objects.equals(detail3, that.detail3) &&
				Objects.equals(notes, that.notes) &&
				Objects.equals(createdDate, that.createdDate) &&
				Objects.equals(lastViewdDate, that.lastViewdDate) &&
				Objects.equals(lastModifiedDate, that.lastModifiedDate);
	}


	public String toStringJson() {
		return "WalletItem{" +
				//"sysGUID='" + sysGUID + '\'' +
				" type=" + type +
				//", name='" + name + '\'' +  handle separately
				", URL='" + URL + '\'' +
				", userName='" + userName + '\'' +
				", accountNumber='" + accountNumber + '\'' +
				", password='" + password + '\'' +
				", notes='" + notes + '\'' +
//				", createdDate=" + createdDate +
//				", lastViewdDate=" + lastViewdDate +
//				", lastModifiedDate=" + lastModifiedDate +
				", pin='" + pin + '\'' +
				", cvc='" + cvc + '\'' +
				", expMonth='" + expMonth + '\'' +
				", expYear='" + expYear + '\'' +
				", accountType='" + accountType + '\'' +
				", phone='" + phone + '\'' +
				", detail1='" + detail1 + '\'' +
				", detail2='" + detail2 + '\'' +
				", detail3='" + detail3 + '\'' +
				'}';
	}


	protected void mergeField(WalletItem item1, WalletItem item2, String fldName) throws NoSuchFieldException {

		String fld1 = (String) ReflectionUtil.getFieldValue(item1, fldName);
		String fld2 = (String) ReflectionUtil.getFieldValue(item2, fldName);

		String mergedFld ;
		boolean modified = false;

		if (!StringUtils.hasValue(fld1))   {
			mergedFld = fld2;
			modified = true;
		}
		else {
			if (!StringUtils.hasValue(fld2))  {
				mergedFld = fld1;
			}
			else {
				if (!fld1.equals(fld2))    {
				    int k = 	fld1.lastIndexOf("/");
					if(k>0)  {
						String s = fld1.substring(k+1, fld1.length()) ;
						if (!s.equals(fld2))   {
							mergedFld = fld1 + "/" + fld2 ;
							modified = true;
						}
						else
							mergedFld = fld1;
					}
					else {
						mergedFld = fld1 + "/" + fld2;
						modified = true;
					}
				}
				else
					mergedFld = fld1;
			}

		}


		if (modified)
		     ReflectionUtil.setFieldValue(item1, fldName, mergedFld);
	}


	/**
	 * merge the fields from the target item during import.
	 *
	 * @param item
	 */
	public void mergeFrom(final WalletItem item) throws NoSuchFieldException {
	    if (!this.name.equals(item.getName())) {
			if (this.lastModifiedDate.before(item.getLastModifiedDate()))
				this.name = item.getName();
		}

		mergeField(this, item, "URL");
		mergeField(this, item, "accountNumber");
		mergeField(this, item, "accountType");
		mergeField(this, item, "expYear");
		mergeField(this, item, "expMonth");
		mergeField(this, item, "password");
		mergeField(this, item, "pin");
		mergeField(this, item, "cvc");
		mergeField(this, item, "phone");
		mergeField(this, item, "notes");
		mergeField(this, item, "detail1");
		mergeField(this, item, "detail2");
		mergeField(this, item, "detail3");


		this.lastModifiedDate = new Timestamp(System.currentTimeMillis());

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

	/**
	 * Used in search. include only the searchable fields. password, pin, cvc are not searchable.
	 *
	 * @param filter
	 * @return
	 */

	public boolean isMatch(String filter) {
		//SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		String regex = ".*" + filter + ".*";
		Pattern p = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE);

		return
				isJavaPatternMatch(p, name)
						|| isJavaPatternMatch(p, URL)
						|| isJavaPatternMatch(p, accountNumber)
						|| isJavaPatternMatch(p, expMonth)
						|| isJavaPatternMatch(p, expYear)
						|| isJavaPatternMatch(p, notes)
						|| isJavaPatternMatch(p, detail1)
						|| isJavaPatternMatch(p, detail2)
						|| isJavaPatternMatch(p, detail3)
						|| isJavaPatternMatch(p, phone)

				;
	}

	public FileAccessEntry getOrCreateAttachmentEntry() {
		if (attachmentEntry==null) {
			this.attachmentEntry =  new FileAccessEntry(this.sysGUID);
		}
		return attachmentEntry;
	}

	public void setAttachmentEntry(FileAccessEntry attachmentEntry) {
		this.attachmentEntry = attachmentEntry;
	}

	public void setNewAttachmentEntry(FileAccessEntry newAttachmentEntry) {
		this.newAttachmentEntry = newAttachmentEntry;
	}

	public void addOrReplaceAttachment(String fname) {
		FileAccessEntry attachmentEntry =  getOrCreateAttachmentEntry();

		//file to updated file is still creating new.
		// encContent means loaded from store, will be update if user attach a new image.

		if ( attachmentEntry.getEncSize()>0) { //
			//had a attachment, replacing.
			if (newAttachmentEntry==null)
				newAttachmentEntry =   new FileAccessEntry(this.sysGUID);
			newAttachmentEntry.setFileName(fname);
			attachmentEntry.setNewEntry(newAttachmentEntry);
			attachmentEntry.setAccessFlag(FileAccessFlag.Update);
			newAttachmentEntry.setAccessFlag(FileAccessFlag.Update);
		}
		else {
			attachmentEntry.setFileName(fname);
			attachmentEntry.setAccessFlag(FileAccessFlag.Create);
		}
		setLastModifiedDate(new Timestamp(System.currentTimeMillis()));

	}

	public void removeAttachment()  {
	   if (this.attachmentEntry!=null) {
		   attachmentEntry.setAccessFlag(FileAccessFlag.Delete);
		   newAttachmentEntry = null;
		   setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
	   }
	}


	public FileAccessEntry getAttachmentEntry() {
		return attachmentEntry;
	}

	public FileAccessEntry getNewAttachmentEntry() {
		return newAttachmentEntry;
	}


	/**
	 * return either the entry or the updated one
	 * @return
	 */
	public FileAccessEntry getFileAccessEntryForDisplay() {
		if (attachmentEntry==null || attachmentEntry.getAccessFlag()==null)
			return attachmentEntry;

		return FileAccessFlag.Update==attachmentEntry.getAccessFlag() ?  newAttachmentEntry : attachmentEntry;
	}


	public boolean hasAttachmentToSave() {
		return attachmentEntry.getAccessFlag()!=null; //create, update, delete
	}



	public WalletItem clone() {
		try {
			Serializer<WalletItem> serializer = new Serializer<WalletItem>();
			WalletItem ret = serializer.deserialize(serializer.serialize(this));

			//this part is not really a clone. point to the same Attachment Entry for exporting is good enough.
			if (this.getAttachmentEntry()!=null ) {
				ret.setAttachmentEntry(  this.attachmentEntry );
				ret.setNewAttachmentEntry( this.newAttachmentEntry );
			}

			return ret;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("cloneItem() failed", e);
		}
	}


	public WalletItem findItemInModel() {
		for (WalletItem walletItem : ServiceRegistry.instance.getWalletModel().getItemsFlatList()) {
			if (walletItem.getSysGUID().equals(getSysGUID()) || walletItem.getName().equalsIgnoreCase(getName()))
					return walletItem;

		}
		return null;

	}


	public WalletItem findItemInModel(final WalletModel model) {
		for (WalletItem walletItem : model.getItemsFlatList()) {
			if (walletItem.getSysGUID().equals(getSysGUID()) || walletItem.getName().equalsIgnoreCase(getName()))
				return walletItem;

		}
		return null;

	}
}
