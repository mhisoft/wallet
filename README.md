MHISoft eWallet

A ultimate secure wallet for storing the your app, site login passwords and  personal information …etc. 
I just don't trust any online or third party vault for storing my personal information and passwords.  So I created this open source.

* The MHISoft eWallet is a java application and runs on Windows, MacOS. It requires the  latest JRE with the  “Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy”  installed. 

* All data are stored locally, encrypted. By default it uses the PBEWithHmacSHA512AndAES_256 algorithm, 256 bit key encryption which is considered very safe. 
 

* Password is stored in the most secured Hash format. In theory the password can't be reverse engineeered from this hash , not under the current human technology. Salted hashing with the  PBKDF2WithHmacSHA512 algorithm is used.   mroe tech details see  https://crackstation.net/hashing-security.htm for more information.


* I have always been worried about the data is secretly transmitted across the network without my knowledge while using other applications that connects to the network.  Usually such app allows the data to be shared in the cloud so that it can be access in  multiple devices.  If there are malware or virus, it may steal the personal data. SO YOU SHOULD NOT TRUST ANY VAULT APPLICATION WITH NETWORK ACTIVITIES INVOLVED. DEFINITLY DON'T STORE YOUR DATA ON THE INTERNET. EVEN IN THE ENCRYPTED FORM.  In contrast, the MHISoft eWallet does not use any network activity of any sort. It does not open network port (TCP ports) or use http.  It can be verified by examine the source code or  by using a network sniffer or monitor utility for example. 
 

* Then how to share the data in multiple devices? I'll build the import and merge wallet feature so new entries made on another device can be merged into the current device. It can be across the OS Plaform.  

* The data is encrypted is a very secured algorithem and  256 bit key so if you are relaxed, you can stored it on your personal secured data share in the cloud so that it can be shared by multiple devices. 

* As a best practice, I would still suggest only store the data file on the private computer and transfer via private network and USB drives. Don't evey expose the even encrypted data to the public network. 
