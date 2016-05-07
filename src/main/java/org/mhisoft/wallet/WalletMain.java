package org.mhisoft.wallet;

import java.io.File;

import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.FileContentVO;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.service.WalletSettingsService;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description: main class is the entry point.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class WalletMain {


	public static void main(String[] args) {
		//read the settings if exists
		WalletSettingsService walletSettingsService = ServiceRegistry.instance.getService(
				BeanType.singleton, WalletSettingsService.class);
		walletSettingsService.readSettingsFromFile();



		WalletForm form = ServiceRegistry.instance.getWalletForm();

		form.init();
	}



	protected void openWalletFile(String  fileName) {

		if (fileName==null)
			fileName = WalletSettings.defaultWalletFile;

		WalletModel model = ServiceRegistry.instance.getWalletForm().getModel();

		if (new File(fileName).isFile()) {
			//read tree from the existing file
			FileContentVO vo=ServiceRegistry.instance.getWalletService().readFromFile(fileName);
			model.setItemsFlatList(vo.getWalletItems());
			model.setPassHash(vo.getPassHash());
		}
		else {
			//create an empty tree with one root.
			model.setupEmptyWalletData();
		}

	}

}
