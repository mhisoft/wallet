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


		 //backupAction
		String sourceFile = WalletSettings.getInstance().getLastFile() ;
		int k = sourceFile.lastIndexOf(File.separator) ;
		String dir = WalletSettings.userHome;
		String fileName;
		String fileExt;
		if (k>-1) {
			dir = sourceFile.substring(0, k);                         // no slash at the end
			fileName = sourceFile.substring(k + 1, sourceFile.length());
		}
		else
			fileName =  sourceFile;


		String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
		fileName =  tokens[0];

		StringBuilder targetFile = new StringBuilder(dir);
		targetFile.append(File.separator).append(fileName)  ;
		targetFile.append("-") .append(System.currentTimeMillis() ) ;
		targetFile.append(".")  ;
		targetFile.append(tokens[1])  ;

		try {
			FileUtils.copyFile( new File(sourceFile), new File(targetFile.toString()));
			DialogUtils.getInstance().info("The data file is backed up at :" + targetFile.toString());
		} catch (IOException e) {
			logger.error(e.toString());
			DialogUtils.getInstance().error(e.getMessage());
		}


		return null;
	}
}
