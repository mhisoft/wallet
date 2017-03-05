package org.mhisoft.wallet.service;

import java.io.IOException;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.Serializer;
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


	public FileContent readFromFile(final String filename, final Encryptor encryptor)  {
		FileContentHeader header = readHeader(filename, true);
		DataService ds = DataServiceFactory.createDataService(header.getVersion());
		return ds.readFromFile(filename, encryptor);
	}


	public void saveToFile(final String filename, final WalletModel model, final Encryptor encryptor) {

		DataServiceFactory.createDataService().saveToFile(filename, model, encryptor);
	}

	public  FileContentHeader readHeader(final String filename, boolean closeAfterRead) {
		DataService dataServicev10 = DataServiceFactory.createDataService(10);
		DataService dataServicev12 = DataServiceFactory.createDataService(12);
		DataService dataServicev13 = DataServiceFactory.createDataService(13);

		int v;
		FileContentHeader header=null;

		try {
			header = dataServicev12.readHeader(filename, true);
			v= header.getVersion();
		} catch (IOException e) {
			try {
				header = dataServicev10.readHeader(filename, true);
				v= header.getVersion();
			} catch (IOException e1) {
				e.printStackTrace();
				DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
		}

		return header;


	}


	public WalletItem cloneItem(final WalletItem src) {
		try {
			Serializer<WalletItem> serializer  = new Serializer<WalletItem>();
			WalletItem ret = serializer.deserialize(serializer.serialize(src));
			return ret;
		} catch (IOException | ClassNotFoundException e) {
		   throw new RuntimeException("cloneItem() failed", e);
		}
	}



}
