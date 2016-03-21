MHISoft eWallet

A safe wallet to store the your app, site login, personal information and passwords…etc. 
I just don't trust any online or third party vault for storing my personal information and passwords.  So I created this open source.
* The MHISoft eWallet is a java application and runs on Windows, MacOS where the latest JRE and “Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy”  is installed. 
* All data are stored locally, encrypted. By default it uses the PBEWithHmacSHA512AndAES_256 algorithm which is considered very safe. 
* The password for the wallet is composed of a user password and a serial of number like the keys to a safe. Only the password's one way hash is stored. (The passwrod can't be recovered from the one way hash).  Salted hashing with the  PBKDF2WithHmacSHA512 algorithm is used.   See https://crackstation.net/hashing-security.htm for more information.
* When using other applications that I can’t trust, I have always been worried about the data is secretly transmitted across the network without my knowledge.   Any vault application has decrypted data in memory so there is a vulnerability the decrypted data is stolen across the network. Thus to be rest assure, the MHISoft eWallet does not use any network activity of any sort. It does not open network port or use http.  It can be verified by examine the source code or  by using a network sniffer or monitor utility for example. 
* The encrypted data though is transferable to other computers if you run the application in multiple computers. As a best practice, I would still suggest only store the data file on the private computer and transfer via private network and USB drives. 
