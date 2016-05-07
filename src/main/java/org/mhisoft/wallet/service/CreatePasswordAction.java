package org.mhisoft.wallet.service;

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
public class CreatePasswordAction implements Action {

	@Override
	public void execute(Object... params)    {
		String pass = (String)params[0];
		boolean createHash = (Boolean)params[1];
		PasswordForm passwordForm  = (PasswordForm)params[2];

		if (createHash) {
			if (createPassword(pass)) {
				passwordForm.exitPasswordForm();
				DialogUtils.getInstance().info("Please keep this in a safe place, it can't be recovered\n"
						+ passwordForm.getUserInputPass() + ", combination:"
						+ passwordForm.getCombinationDisplay());

				//proceed to load wallet
				LoadWalletAction loadWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, LoadWalletAction.class);
				loadWalletAction.execute();
			}

		}
		else {
			//verify the pass
			if (verifyPassword(pass )) {
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

	public boolean verifyPassword(String pass)   {
		return true;
	}





}
