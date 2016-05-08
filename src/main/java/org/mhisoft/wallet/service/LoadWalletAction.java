package org.mhisoft.wallet.service;

import java.io.File;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.wallet.model.WalletSettings;

/**
 * Description: Action for loading the wallet.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class LoadWalletAction implements Action {

	@Override
	public ActionResult execute(Object... params) {
		String pass = (String)params[0] ;

		String  fileName;
		if (params.length==1)
			fileName = WalletSettings.defaultWalletFile;
		else
			fileName = (String)params[1] ;


		ServiceRegistry.instance.getWalletSettings().setPassPlain(pass);
		Encryptor.createInstance(pass);


		if (new File(fileName).isFile()) {
			//read tree from the existing file
			FileContent vo= ServiceRegistry.instance.getWalletService().readFromFile(fileName);
			ServiceRegistry.instance.getWalletModel().setItemsFlatList(vo.getWalletItems());
			ServiceRegistry.instance.getWalletModel().setPassHash(vo.getPassHash());
		}
		else {
			//new file, needs to be saved on close.
			ServiceRegistry.instance.getWalletModel().setModified(true);
		}
		ServiceRegistry.instance.getWalletForm().loadTree();
		return new ActionResult(true);

	}

}
