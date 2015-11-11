import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Created by Per Malmberg on 2015-11-08.
 */
public class Client {
	public static void main(String[] args) {

		//viaManualLoadOfKeyStore("C:/Users/perma/.keystore", "password");
		//viaProperties("C:/Users/perma/.keystore", "password");
		viaCertFileLoad("MyAlias", "d:/source-svn/Java/SSL/cert/mykeypair.cer", "password");

	}

	private static void viaCertFileLoad(String certAlias, String pathToCert, String keyStorePassword) {


//http://stackoverflow.com/questions/18889058/programmatically-import-ca-trust-cert-into-existing-keystore-file-without-using

		try {

			// Get a certificate factory for X.509 certificates
			CertificateFactory certFact = CertificateFactory.getInstance("X.509");

			// Create an empty keystore
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(null, null);

			// Load the certificate as a byte stream
			InputStream certStream = getFileAsByteStream(pathToCert);

			// Create a certificate from the stream and add it to the key store
			Certificate c = certFact.generateCertificate(certStream);
			keystore.setCertificateEntry(certAlias, c);

			// Initialize the TrustManagerFactory with the keystore
			TrustManagerFactory trustMgr = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustMgr.init(keystore);

			// Create an SSL context using the trust manager
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, trustMgr.getTrustManagers(), null);
			// Create a SSL socket factory from the context.
			SSLSocketFactory fact = context.getSocketFactory();

			SendData(fact);

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void viaManualLoadOfKeyStore(String pathToTrustStore, String trustStorePassword) {

		try {
			// Load the provided keystore
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(new FileInputStream(pathToTrustStore), trustStorePassword.toCharArray());

			// Initialize the TrustManagerFactory with the keystore
			TrustManagerFactory trustMgr = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustMgr.init(keystore);

			// Create an SSL context using the trust manager
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, trustMgr.getTrustManagers(), null);
			// Create a SSL socket factory from the context.
			SSLSocketFactory fact = context.getSocketFactory();

			SendData(fact);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void viaProperties(String pathToTrustStore, String trustStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", pathToTrustStore);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

		try {
			SSLSocketFactory fact = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SendData(fact);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void SendData(SSLSocketFactory fact) throws IOException {
		try (SSLSocket client = (SSLSocket) fact.createSocket("localhost", 4545)) {
			try (BufferedInputStream in = new BufferedInputStream(client.getInputStream())) {
				try (OutputStream out = client.getOutputStream()) {
					System.out.println("Waiting for data: " + in.available());

					char[] data = "abcdefg".toCharArray();
					for (char c : data) {
						out.write(c);
					}
					client.close();
				}
			}
		}
	}

	private static InputStream getFileAsByteStream(String fname) throws IOException {
		byte[] bytes;
		try (FileInputStream fis = new FileInputStream(fname)) {
			try (DataInputStream dis = new DataInputStream(fis)) {
				bytes = new byte[dis.available()];
				dis.readFully(bytes);
			}
		}
		return new ByteArrayInputStream(bytes);
	}
}
