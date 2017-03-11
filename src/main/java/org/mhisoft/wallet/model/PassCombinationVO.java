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
	String spinner1, spinner2, spinner3;

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
		//the combination field is only used in unit tests.
		return combination==null?(spinner2 + spinner1 + spinner3):combination;
	}


	public String getSpinner1() {
		return spinner1;
	}

	public void setSpinner1(String spinner1) {
		this.spinner1 = spinner1;
	}

	public String getSpinner2() {
		return spinner2;
	}

	public void setSpinner2(String spinner2) {
		this.spinner2 = spinner2;
	}

	public String getSpinner3() {
		return spinner3;
	}

	public void setSpinner3(String spinner3) {
		this.spinner3 = spinner3;
	}

	public void setCombination(String spinner1, String spinner2, String spinner3) {
		this.spinner1 = spinner1;
		this.spinner2 = spinner2;
		this.spinner3 = spinner3;
	}


	public String getPassAndCombination() {
		return pass+(getCombination()==null?"":getCombination());
	}





}
