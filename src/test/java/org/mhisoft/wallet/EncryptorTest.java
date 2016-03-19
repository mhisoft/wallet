package org.mhisoft.wallet;

import org.junit.Test;
import org.mhisoft.common.util.StringUtils;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2016
 */
public class EncryptorTest {
	@Test
	public void testPBEEncryption() {
		try {
			StandardPBEByteEncryptor encryptor = new StandardPBEByteEncryptor();
			encryptor.setAlgorithm("PBEWithHmacSHA512AndAES_256");
			encryptor.setPassword("testpassword");
			encryptor.setProviderName("SunJCE");


			encryptor.initialize();
			String s1 = "Columbia";
			byte[] enc = encryptor.encrypt(StringUtils.getBytes(s1));
			System.out.println(StringUtils.toHexString(enc));

			byte[]  dec = encryptor.decrypt(enc);
			System.out.println(StringUtils.bytesToString(dec));


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
