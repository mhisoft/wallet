package main.java.com.mhisoft.wallet.util;

import javax.swing.UIManager;

/**
 * Description:  FontUtils
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class FontUtils {


	/**
	 * set the UI Font globally.
	 */
	public static void setUIFont(javax.swing.plaf.FontUIResource f)
	{
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
			{
				UIManager.put(key, f);
			}
		}
	}
}
