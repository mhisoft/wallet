package org.mhisoft.wallet.service;

import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;

import org.mhisoft.common.util.StringUtils;
import org.mhisoft.common.util.security.HashingUtils;
import org.mhisoft.common.util.security.PBEEncryptor;
import org.mhisoft.wallet.model.FileAccessEntry;
import org.mhisoft.wallet.model.FileAccessFlag;
import org.mhisoft.wallet.model.FileAccessTable;
import org.mhisoft.wallet.model.ItemType;
import org.mhisoft.wallet.model.PassCombinationVO;
import org.mhisoft.wallet.model.WalletItem;
import org.mhisoft.wallet.model.WalletModel;
import org.mhisoft.wallet.view.DialogUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class WalletService {

	private static final Logger logger = Logger.getLogger(WalletService.class.getName());

	AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);


	/**
	 * Load by reading the vault data file.
	 * @param filename
	 * @param encryptor
	 * @return
	 */
	public StoreVO loadVault(final String filename, final PBEEncryptor encryptor) {
		FileContentHeader header = readHeader(filename, true);
		DataService ds = DataServiceFactory.createDataService(header.getVersion());
		StoreVO ret =  ds.readFromFile(filename, encryptor);
		String attFileName = attachmentService.getAttachmentFileName(filename);
		FileAccessTable t = attachmentService.read(attFileName, encryptor);
		if (t!=null) {
			for (FileAccessEntry entry : t.getEntries()) {
				WalletItem item = ret.getWalletItem(entry.getGUID());
				if (item!=null) {
					item.setAttachmentEntry(entry);
					item.setNewAttachmentEntry(null);
				}
			}
			ret.setDeletedEntriesInStore(t.getDeletedEntries());
		}

		return ret;


	}


	/**
	 * Load the model by reading the vault data file.
	 * @param vaultFileName
	 * @param encryptor
	 * @return a new model.
	 */
	public WalletModel loadVaultIntoModel(final String vaultFileName, final PBEEncryptor encryptor) {
		StoreVO vo = loadVault(vaultFileName, encryptor);
		WalletModel model = new WalletModel();
		model.setPassHash(vo.getHeader().getPassHash());
		model.setCombinationHash(vo.getHeader().getCombinationHash());
		model.setDataFileVersion(vo.getHeader().getVersion());

		model.setEncryptor(encryptor);
		model.setItemsFlatList(vo.getWalletItems());
		model.buildTreeFromFlatList();
		model.setVaultFileName(vaultFileName);
		return model;

	}


	/**
	 * Save the vault with the latest version format.
	 * The attachment data store will be ugpraded if needed.
	 * @param filename main store filename.
	 * @param model the model to be saved.
	 * @param encryptor encrypor to use for write the new store.
	 */
	public void saveVault(final String filename, final WalletModel model, final PBEEncryptor encryptor) {

		for (WalletItem item : model.getItemsFlatList()) {
			int k = item.getName().indexOf("(*)");
			if (k>0) {
				item.setName(item.getName().substring(0, k));
			}
		}


		//save with the latest version of data services.
		DataServiceFactory.createDataService().saveToFile(filename, model, encryptor);

		/* save attachments. */
		AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);
		//upgrade the current store to the lates first.
		if (model.getCurrentDataFileVersion()!=WalletModel.LATEST_DATA_VERSION) {
			upgradeAttachmentStore(filename, model, encryptor);
		}
		else {
			attachmentService.saveAttachments(attachmentService.getAttachmentFileName(filename), model, encryptor);
		}

	}

	/**
	 * Save the model to a new exported store.
	 * And if there are attachments on the item, we need to read the content out from the old attachment store and transfer to a new one.
	 * @param expVaultName
	 * @param expModel
	 */
	public void exportModel(
			final String existingVaultFileName,
			final String expVaultName
			, final WalletModel model
			, final WalletModel expModel) {


		DataServiceFactory.createDataService().saveToFile(expVaultName, expModel, expModel.getEncryptor());

		//save attachments.
		AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);

		String expAttStoreName = attachmentService.getAttachmentFileName(expVaultName);

		if (!new File(expAttStoreName).exists()) {

			attachmentService.transferAttachmentStore(
					attachmentService.getAttachmentFileName(existingVaultFileName)
					, attachmentService.getAttachmentFileName(expVaultName)
					, ServiceRegistry.instance.getWalletModel()
					, expModel
					, expModel.getEncryptor()
					, false); //no change to original model
		}
		else {
		   //import the entry from current model to the exp vault. use the merge
			expModel.setImpModel(model);
			attachmentService.appendAttachmentStore( attachmentService.getAttachmentFileName(expVaultName)
					, expModel
					, expModel.getEncryptor()  );
		}

	}



	/**
	 *
	 * @param filename
	 * @param model
	 * @param newEnc This is the new encryptor with new pass
	 */
	public void saveVaultWithNewPass(final String filename, final WalletModel model, final PBEEncryptor newEnc) {

		for (WalletItem item : model.getItemsFlatList()) {
			int k = item.getName().indexOf("(*)");
			if (k>0) {
				item.setName(item.getName().substring(0, k));
			}
		}


		DataServiceFactory.createDataService().saveToFile(filename, model, newEnc);

		//save attachments.
		AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);

		//transfer to the ne store with new password.
		String oldStoreName = attachmentService.getAttachmentFileName(filename);
		String newStoreName = oldStoreName + ".tmp";

		//the same one model, just that use the new encryptor for writing the new store.
		if (attachmentService.transferAttachmentStore( oldStoreName,  newStoreName  , model, model, newEnc, true)) {
			//now do the swap of the store to the new one.
			new File(oldStoreName).delete();
			File newFile = new File(newStoreName);
			newFile.renameTo(new File(oldStoreName));
		}

	}


	/**
	 *
	 * @param vaultFileName main store file name
	 * @param model current model
	 * @param encryptor encryptor use to write the new store.
	 */

	//to simplify, I should just do the upgrade without model being modified at all.

	public void upgradeAttachmentStore(final String vaultFileName, final WalletModel model,final PBEEncryptor encryptor) {

		//save attachments.
		AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);

		//transfer to the the store
		String oldStoreName = attachmentService.getAttachmentFileName(vaultFileName);
		String newStoreName = oldStoreName + ".tmp";

		//the same one model, just that use the new encryptor for writing the new store.
		if (attachmentService.transferAttachmentStore( oldStoreName,  newStoreName  , model, model, encryptor, false)) {
			//now do the swap of the store to the new one.
			new File(oldStoreName).delete();
			File newFile = new File(newStoreName);
			newFile.renameTo(new File(oldStoreName));

// reload entries into a model, the attment entry  pos points has changed.
//scratch it. doing a full reloadAttachments() at the end
//			WalletModel  newModel =  model.clone();
//			newModel.setDataFileVersion(WalletModel.LATEST_DATA_VERSION);
//			attachmentService.reloadAttachments(vaultFileName, newModel );

			boolean hasNewEntriesTobeCreated = false;

			for (WalletItem walletItem : model.getItemsFlatList()) {
				if (walletItem.getAttachmentEntry()!=null) {
					if (walletItem.getAttachmentEntry().getAccessFlag()== FileAccessFlag.Delete) {
						//ignore the deleted attachments. they does not exist in the new store.
						walletItem.setAttachmentEntry(null);
						walletItem.setNewAttachmentEntry(null);
					}
					else if (walletItem.getAttachmentEntry().getAccessFlag()== FileAccessFlag.Update
							|| walletItem.getAttachmentEntry().getAccessFlag()== FileAccessFlag.Create
							) {
						//to be appended to new store.
						walletItem.getAttachmentEntry().setAccessFlag(FileAccessFlag.Create);
						hasNewEntriesTobeCreated = true;
					}
				}
			}


			if (hasNewEntriesTobeCreated) {
				//NONE items were transered.
				//DELETE items is set to null , ignored.
				//UPDATE --> create
				attachmentService.appendAttachmentStore(vaultFileName, model, encryptor);
			}


		}
		else {

			/* nothing transferred. such as all attachments are marked as deleted. */

			for (WalletItem walletItem : model.getItemsFlatList()) {
				if (walletItem.getAttachmentEntry() != null) {
					if (walletItem.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Delete) {
						//ignore the deleted attachments. they does not exist in the new store.
						walletItem.setAttachmentEntry(null);
						walletItem.setNewAttachmentEntry(null);
					}
				}
			}


			//no transfer happened.
			//no upgrade , creating of new store with latest version happened.
			attachmentService.newAttachmentStore(vaultFileName, model, encryptor);
		}


		//re read the new store into newModel
		attachmentService.reloadAttachments(vaultFileName, model );

	}




	private FileContentHeader readVersion(DataService ds, final String filename) {
		try {
			FileContentHeader header = ds.readHeader(filename, true);
			int v = header.getVersion();
			return header;
		} catch (IOException e) {
//			if (DialogUtils.getInstance() != null)
//				DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
			//e.printStackTrace();
		}
		return null;
	}

	public FileContentHeader readHeader(final String filename, boolean closeAfterRead) {
		DataService dataServicev10 = DataServiceFactory.createDataService(10);
		DataService dataServicev12 = DataServiceFactory.createDataService(12);
		DataService dataServicev13 = DataServiceFactory.createDataService(13);
		DataService dataServicev14 = DataServiceFactory.createDataService(14);

		int v;
		FileContentHeader header = null;

		header = readVersion(dataServicev14, filename);
		if (header == null) {
			header = readVersion(dataServicev13, filename);
			if (header == null) {
				header = readVersion(dataServicev12, filename);
				if (header == null)
					header = readVersion(dataServicev10, filename);
			}
		}


		if (header==null) {
			if (DialogUtils.getInstance() != null)
				DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
			throw new RuntimeException("Can't read file  header" + filename) ;
		}

		logger.fine("version from header:" + header.getVersion() +", file:" + filename);
//		if (SystemSettings.isDevMode && DialogUtils.getInstance() != null)
//			DialogUtils.getInstance().info("file version:" + header.getVersion());
		return header;


	}




	/**
	 * Export the sourceItem to a new vault for transportation.
	 * @param sourceItem the source item to be exported.
	 * @param exportVaultPassVO the passwords for the new vault.
	 * @param exportVaultFilename The new vault name.
	 */
	public void exportItem(final WalletItem sourceItem
	      ,final PassCombinationVO exportVaultPassVO , final String exportVaultFilename
		//	,final PBEEncryptor expEncryptor
	) {
		try {

			boolean isExistingVault = new File(exportVaultFilename).exists();
			WalletModel model =ServiceRegistry.instance.getWalletModel();

			if (sourceItem.getType()==ItemType.category) {
				//not suporoted. now.
				DialogUtils.getInstance().warn("Error", "Category is not supported yet. Select an item instead.");
			}
			else {
				//get its parent.

				WalletModel expModel= new WalletModel(); ;
				WalletItem root;
				WalletItem newParent=null;
				WalletItem newItem = sourceItem.clone();


				/*export to existing vault*/
				if (isExistingVault) {
					expModel.initEncryptor(exportVaultPassVO);
					expModel = loadVaultIntoModel(exportVaultFilename, expModel.getEncryptor());
					//root = expModel.getRootItem();
					if (newItem.getAttachmentEntry()!=null)
						newItem.getAttachmentEntry().setAccessFlag(FileAccessFlag.Merge);
				}
				else {
					String hash2 = HashingUtils.createHash(exportVaultPassVO.getPass());
					String combinationHash2 = HashingUtils.createHash(exportVaultPassVO.getCombination());
					expModel.setHash(hash2, combinationHash2);
					expModel.initEncryptor(exportVaultPassVO);
					root = new WalletItem(ItemType.category, "export");
					expModel.getItemsFlatList().add(root);
				}


				//find if item exists in the export model already
				WalletItem foundItem = expModel.findItem(newItem.getSysGUID());
				if (foundItem!=null) {
					if (foundItem.isSame(sourceItem)) {
						DialogUtils.getInstance().info("The Item is not exported because it already exists in the target vault.");
						return;
					}
					else {
						//not the same, change the GUID so it is imported as a new item
						newItem.setSysGUID( StringUtils.getGUID() );
					}
				}


				if (sourceItem.getParent()!=null) {
					//find existing parent in the export model
					newParent = expModel.getWalletItem(sourceItem.getParent().getSysGUID());
					if (newParent==null) {
						newParent = sourceItem.getParent().clone();
						expModel.getItemsFlatList().add(newParent);
					}
					newParent.addChild(newItem);
				}


				expModel.getItemsFlatList().add(newItem);

				//save to the export vault.
				String vaultFileName = ServiceRegistry.instance.getWalletModel().getVaultFileName();
				exportModel(vaultFileName, exportVaultFilename
						, model, expModel);

				try {
					DialogUtils.getInstance().info("The item " + sourceItem.getName() +" has been successfully exported to vault:" + exportVaultFilename);
				} catch (Exception e) {
					e.printStackTrace();
				}


			}


		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("An error occurred while trying to export the entry", e.getMessage());
		}

	}


}
