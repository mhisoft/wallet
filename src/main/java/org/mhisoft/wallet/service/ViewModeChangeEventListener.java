package org.mhisoft.wallet.service;

import java.util.logging.Logger;

import org.mhisoft.common.event.EventListener;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.wallet.view.DisplayMode;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ViewModeChangeEventListener implements EventListener {

	private static final Logger logger = Logger.getLogger(ViewModeChangeEventListener.class.getName());


	@Override
	public void handleEvent(MHIEvent event) {
		logger.finest("handling event : " + event.toString());
		DisplayMode displayMode =  (DisplayMode)event.getPayload() ;
		WalletForm form =  ServiceRegistry.instance.getWalletForm();

		if (event.getId() == EventType.ViewModeChangeEvent) {
			if (displayMode == DisplayMode.edit || displayMode == DisplayMode.add ) {
				form.btnEditForm.setVisible(false);
				form.btnCancelEdit.setVisible(true);
				form.btnSaveForm.setVisible(true);
				form.btnClose.setVisible(false);
				form.menuClose.setVisible(false);
				form.btnAttach.setVisible(true);
				form.menuExport.setVisible(false);
				form.disableMenus();
			} else if (displayMode == DisplayMode.view) {
				form.btnEditForm.setVisible(true);
				form.btnCancelEdit.setVisible(false);
				form.btnSaveForm.setVisible(false);
				form.btnClose.setVisible(true);
				form.menuClose.setVisible(true);
				form.btnAttach.setVisible(true);
				form.menuExport.setVisible(true);
				form.enableMenus();
			}
		}

		if (ServiceRegistry.instance.getWalletModel().isModified())  {
			form.btnCancelEdit.setVisible(true);
			form.btnSaveForm.setVisible(true);
			form.disableMenus();
		}

	}
}
