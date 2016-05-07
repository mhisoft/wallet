package org.mhisoft.wallet.service;

import java.util.Map;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.wallet.view.DialogUtils;
import org.mhisoft.wallet.view.PasswordForm;

/**
 * Description:   action for creating the password.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class CreatePasswordAction {

	public void execute(Map<String, Object> params)   {
		String pass = (String)params.get("pass");
		boolean createHash = (Boolean)params.get("createHash");
		PasswordForm passwordForm  = (PasswordForm)params.get("passwordForm");

		if (createHash) {
			if (createPassword(pass)) {
				passwordForm.exitPasswordForm();
				DialogUtils.getInstance().info("Please keep this in a safe place, it can't be recovered\n"
						+ passwordForm.getUserInputPass() + ", combination:"
						+ passwordForm.getCombinationDisplay());

				//proceed to load wallet
				LoadWalletAction loadWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, LoadWalletAction.class);
				loadWalletAction.execute(null);
			}

		}
		else {
			//verify the pass
			if (verifyPassword(params)) {
				passwordForm.exitPasswordForm();
				//proceed to form


			}
			else {
				DialogUtils.getInstance().warn("Error", "Can not confirm your password. Please try again.");
			}
		}

	}

	//create the hash and save to file.
	protected void createHash(String pass) {
		try {
			String hash = HashingUtils.createHash(pass);
			ServiceRegistry.instance.getWalletModel().setPassHash(hash);
			ServiceRegistry.instance.getWalletSettings().setPassPlain(pass);
			ServiceRegistry.instance.getService(BeanType.singleton, WalletSettingsService.class)
					.saveSettingsToFile(ServiceRegistry.instance.getWalletSettings());

		} catch (HashingUtils.CannotPerformOperationException e1) {
			e1.printStackTrace();
			DialogUtils.getInstance().error("An error occurred", "Failed to hash the password:" + e1.getMessage());
		}
	}


	public boolean createPassword(String pass)   {
		ServiceRegistry.instance.getWalletSettings().setPassPlain(pass);
		createHash(pass);
		Encryptor.createInstance(pass);  ;
		return true;



	}

	public boolean verifyPassword(Map<String, Object> params)   {
		return true;
	}





}
