#MHISoft eWallet - Password Manager and Secure Storage

A ultimate secure wallet for storing the your app, site login passwords and  personal information …etc. 
I just don't trust any online or third party vault for storing my personal information and passwords.  So I created this open source.

# What does it do?


* The MHISoft eWallet is a free Java application and runs on Windows, MacOS etc as long as it has JRE. It requires the  latest JRE with the  “Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy”  installed. 

* All the data are stored locally, encrypted. By default it uses the PBEWithHmacSHA512AndAES_256 algorithm, 256 bit key encryption which is considered very safe. 
 
* Password is stored in the most secured Hash format. In theory the password can't be reverse engineeered from this hash , not under the current human technology. Salted hashing with the  PBKDF2WithHmacSHA512 algorithm is used.   mroe tech details see  https://crackstation.net/hashing-security.htm for more information.

* No netrowk connection required. 
 I have always been worried about the data could be secretly transmitted across the network without my knowledge while using other applications that connects to the network.  Usually such app allows the data to be shared in the cloud so that it can be access in  multiple devices.  If there are malware or virus, it may steal the personal data. SO YOU SHOULD NOT TRUST ANY VAULT APPLICATION WITH NETWORK ACTIVITIES INVOLVED. DEFINITLY DON'T STORE YOUR DATA ON THE INTERNET. EVEN IN THE ENCRYPTED FORM.  In contrast, the MHISoft eWallet does not use any network activity of any sort. It does not open network port (TCP ports) or use http.  It can be verified by examine the source code or  by using a network sniffer or monitor utility for example. 
 

#Features
* Organize your password in categories. 
* Search  the wallet and lcoate the item you need instantly. 
* Support multiple wallet files. 
* Import and merge in changes from another wallet data file. 



