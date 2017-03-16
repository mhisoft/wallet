package org.mhisoft.wallet.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.model.WalletModel;

/**
 * Description:
 *
 * @author Tony Xue
 * @since Mar, 2017
 */
//only added combinatin hash to the header.
public class DataServiceImplv13 extends DataServiceImplv12 {
	public int getVersion() {
		return 13;
	}

	public FileContentHeader readHeader(FileContentHeader header, FileInputStream fileIN, DataInputStream dataIn  )
			throws IOException {

		super.readHeader(header, fileIN, dataIn)  ;

		header.setCombinationHash(FileUtils.readString(fileIN));

		return header;
	}


	protected void saveHeader(DataOutputStream dataOut, final WalletModel model) throws IOException {
		super.saveHeader(dataOut, model);
		FileUtils.writeString(dataOut, model.getCombinationHash());
	}


}
