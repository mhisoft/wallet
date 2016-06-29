package org.mhisoft.wallet.service;

import java.util.logging.Logger;

import org.mhisoft.common.event.EventListener;
import org.mhisoft.common.event.EventType;
import org.mhisoft.common.event.MHIEvent;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class UserActivityCheckinListener implements EventListener {

	private static final Logger logger = Logger.getLogger(UserActivityCheckinListener.class.getName());


	@Override
	public void handleEvent(MHIEvent event) {
		logger.finest("handling event : " + event.toString());

		if (event.getId() == EventType.UserCheckInEvent) {
			IdleTimerService.instance.checkIn();
		}

	}
}
