package org.mhisoft.wallet.service;

import java.util.Timer;
import java.util.TimerTask;

import org.mhisoft.wallet.SystemSettings;
import org.mhisoft.wallet.action.ActionResult;
import org.mhisoft.wallet.action.CloseWalletAction;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class IdleTimerService {

	protected Timer t;
	protected long checkPeriod = SystemSettings.debug ? 15000 : 60000;

	protected long startTime;

	public static  IdleTimerService instance = new IdleTimerService();

	private IdleTimerService() {
		//none
	}

	public void start() {

		 if (SystemSettings.debug)
			 WalletSettings.getInstance().setIdleTimeout(180);  //3 min.

		if (t!=null)
			t.cancel();

		t = new Timer(true);
		startTime = System.currentTimeMillis();

		t.schedule(new TimerTask() {
			@Override
			public void run() {
				long elapsed = System.currentTimeMillis() - startTime;
				if (elapsed > WalletSettings.getInstance().getIdleTimeout() * 1000) {
					//times out , close the wallet file
					t.cancel();

					CloseWalletAction closeWalletAction = ServiceRegistry.instance.getService(BeanType.prototype, CloseWalletAction.class);
					ActionResult r = closeWalletAction.execute(Boolean.TRUE); //close the wallet file quietly

					//close the tree view.
					ServiceRegistry.instance.getWalletForm().closeView();

					//close the wallet
					DialogUtils.getInstance().info("<html>Closing the wallet as it has been idling too long.<br>You can use the Open menu to open it again.</html>");

				}
			}
		}, 0, checkPeriod);
	}


	//use activity checks in, reset the  countdown
	public void checkIn() {
		startTime = System.currentTimeMillis();
	}


}
