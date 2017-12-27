package org.mhisoft.wallet.service;

import java.util.Timer;
import java.util.TimerTask;

import org.mhisoft.wallet.SystemSettings;
import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.action.CloseWalletAction;
import org.mhisoft.wallet.model.WalletSettings;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class IdleTimerService {

	protected Timer t;
	protected long checkPeriod = SystemSettings.isDevMode ? 15000 : 60000;

	protected long startTime;

	public static  IdleTimerService instance = new IdleTimerService();

	private IdleTimerService() {
		//none
	}

	public void start() {


		if (t!=null)
			t.cancel();

		t = new Timer(true);
		startTime = System.currentTimeMillis();


		t.schedule(new TimerTask() {
			@Override
			public void run() {
				long elapsed = System.currentTimeMillis() - startTime;
				//need to load the latest timeout config. bug fix : when value changed on the UI. it is not reflected.
				long idleTimeoutInMilliSec = WalletSettings.getInstance().getIdleTimeout() * 60* 1000;
				if (elapsed > idleTimeoutInMilliSec ) {
					//times out , close the wallet file
					t.cancel();

//
//					SwingUtilities.invokeLater(new Runnable() {
//
//						@Override
//						public void run() {

							CloseWalletAction closeWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CloseWalletAction.class);
							ActionResult r = closeWalletAction.execute(Boolean.TRUE); //close the wallet file quietly

							//close the tree view.
							ServiceRegistry.instance.getWalletForm().resetForm();
					ServiceRegistry.instance.getWalletForm().showMessage("The vault is closed because it has been idling for too long.", false);
//
//						}
//					});




					//close the wallet
					//DialogUtils.getInstance().info("<html>Closing the wallet as it has been idling too long.<br>You can use the Open menu to open it again.</html>");

				}
			}
		}, 0, checkPeriod);
	}


	//use activity checks in, reset the  countdown
	public void checkIn() {
		startTime = System.currentTimeMillis();
	}


}
