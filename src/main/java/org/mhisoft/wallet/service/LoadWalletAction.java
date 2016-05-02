package org.mhisoft.wallet.service;

import java.util.Map;
import java.io.File;

import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description: Action for loading the wallet.
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
		String  fileName;
		if (params==null)
			fileName = WalletSettings.defaultWalletFile;
		else
			fileName = (String)params.get("filename") ;


		if (new File(fileName).isFile()) {
			//read tree from the existing file
			getWalletForm().getModel().setItemsFlatList(getWalletService().readFromFile(fileName));
		}
		else {
			//create an empty tree with one root.
			getWalletForm().getModel().setupEmptyWalletData();
		}
		getWalletForm().loadTree();

	}

}
