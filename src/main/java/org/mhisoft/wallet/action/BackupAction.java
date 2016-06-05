package org.mhisoft.wallet.action;

import java.io.File;
import java.io.IOException;

import org.mhisoft.common.logger.Loggerfactory;
import org.mhisoft.common.logger.MHILogger;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.SystemSettings;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.BeanType;
import org.mhisoft.wallet.service.ServiceRegistry;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Jun, 2016
 */
public class BackupAction implements Action {

	private static final MHILogger logger = Loggerfactory.getLogger(BackupAction.class,
			SystemSettings.loggerLevel);



	@Override
	public ActionResult execute(Object... params) {

		if (ServiceRegistry.instance.getWalletModel().isModified()) {
			SaveWalletAction saveWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, SaveWalletAction.class);
			saveWalletAction.save(WalletSettings.getInstance().getLastFile());
		}



		String[] parts = FileUtils.splitFileParts(WalletSettings.getInstance().getLastFile());


		StringBuilder targetFile = new StringBuilder(parts[0]);
		targetFile.append(File.separator).append(parts[1])  ;
		targetFile.append("-") .append(System.currentTimeMillis() ) ;
		targetFile.append(".")  ;
		targetFile.append(parts[2])  ;  //ext

		try {
			FileUtils.copyFile( new File(WalletSettings.getInstance().getLastFile()), new File(targetFile.toString()));
			DialogUtils.getInstance().info("The data file is backed up at :" + targetFile.toString());
		} catch (IOException e) {
			logger.error(e.toString());
			DialogUtils.getInstance().error(e.getMessage());
		}


		return null;
	}
}
