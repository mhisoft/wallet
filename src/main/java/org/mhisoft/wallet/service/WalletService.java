package org.mhisoft.wallet.service;

import java.io.File;
import java.io.IOException;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.HashingUtils;
import org.mhisoft.common.util.Serializer;
import org.mhisoft.wallet.model.FileAccessEntry;
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

	AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);


	public FileContent readFromFile(final String filename, final Encryptor encryptor) {
		FileContentHeader header = readHeader(filename, true);
		DataService ds = DataServiceFactory.createDataService(header.getVersion());
		FileContent ret =  ds.readFromFile(filename, encryptor);
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



	public void saveToFile(final String filename, final WalletModel model, final Encryptor encryptor) {

		for (WalletItem item : model.getItemsFlatList()) {
			int k = item.getName().indexOf("(*)");
			if (k>0) {
				item.setName(item.getName().substring(0, k));
			}
		}


		DataServiceFactory.createDataService().saveToFile(filename, model, encryptor);

		//save attachments.
		AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);
		attachmentService.saveAttachments(attachmentService.getAttachmentFileName(filename), model, encryptor);

	}

	/**
	 * Save the model to a new exported store.
	 * And if there are attachments on the item, we need to read the content out from the old attachment store and transfer to a new one.
	 * @param expVaultName
	 * @param expModel
	 * @param expEncryptor
	 */
	public void saveToFileAndTransferAttachment(final String existingVaultFileName,
			final String expVaultName, final WalletModel expModel, final Encryptor expEncryptor) {

		for (WalletItem item : expModel.getItemsFlatList()) {
			int k = item.getName().indexOf("(*)");
			if (k>0) {
				item.setName(item.getName().substring(0, k));
			}
		}


		DataServiceFactory.createDataService().saveToFile(expVaultName, expModel, expEncryptor);

		//save attachments.
		AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);
		attachmentService.transferAttachmentStore(
				 attachmentService.getAttachmentFileName(existingVaultFileName)
				,attachmentService.getAttachmentFileName(expVaultName)
				,ServiceRegistry.instance.getWalletModel()
				,expModel, expEncryptor);

	}


	//


	/**
	 *
	 * @param filename
	 * @param model
	 * @param newEnc This is the new encryptor with new pass
	 */
	public void saveToFileWithNewPassword(final String filename, final WalletModel model, final Encryptor newEnc) {

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
		if (attachmentService.transferAttachmentStore( oldStoreName,  newStoreName  , model, model, newEnc)) {
			//now do the swap of the store to the new one.
			new File(oldStoreName).delete();
			File newFile = new File(newStoreName);
			newFile.renameTo(new File(oldStoreName));
		}

	}




	private FileContentHeader readVersion(DataService ds, final String filename) {
		try {
			FileContentHeader header = ds.readHeader(filename, true);
			int v = header.getVersion();
			return header;
		} catch (IOException e) {
//			if (DialogUtils.getInstance() != null)
//				DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
			e.printStackTrace();
		}
		return null;
	}

	public FileContentHeader readHeader(final String filename, boolean closeAfterRead) {
		DataService dataServicev10 = DataServiceFactory.createDataService(10);
		DataService dataServicev12 = DataServiceFactory.createDataService(12);
		DataService dataServicev13 = DataServiceFactory.createDataService(13);

		int v;
		FileContentHeader header = null;

		header = readVersion(dataServicev13, filename);
		if (header == null) {
			header = readVersion(dataServicev12, filename);
			if (header == null)
				header = readVersion(dataServicev10, filename);
		}

		if (header==null) {
			if (DialogUtils.getInstance() != null)
				DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
			throw new RuntimeException("Can't read file  header" + filename) ;
		}

//		if (SystemSettings.isDevMode && DialogUtils.getInstance() != null)
//			DialogUtils.getInstance().info("file version:" + header.getVersion());
		return header;


	}


	public WalletItem cloneItem(final WalletItem src) {
		try {
			Serializer<WalletItem> serializer = new Serializer<WalletItem>();
			WalletItem ret = serializer.deserialize(serializer.serialize(src));

			//this part is not really a clone. point to the same Attachment Entry for exporting is good enough.
			if (src.getAttachmentEntry()!=null ) {
				ret.setAttachmentEntry(  src.getAttachmentEntry() );
			}

			return ret;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("cloneItem() failed", e);
		}
	}


	/**
	 * Export the sourceItem to a new vault for transportation.
	 * @param sourceItem the source item to be exported.
	 * @param exportVaultPassVO the passwords for the new vault.
	 * @param exportVaultFilename The new vault name.
	 */
	public void exportItem(final WalletItem sourceItem
	      ,final PassCombinationVO exportVaultPassVO
			, final String exportVaultFilename ) {
		try {


			if (sourceItem.getType()==ItemType.category) {
				//not suporoted. now.
				DialogUtils.getInstance().warn("Error", "Category is not supported yet. Select an item instead.");
			}
			else {
				//get its parent.


				WalletModel expModel = new WalletModel();
				String hash2 = HashingUtils.createHash(exportVaultPassVO.getPass());
				String combinationHash2 = HashingUtils.createHash(exportVaultPassVO.getCombination());
				expModel.setHash(hash2, combinationHash2);
				expModel.initEncryptor(exportVaultPassVO);

				WalletItem newParent=null;
				WalletItem newItem = cloneItem(sourceItem);
				if (sourceItem.getParent()!=null) {
					newParent = cloneItem(sourceItem.getParent());
					newParent.addChild(newItem);
				}


				WalletItem root = new WalletItem(ItemType.category, "export");
				expModel.getItemsFlatList().add(root);
				if (newParent!=null)
					expModel.getItemsFlatList().add(newParent);
				expModel.getItemsFlatList().add(newItem);

				//save to the export vault.
				String vaultFileName = ServiceRegistry.instance.getWalletModel().getVaultFileName();
				saveToFileAndTransferAttachment(vaultFileName, exportVaultFilename, expModel, expModel.getEncryptor());

				DialogUtils.getInstance().info("The item " + sourceItem.getName() +" has been successfully exported to vault:" + exportVaultFilename);


			}


		} catch (HashingUtils.CannotPerformOperationException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("An error occurred while trying to export the entry", e.getMessage());
		}

	}


}
