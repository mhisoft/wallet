package org.mhisoft.wallet.model;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
public class PassCombinationVO {
	String pass;
	String combination;

	public PassCombinationVO() {
	}

	public PassCombinationVO(String pass, String combination) {
		this.pass = pass;
		this.combination = combination;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public String getPassAndCombination() {
		return pass+(combination==null?"":combination);

	}


}
