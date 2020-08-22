package org.mhisoft.wallet.action;

import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;

import org.mhisoft.common.util.FileUtils;
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


	private static final Logger logger = Logger.getLogger(BackupAction.class.getName());




	@Override
	public ActionResult execute(Object... params) {

		if (ServiceRegistry.instance.getWalletModel().isModified()) {
			SaveWalletAction saveWalletAction = ServiceRegistry.instance.getService(BeanType.singleton, SaveWalletAction.class);
			saveWalletAction.saveVault(WalletSettings.getInstance().getLastFile());
		}



		String[] parts = FileUtils.splitFileParts(WalletSettings.getInstance().getLastFile());
		String targetAttachmeSoteName ;

		StringBuilder targetFile = new StringBuilder(parts[0]);
		targetFile.append(File.separator).append(parts[1])  ;
		targetFile.append("-") .append(System.currentTimeMillis() ) ;
		targetFile.append("-") .append( "BACKUP" ) ;
		targetAttachmeSoteName = targetFile.toString();

		targetFile.append(".")  ;
		targetFile.append(parts[2])  ;  //ext
		targetAttachmeSoteName = targetAttachmeSoteName + "_attachments." +parts[2];


		try {
			FileUtils.copyFile( new File(WalletSettings.getInstance().getLastFile()), new File(targetFile.toString()));
			//attaments is optional so may not have it. 
			File attFile = new File( WalletSettings.getInstance().getAttachmentStoreFileName());
			if (attFile.exists()) {
				FileUtils.copyFile(attFile, new File(targetAttachmeSoteName));
			}
			DialogUtils.getInstance().info("The data file is backed up at :" + targetFile.toString());
		} catch (IOException e) {
			logger.severe(e.toString());
			DialogUtils.getInstance().error(e.getMessage());
		}


		return null;
	}
}
