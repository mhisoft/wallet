package org.mhisoft.wallet;

import java.io.File;

import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.FileContentHeader;
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
	public static final String version = "1.0";
	public static final String build = "";


	public static void main(String[] args) {

		WalletMain app = new WalletMain();
		//read the settings if exists
		WalletSettingsService walletSettingsService = ServiceRegistry.instance.getService(
				BeanType.singleton, WalletSettingsService.class);
		walletSettingsService.readSettingsFromFile();

		WalletForm form = ServiceRegistry.instance.getWalletForm();
		app.openWalletFile(null);
		form.init();
	}



	protected void openWalletFile(String  fileName) {

		if (fileName==null)
			fileName = WalletSettings.defaultWalletFile;

		WalletModel model = ServiceRegistry.instance.getWalletForm().getModel();

		if (new File(fileName).isFile()) {
			FileContentHeader header=ServiceRegistry.instance.getWalletService().readHeader(fileName, true);
			model.setPassHash(header.getPassHash());
		}
		else {
			//create an empty tree with one root.
			model.setupEmptyWalletData();
		}

	}

}
