MHISoft eVault - A Secure Password Manager and  Vault
======================================================

It is a secure password vault which is best suitable for storing passwords or any personal data. Since v1.2 it starts to support saving the attachment documents such images, PDF, Word Doc..etc to the eVault.  All the data are stored in a encrypted database with AES 256-bit key encryption.

One master password and a set of combination code is required to enter the eVault. The master password  and combination code are stored as one-way-Hash and can't be reverse engineered.  

I just don't trust any online or third party vault for storing my personal information and passwords.  So I created this open source. I believe a personal data vault or password manager should never be clound based. So as designed, the eVault does not use network so you can rest asure that not a single bit of data is transmitted over the network which is not under your control.  

Though the MHISoft eVault encrypted database is local, you can choose to share it on Google Drive or Dropbox.. etc. So you can share the same database across multiple devices. 

The password once lost can't be recovered, which is by design. Even he author of this code can't recover the lost password. And without the password and combination, the vault can't be opened, so please keep the master password and combination key in a safe place, such as in your brain only. 

Download
========
from the Releases tab above : https://github.com/mhisoft/eVault/releases

Features
=========
* Open source and Free. 
* Secure and fast.  AES 256-bit key encryption.
* Runs offline and database is kept local on your machine.
* Platform: Windows and Mac OS X...etc where Java is supported. 
* Organize passwords with categories.  
* Search the entire wallet and locate an item instantly. 
* Support multiple eVault data files. 
* Backup .
* Import and merge in changes from another data file. 
* Auto close the vault if idling for too long.
* Support adding images and attachment docs (PDF, DOC..etc) to the vault items. 

![Screenshot](dist/evault-screenshot-passwordform.png "screenshot")

![Screenshot](dist/evault-screenshot-main.png "screenshot")


Some technical details
======================

*  The JRE is bundled with the release. But if you use your own JRE or JDK , the  “Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy”  need to be installed. 

* All the data are stored locally, encrypted. By default, it uses the PBEWithHmacSHA512AndAES_256 algorithm, 256 bit key encryption provide by the Java 8.  No extra third party library is used. 
 
* Password is stored in the most secured Hash format. In theory the password can't be reverse engineered from this hash , not under the current human technology. Random salted hashing with the  PBKDF2WithHmacSHA512 algorithm is used.   More tech details please see  https://crackstation.net/hashing-security.htm for more information. (thanks to crackstation)

* The app runs offline by design so rest ensure there is absolutely no network connection used by the app. 
 While using other similar product which connects to the network, I have always been worried about the data could be secretly transmitted to somewhere across the network without my knowledge, either intentionally or unintentionally due to malware or virus.   The author decided to design the eVault by not using any kind of network activities (i.e. no port is opended on local and not remote port is reached from eVault either). The encrypted database is local unless you share it across the network.
 
 ## License
Apache License 2.0, January 2004 http://www.apache.org/licenses/

(The text of this page is available for modification and reuse under the terms of the Creative Commons Attribution-Sharealike 3.0 Unported License and the GNU Free Documentation License (unversioned, with no invariant sections, front-cover texts, or back-cover texts).

## Disclaimer
The author is not responsible for loss of any kind incurred directly or indirectly by using this application.


## Donate if you feel like
[![paypal](https://www.paypalobjects.com/webstatic/en_US/i/btn/png/silver-pill-paypal-26px.png)](https://www.paypal.me/mhisoft)
Help to improve the app.
Any amount :)


