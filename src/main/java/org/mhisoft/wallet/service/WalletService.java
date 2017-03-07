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


	private FileContentHeader readVersion(DataService ds, final String filename) {
		try {
			FileContentHeader header = ds.readHeader(filename, true);
			int v= header.getVersion();
			return header;
		} catch (IOException e) {
			if (DialogUtils.getInstance()!=null)
				DialogUtils.getInstance().error("Error occurred", "Can't read file " + filename);
		}
		return null;
	}

	public  FileContentHeader readHeader(final String filename, boolean closeAfterRead) {
		DataService dataServicev10 = DataServiceFactory.createDataService(10);
		DataService dataServicev12 = DataServiceFactory.createDataService(12);
		DataService dataServicev13 = DataServiceFactory.createDataService(13);

		int v;
		FileContentHeader header=null;

		header = readVersion(dataServicev13, filename);
		if (header == null) {
			header = readVersion(dataServicev12, filename);
			if (header == null)
				header = readVersion(dataServicev10, filename);
		}

		if (DialogUtils.getInstance()!=null)
		DialogUtils.getInstance().info("file version:"+header.getVersion());
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
