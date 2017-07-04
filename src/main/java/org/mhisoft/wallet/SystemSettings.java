package org.mhisoft.wallet;

import org.mhisoft.common.logger.LoggerLevel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class SystemSettings {
	public static boolean debug = Boolean.getBoolean("debug");
	public static boolean isDevMode = System.getProperty("envMode", "production").equals("dev");

	public static LoggerLevel loggerLevel =
			SystemSettings.debug ? LoggerLevel.debug : LoggerLevel.info;


}
