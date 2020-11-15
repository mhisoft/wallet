package org.mhisoft.wallet.service;

import org.mhisoft.common.util.StringUtils;
import org.mhisoft.common.util.security.HashingUtils;
import org.mhisoft.common.util.security.PBEEncryptor;
import org.mhisoft.wallet.model.*;
import org.mhisoft.wallet.view.DialogUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

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
     *
     * @param filename
     * @param encryptor
     * @return
     */
    public StoreVO loadVault(final String filename, final PBEEncryptor encryptor) throws WalletServiceException {
        FileContentHeader header = readHeader(filename, true);
        DataService ds = DataServiceFactory.createDataService(header.getVersion());
        StoreVO ret = ds.readFromFile(filename, encryptor);
        String attFileName = attachmentService.getAttachmentFileName(filename);
        FileAccessTable t = attachmentService.read(attFileName, encryptor);
        if (t != null) {
            for (FileAccessEntry entry : t.getEntries()) {
                WalletItem item = ret.getWalletItem(entry.getGUID());
                if (item != null) {
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
     *
     * @param vaultFileName
     * @param encryptor
     * @return a new model.
     */
    public WalletModel loadVaultIntoModel(final String vaultFileName, final PBEEncryptor encryptor) {
        try {
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
        } catch (WalletServiceException e) {
            e.printStackTrace();
            DialogUtils.getInstance().error("Can not open Vault:" + vaultFileName, e.getMessage());
        }
        return null;
    }


    /**
     * Save the vault with the latest version format.
     * The attachment data store will be ugpraded if needed.
     *
     * @param filename  main store filename.
     * @param model     the model to be saved.
     * @param encryptor encrypor to use for write the new store.
     */
    public void saveVault(final String filename, final WalletModel model, final PBEEncryptor encryptor) {

        for (WalletItem item : model.getItemsFlatList()) {
            int k = item.getName().indexOf("(*)");
            if (k > 0) {
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
        } else {
            attachmentService.saveAttachments(attachmentService.getAttachmentFileName(filename), model, encryptor);
        }

    }

    /**
     * Save the model to a new exported store.
     * And if there are attachments on the item, we need to read the content out from the old attachment store and transfer to a new one.
     *
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
        } else {
            //import the entry from current model to the exp vault. use the merge
            expModel.setImpModel(model);
            attachmentService.appendAttachmentStore(attachmentService.getAttachmentFileName(expVaultName)
                    , expModel
                    , expModel.getEncryptor());
        }

    }


    /**
     * @param filename
     * @param model
     * @param newEnc   This is the new encryptor with new pass
     */
    public void saveVaultWithNewPass(final String filename, final WalletModel model, final PBEEncryptor newEnc) {

        for (WalletItem item : model.getItemsFlatList()) {
            int k = item.getName().indexOf("(*)");
            if (k > 0) {
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
        if (attachmentService.transferAttachmentStore(oldStoreName, newStoreName, model, model, newEnc, true)) {
            //now do the swap of the store to the new one.
            new File(oldStoreName).delete();
            File newFile = new File(newStoreName);
            newFile.renameTo(new File(oldStoreName));
        }

    }


    /**
     * @param vaultFileName main store file name
     * @param model         current model
     * @param encryptor     encryptor use to write the new store.
     */

    //to simplify, I should just do the upgrade without model being modified at all.
    public void upgradeAttachmentStore(final String vaultFileName, final WalletModel model, final PBEEncryptor encryptor) {

        //save attachments.
        AttachmentService attachmentService = ServiceRegistry.instance.getService(BeanType.singleton, AttachmentService.class);

        //transfer to the the store
        String oldStoreName = attachmentService.getAttachmentFileName(vaultFileName);
        String newStoreName = oldStoreName + ".tmp";

        //the same one model, just that use the new encryptor for writing the new store.
        if (attachmentService.transferAttachmentStore(oldStoreName, newStoreName, model, model, encryptor, false)) {
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
                if (walletItem.getAttachmentEntry() != null) {
                    if (walletItem.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Delete) {
                        //ignore the deleted attachments. they does not exist in the new store.
                        walletItem.setAttachmentEntry(null);
                        walletItem.setNewAttachmentEntry(null);
                    } else if (walletItem.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Update
                            || walletItem.getAttachmentEntry().getAccessFlag() == FileAccessFlag.Create
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


        } else {

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
        attachmentService.reloadAttachments(vaultFileName, model);

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
        DataService dataServicev20 = DataServiceFactory.createDataService(20);

        int v;
        FileContentHeader header = null;

        header = readVersion(dataServicev20, filename);

        if (header == null) {
            header = readVersion(dataServicev14, filename);
            if (header == null) {
                header = readVersion(dataServicev13, filename);
                if (header == null) {
                    header = readVersion(dataServicev12, filename);
                    if (header == null)
                        header = readVersion(dataServicev10, filename);
                }
            }
        }


        if (header == null) {
            if (DialogUtils.getInstance() != null)
                DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
            throw new RuntimeException("Can't read file  header" + filename);
        }

        logger.fine("version from header:" + header.getVersion() + ", file:" + filename);
//		if (SystemSettings.isDevMode && DialogUtils.getInstance() != null)
//			DialogUtils.getInstance().info("file version:" + header.getVersion());
        return header;


    }


    /**
     * Export the sourceItem to a new vault for transportation.
     *
     * @param sourceItem          the source item to be exported.
     * @param exportVaultPassVO   the passwords for the new vault.
     * @param exportVaultFilename The new vault name.
     */
    public void exportItem(final WalletItem sourceItem
            , final PassCombinationVO exportVaultPassVO
            , final String exportVaultFilename
    ) {

        boolean isExportToExistingVault = new File(exportVaultFilename).exists();
        //this model
        WalletModel model = ServiceRegistry.instance.getWalletModel();
        WalletModel exportModel = new WalletModel();
        File exportAttachmentVault=null;
        WalletItem expRoot; // the root in the new export vault


        try {
            if (isExportToExistingVault) {
                if ( model.isRoot( sourceItem )) {
                    DialogUtils.getInstance().error("The root can not be exported to an existing vault. choose a new vault file");
                    return;
                }
                exportModel.initEncryptor(exportVaultPassVO);
                exportModel = loadVaultIntoModel(exportVaultFilename, exportModel.getEncryptor());
                expRoot = exportModel.getRootItem();
                exportAttachmentVault = new File(attachmentService.getAttachmentFileName(exportVaultFilename));
            } else {
                /*export to a new vault*/
                String hash2 = HashingUtils.createHash(exportVaultPassVO.getPass());
                String combinationHash2 = HashingUtils.createHash(exportVaultPassVO.getCombination());
                exportModel.setHash(hash2, combinationHash2);
                exportModel.initEncryptor(exportVaultPassVO);
                if (model.isRoot( sourceItem )) {
                    WalletItem newExportItem = sourceItem.clone();
                    expRoot = newExportItem;
                }
                else {
                    expRoot = new WalletItem(ItemType.category, "export");
                }
                exportModel.getItemsFlatList().add(expRoot);
                exportModel.buildTreeFromFlatList();;

            }


            if (sourceItem.getType() == ItemType.category) {
                //export this item and iterate the children
                if (  !model.isRoot( sourceItem ) )
                exportSingleItem(sourceItem, exportModel,  exportAttachmentVault);
                if (sourceItem.hasChildren()) {
                for (WalletItem child:sourceItem.getChildren()) {
                    exportSingleItem(child, exportModel,  exportAttachmentVault);
                        if (child.getType() == ItemType.category && child.hasChildren()) {
                            for (WalletItem grandChild : child.getChildren())
                                exportSingleItem(grandChild, exportModel, exportAttachmentVault);
                        }
                    }
                }

            } else {
                //export this item
                exportSingleItem(sourceItem, exportModel,  exportAttachmentVault);
            }


            //save the exportModel to the new vault.
            String vaultFileName = ServiceRegistry.instance.getWalletModel().getVaultFileName();
            exportModel(vaultFileName, exportVaultFilename, model, exportModel);

            try {
                DialogUtils.getInstance().info("The item " + sourceItem.getName() + " has been successfully exported to vault:" + exportVaultFilename);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (HashingUtils.CannotPerformOperationException e) {
            e.printStackTrace();
            DialogUtils.getInstance().error("An error occurred while trying to export the entry", e.getMessage());
        }


    }


    private void exportSingleItem(
            final WalletItem sourceItem
            , final WalletModel exportModel
            , final File exportAttachmentVault    ) {

        WalletItem newParent = null;

        WalletItem newExportItem = sourceItem.clone();
        if (exportAttachmentVault!=null && exportAttachmentVault.exists()) {
            if (newExportItem.getAttachmentEntry() != null)
                newExportItem.getAttachmentEntry().setAccessFlag(FileAccessFlag.Merge);
        }

        WalletItem foundItem = exportModel.findItem(newExportItem.getSysGUID());
        if (foundItem == null) {
            // use the newExportItem with the existing GUID
        } else {
            //find if item exists in the export model already
            if (foundItem.isSame(sourceItem)) {
                //DialogUtils.getInstance().info("The Item is not exported because it already exists in the target vault.");
                return;
            } else {
                //not the same, change the GUID so it is imported as a new item
                //refresh with a new GUID
                newExportItem.setSysGUID(StringUtils.getGUID());
            }
        }

        if (sourceItem.getType() == ItemType.category) {
            //it is a parent node
            exportModel.addItem(exportModel.getRootItem(), newExportItem);

        } else {
            //this is an item
            if (sourceItem.getParent() != null) {
                //find existing parent in the export model
                newParent = exportModel.getWalletItem(sourceItem.getParent().getSysGUID());
                if (newParent == null) {
                    newParent = sourceItem.getParent().clone();
                    newParent.addChild(newExportItem);
                    exportModel.getItemsFlatList().add(newParent);
                    exportModel.getItemsFlatList().add(newExportItem);
                } else {
                    //add node to the existing parent.
                    newParent.addChild(newExportItem); // this adds it to the new export model
                    exportModel.buildFlatListFromTree();
                }
            }
        }

    }


}
