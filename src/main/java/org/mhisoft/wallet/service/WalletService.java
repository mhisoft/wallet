package org.mhisoft.wallet.service;

import java.util.ArrayList;
import java.util.List;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;

import org.mhisoft.common.util.Encryptor;
import org.mhisoft.common.util.FileUtils;
import org.mhisoft.common.util.Serializer;
import org.mhisoft.common.util.StringUtils;
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
	private static int FIXED_RECORD_LENGTH =2000;


	public void saveToFile(final String filename) {
		FileOutputStream stream = null;
		try {

			final WalletModel model = ServiceRegistry.instance.getWalletModel();

			stream = new FileOutputStream(filename);
			DataOutputStream outputStream = new DataOutputStream(stream);
			model.buildFlatListFromTree();
			Serializer<WalletItem> serializer  = new Serializer<WalletItem>();

			/*#1: hash*/
			writeString( outputStream, model.getPassHash() );

			/*#2: list size 4 bytes*/
			outputStream.write(FileUtils.intToByteArray(model.getItemsFlatList().size()));

			int i=0;
			byte[] cipherParameters;
			for (WalletItem item : model.getItemsFlatList()) {
				byte[] _byteItem = serializer.serialize(item);
				byte[] enc = Encryptor.getInstance().encrypt(_byteItem);
				cipherParameters = Encryptor.getInstance().getCipherParameters();
				/*#3: cipherParameters size 4 bytes*/
				outputStream.write(FileUtils.intToByteArray(cipherParameters.length));

				/*#4: cipherParameters body*/
				outputStream.write(cipherParameters);

				byte[] byteItem = FileUtils.padByteArray(enc, FIXED_RECORD_LENGTH);

				/*#5: item body*/
				//write the object byte stream
				outputStream.write(byteItem);
				i++;
				System.out.println("write " + item.getName()+", size:" + byteItem.length);
			}

		} catch ( IOException e) {
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred when saveToFile()", e.getMessage());

		} finally {
			if (stream!=null)
				try {
					stream.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
		}
	}

	protected void writeString(DataOutputStream out, String str) throws IOException  {
		if (str==null)
			throw new RuntimeException("input str is null");

		byte[] _byte = StringUtils.getBytes(str);
		//write size
		out.write(FileUtils.intToByteArray(_byte.length));
		out.write(_byte);

	}


	protected String readString(FileInputStream fileInputStream) throws IOException  {
		int numBytes = FileUtils.readInt(fileInputStream);
		byte[] _byte = new byte[numBytes];
		int readBytes = fileInputStream.read(_byte);
		if (readBytes!=numBytes)
			throw new RuntimeException("readString() failed, " + "read " + readBytes +" bytes only, expected to read:"+ numBytes);

	    return StringUtils.bytesToString(_byte);

	}

	/**
	 * Read the file header info and close it.
	 * @param filename
	 * @return
	 */
	public  FileContentHeader readHeader(final String filename)  {
		FileInputStream fileInputStream =null;
		FileContentHeader header = new FileContentHeader();
		try {

			//don't read the whole file
			//byte[] bytesWholeFile = FileUtils.readFile(filename);
			fileInputStream = new FileInputStream(new File(filename));

			/*#1*/
			header.setPassHash(readString(fileInputStream));


			/*#2 read the size,  int, 4 bytes*/
			header.setNumberOfItems(FileUtils.readInt(fileInputStream));
		}catch (IOException e) {
			throw new RuntimeException("failed to read file header", e);
		} finally {
			if (fileInputStream!=null)
				try {
					fileInputStream.close();
				} catch (IOException e) {
					//
				}
		}
		return header;

	}


	//   need Encryptor to be intialized first.
	public FileContent readFromFile(final String filename) {
		//ByteArrayInputStream input = null;
		//byte[] readBuf = new byte[DELIMITER_bytes.length];
		FileContent ret  = new FileContent();
		List<WalletItem> walletItems = new ArrayList<>();
		ret.setWalletItems(walletItems);

		Serializer<WalletItem> serializer  = new Serializer<WalletItem>();
		int readBytes = 0;
		try {
			//Encryptor encryptor = Encryptor.createInstance("testit&(9938447");


			//don't read the whole file
			//byte[] bytesWholeFile = FileUtils.readFile(filename);
			FileInputStream fileInputStream = new FileInputStream( new File(filename));

			/*#1*/
			ret.setPassHash(readString(fileInputStream));


			/*#2 read the size,  int, 4 bytes*/
			int numberOfItems = FileUtils.readInt(fileInputStream);
			System.out.println();
			System.out.println("numberOfItems=" + numberOfItems);


			int k = 0;
			while (k < numberOfItems) {

                /*#3: ciperParameters size 4 bytes*/
				int cipherParametersLength  = FileUtils.readInt(fileInputStream);

			    /*#4: cipherParameters body*/
				byte[] _byteCiper = new byte[cipherParametersLength];
				readBytes = fileInputStream.read(_byteCiper);
				if (readBytes!=cipherParametersLength)
					throw new RuntimeException("read " + readBytes +" bytes only, expected to read:"+ _byteCiper);

				AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(Encryptor.ALGORITHM);
				algorithmParameters.init(_byteCiper);

				/*#5: item body*/
				int objectSize =FIXED_RECORD_LENGTH;
				byte[] _byteItem = new byte[FIXED_RECORD_LENGTH];
				readBytes = fileInputStream.read(_byteItem);
				if(readBytes==objectSize) {
					_byteItem = FileUtils.trimByteArray(_byteItem);
					byte[] byteItem = Encryptor.getInstance().decrypt(_byteItem, algorithmParameters);
					WalletItem item = serializer.deserialize(byteItem);
					System.out.println(", item: " + item.getName());
					walletItems.add(item);
					k++;
				}
				else {
					throw new RuntimeException("read " + readBytes +" bytes only, expected  objectSize:"+ objectSize);
				}

			}
		} catch (Exception e) {
			//end
			e.printStackTrace();
			DialogUtils.getInstance().error("Error occurred in readFromFile()", e.getMessage());
		}
		ServiceRegistry.instance.getWalletModel().setModified(false);
		return ret;


	}


}
