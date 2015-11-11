import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Per Malmberg on 2015-11-08.
 */
public class Server {
	public static void main(String[] args) {

		System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\perma\\.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		try (ServerSocket serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(4545)) {
			try (SSLSocket workSocket = (SSLSocket) serverSocket.accept()) {
				try (BufferedInputStream in = new BufferedInputStream(workSocket.getInputStream())) {
					try (BufferedOutputStream out = new BufferedOutputStream(workSocket.getOutputStream())) {
						System.out.println("Waiting for data: " + in.available());

						int data = in.read();
						while ( !workSocket.isClosed() && data != -1 ) {
							out.write( (byte)data );
							System.out.print((char) data);
							data = in.read();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
