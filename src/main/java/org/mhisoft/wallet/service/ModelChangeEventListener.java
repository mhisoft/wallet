package org.mhisoft.wallet.service;

import java.util.logging.Logger;

import org.mhisoft.common.event.EventListener;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.wallet.view.WalletForm;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ModelChangeEventListener implements EventListener {

	private static final Logger logger = Logger.getLogger(ModelChangeEventListener.class.getName());


	@Override
	public void handleEvent(MHIEvent event) {
		logger.finest("handling event : " + event.toString());

		if (event.getId() == EventType.ModelChangeEvent) {

			WalletForm form = ServiceRegistry.instance.getWalletForm();
			boolean isModelModified = (Boolean)event.getPayload();
			if (isModelModified) {
				form.btnSaveForm.setVisible(true);
				form.btnCancelEdit.setVisible(true);
				form.disableMenus();
			}
			else
				form.enableMenus();
		}

	}
}
