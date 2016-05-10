package org.mhisoft.wallet.service;

import java.io.IOException;

import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class WalletService {


	public FileContent readFromFile(final String filename) {
		FileContentHeader header = readHeader(filename, true);
		DataService ds = DataServiceFactory.createDataService(header.getVersion());
		return ds.readFromFile(filename);
	}


	public void saveToFile(final String filename, final WalletModel model) {

		DataServiceFactory.createDataService().saveToFile(filename, model);
	}

	public  FileContentHeader readHeader(final String filename, boolean closeAfterRead) {
		DataService dataServicev10 = DataServiceFactory.createDataService(10);
		DataService dataServicev11 = DataServiceFactory.createDataService(11);

		int v;
		FileContentHeader header;

		try {
			 header = dataServicev11.readHeader(filename, true);
			v= header.getVersion();
		} catch (IOException e) {
			try {
				header = dataServicev10.readHeader(filename, true);
				v= header.getVersion();
			} catch (IOException e1) {
				throw new RuntimeException("failed to read the data file");
			}
		}

		return header;


	}



}
