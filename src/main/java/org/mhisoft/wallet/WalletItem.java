package org.mhisoft.wallet;

import java.sql.Timestamp;
import java.util.Map;
import java.io.Serializable;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class WalletItem implements Serializable {
	private String sysGUID;
    private ItemType type;
	private String name;
	private String URL;
	private String userName;
	private String accountNumber;
	private String expirationYear;
	private String expirationMonth;
	private String password;
	private Map<String, String> detailFieldsMap;
	private String notes;
	private Timestamp createdDate;
	private Timestamp lastViewdDate;
	private Timestamp lastModifiedDate;

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

	public Map<String, String> getDetailFieldsMap() {
		return detailFieldsMap;
	}

	public void setDetailFieldsMap(Map<String, String> detailFieldsMap) {
		this.detailFieldsMap = detailFieldsMap;
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
		this.type = type;
		this.name = name;
		this.createdDate = new Timestamp(System.currentTimeMillis());
	}

	@Override
	public String toString() {
		return name;
	}
}
