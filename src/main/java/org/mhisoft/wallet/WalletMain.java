package org.mhisoft.wallet;

import java.io.File;

import org.mhisoft.common.event.EventDispatcher;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.FileContentHeader;
import org.mhisoft.wallet.service.ModelChangeEventListener;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.service.UserActivityCheckinListener;
import org.mhisoft.wallet.service.ViewModeChangeEventListener;
import org.mhisoft.wallet.service.WalletSettingsService;
import org.mhisoft.wallet.view.PasswordForm;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description: main class is the entry point.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class WalletMain {

	public static final String version = "1.0.6";
	public static final String build = "641";
	public static final String BUILD_DETAIL = "MHISoft eVault " + version +" build " +  build;


	public static void main(String[] args) {

		WalletMain app = new WalletMain();
		//read the settings if exists
		WalletSettingsService walletSettingsService = ServiceRegistry.instance.getService(
				BeanType.singleton, WalletSettingsService.class);
		walletSettingsService.readSettingsFromFile();

		/*initialized*/
		HashingUtils.init();

		app.registerEventListeners();

		WalletForm form = ServiceRegistry.instance.getWalletForm();
		//read the last wallet file header if can.
		app.prepareModel();

		form.init();


		//Open old wallet file or create new password.
		String title ;
		if (WalletSettings.getInstance().getLastFile()==null) {
			title = "Creating a new wallet";
			WalletSettings.getInstance().setLastFile(WalletSettings.defaultWalletFile);
		}
		else
			title ="Opening file:" + WalletSettings.getInstance().getLastFile();

		PasswordForm passwordForm = new PasswordForm(title);
		passwordForm.showPasswordForm(form, null);


	}

	//set hash into model                                                     f
	// or load empty data for a new wallet.
	protected void prepareModel() {


		String fileName = WalletSettings.getInstance().getLastFile();

		WalletModel model = ServiceRegistry.instance.getWalletForm().getModel();

		if (fileName!=null && new File(fileName).isFile()) {
			WalletSettings.getInstance().setLastFile(fileName);
			FileContentHeader header = ServiceRegistry.instance.getWalletService().readHeader(fileName, true);
			model.setPassHash(header.getPassHash());
		} else {
			//create an empty tree with one root.
			model.setupEmptyWalletData(null);
		}

	}

	protected void registerEventListeners() {
		EventDispatcher.instance.registerListener(EventType.UserCheckInEvent,  new UserActivityCheckinListener());
		EventDispatcher.instance.registerListener(EventType.ModelChangeEvent,  new ModelChangeEventListener());
		EventDispatcher.instance.registerListener(EventType.ViewModeChangeEvent,  new ViewModeChangeEventListener());
	}

}
