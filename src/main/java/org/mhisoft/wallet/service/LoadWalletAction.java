package org.mhisoft.wallet.service;

import java.io.File;

import org.mhisoft.wallet.model.WalletSettings;

/**
 * Description: Action for loading the wallet.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class LoadWalletAction implements Action {





	@Override
	public void execute(Object... params) {
		String  fileName;
		if (params==null)
			fileName = WalletSettings.defaultWalletFile;
		else
			fileName = (String)params[0] ;


		if (new File(fileName).isFile()) {
			//read tree from the existing file
			FileContentVO vo= ServiceRegistry.instance.getWalletService().readFromFile(fileName);
			ServiceRegistry.instance.getWalletModel().setItemsFlatList(vo.getWalletItems());
			ServiceRegistry.instance.getWalletModel().setPassHash(vo.getPassHash());
		}
		else {
			//create an empty tree with one root.
			ServiceRegistry.instance.getWalletForm().getModel().setupEmptyWalletData();
		}
		ServiceRegistry.instance.getWalletForm().loadTree();

	}

}
