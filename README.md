#MHISoft eWallet - Password Manager and Secure Storage

A ultimate secure wallet for storing the your app, site login passwords and personal information …etc. 
I just don't trust any online or third party vault for storing my personal information and passwords.  So I created this open source.

#Features
* Free. 
* It is a Java application runs on Windows , Mac. 
* Organize your passwords with different categories. 
* Search the entire wallet and locate the item you need instantly. 
* Support multiple wallet files. 
* Import and merge in changes from another wallet data file. 

# Some technical details

*  The JRE is bundled with the release so there is no extra step to install the JRE or  the  “Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy” .  

* All the data are stored locally, encrypted. By default, it uses the PBEWithHmacSHA512AndAES_256 algorithm, 256 bit key encryption which is considered very safe. 
 
* Password is stored in the most secured Hash format. In theory the password can't be reverse engineered from this hash , not under the current human technology. Salted hashing with the  PBKDF2WithHmacSHA512 algorithm is used.   More tech details please see  https://crackstation.net/hashing-security.htm for more information.

* No network connection required, neither is it used by the app. 
 While using other similar product which connects to the network, I have always been worried about the data could be secretly transmitted to somewhere across the network without my knowledge.  The MHISoft eWallet does not use any network activity of any sort. It does not open network port (TCP ports) or use http.  Thus it definitely will not transmit any data over the network. 
 




