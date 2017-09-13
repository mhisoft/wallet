MHISoft eVault - A Secure Password Manager and  Vault
======================================================

I just don't trust any online or third party vault for storing my personal information and passwords.  So I created this open source.

It is a secure password vault which is best suitable for storing passwords and personal data.  All the data are stored in a encrypted database with AES 256 bit key  encryption. You only need to remember one master password and a set of combination code to the vault. The password is stored as one way Hash and can't be reverse engineered.  The app runs offline so you don't need to worry about data being transmitted over the network.  

The password once lost can't be recovered. This is by design. The author of this code can't even recover the lost password. And without the password and combination, the vault can't be opened, so please keep the master password and combination key in a safe place, such as in your brain only. 

Download
========
from the Releases tab above : https://github.com/mhisoft/eVault/releases

Features
=========
* Free. 
* Secure and fast.  AES 256-bit key encryption.
* Runs offline for security.
* Platform: Windows and Mac OS X...etc where Java  is supported. 
* Organize  passwords with categories.  
* Search the entire wallet and locate the item you need instantly. 
* Support multiple data files. Backup.
* Import and merge in changes from another data file. 
* Auto close the vault if idling for too long.
* Now support adding images and attachment docs (PDF, DOC..etc) to the vault items. 

![Screenshot](dist/evault-screenshot-passwordform.png "screenshot")

![Screenshot](dist/evault-screenshot-main.png "screenshot")


Some technical details
======================

*  The JRE is bundled with the release. But if you use your own JRE or JDK , the  “Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy”  need to be installed. 

* All the data are stored locally, encrypted. By default, it uses the PBEWithHmacSHA512AndAES_256 algorithm, 256 bit key encryption provide by the Java 8.  No extra third party library is required. 
 
* Password is stored in the most secured Hash format. In theory the password can't be reverse engineered from this hash , not under the current human technology. Random salted hashing with the  PBKDF2WithHmacSHA512 algorithm is used.   More tech details please see  https://crackstation.net/hashing-security.htm for more information.

* The app runs offline by design so rest ensure there is absolutely no network connection used by the app. 
 While using other similar product which connects to the network, I have always been worried about the data could be secretly transmitted to somewhere across the network without my knowledge, either intentionally or unintentionally due to malware or virus.   The MHISoft eVault started with this design to not use any network activity. The encrypted database is local unless you copy it over the network.  
 
## Disclaimer
The author is not responsible for loss of any kind incurred directly or indirectly by using this application.

## License
Apache License 2.0, January 2004 http://www.apache.org/licenses/


## Donate if you feel like
[![paypal](https://www.paypalobjects.com/webstatic/en_US/i/btn/png/silver-pill-paypal-26px.png)](https://www.paypal.me/mhisoft)
Help to improve the app.
Any amount :)





