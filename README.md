A safe vault to store the app , site login and passwords. 
I don't trust any online or third party vault for storing my passwords. So createdthis open source.
* All data are stored locally and are encrypted.
* Only hash of the password for the wallet is stored. Salted hashing using the PBKDF2WithHmacSHA512 algorithem.   see https://crackstation.net/hashing-security.htm for more information.
* I have always been worried about the data is transmitted across the network in the thrird party applications. Thus I made sure this MHISoft Wallet application does not use network activity of any sort.
password vault The applicaotin absolutely does not use any sort of network activity, i.e. it does not transmit data through network.
