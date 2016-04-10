package org.mhisoft.wallet;

import org.mhisoft.wallet.service.BeanType;
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

		WalletForm form = ServiceRegistry.instance.getService(
				BeanType.singleton, WalletForm.class);

		form.init();
	}

}
