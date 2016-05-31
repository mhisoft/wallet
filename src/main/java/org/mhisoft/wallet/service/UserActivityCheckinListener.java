package org.mhisoft.wallet.service;

import org.mhisoft.common.event.EventListener;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;
import org.mhisoft.common.logger.Loggerfactory;
import org.mhisoft.common.logger.MHILogger;
import org.mhisoft.wallet.SystemSettings;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class UserActivityCheckinListener implements EventListener {
	 private static final MHILogger logger = Loggerfactory.getLogger(UserActivityCheckinListener.class,
			SystemSettings.loggerLevel);

	@Override
	public void handleEvent(MHIEvent event) {
		logger.debug("handling event : " + event.toString());

		if (event.getId() == EventType.UserCheckInEvent) {
			IdleTimerService.instance.checkIn();
		}
	}
}
