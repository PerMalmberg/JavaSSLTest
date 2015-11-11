# Java SSL Test
Three ways of creating SSL sockets
* Via keystore/truststore.
* By loading a custom trust store.
* By loading a certificate into a trust store.

Create a certificate in your keystore using `keytool` that comes with Java.
To test the certificate loading, you have to export the certificate and place the .cer-file in the .\cert folder.
