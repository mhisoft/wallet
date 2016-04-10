package org.mhisoft.wallet.service;

import java.util.Map;

import org.mhisoft.wallet.view.WalletForm;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class LoadWalletAction {


	public WalletForm getWalletForm() {
		return  ServiceRegistry.instance.getService(BeanType.singleton, WalletForm.class);
	}

	public WalletService getWalletService() {
		return  ServiceRegistry.instance.getService(BeanType.singleton, WalletService.class);
	}


	public void execute(Map<String, Object> params) {
		String  fileName = (String)params.get("filename") ;
		getWalletForm().getModel().setItemsFlatList(getWalletService().readFromFile(fileName));
		getWalletForm().loadTree();

	}

}
