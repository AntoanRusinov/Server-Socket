import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

	public void go() {
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			while (true) {
				System.out.print("Listening for connections on port 5000... ");
				Socket client = serverSocket.accept();
				Thread t = new Thread(new EchoClientHandler(client));
				t.start();
				System.out.println("Connected - " + client.getInetAddress());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EchoServer server = new EchoServer();
		server.go();
	}
}

/**
 * The Runnable job that takes in the new Socket
 */
class EchoClientHandler implements Runnable {

	private final BufferedReader reader;
	private final PrintWriter output;
	private final Socket socket;
	private static final String MESSAGE = "ECHO... [?]\r\n";
	private static final String EXIT_MESSAGE = "Sad to see you go. Goodbye.\r\n";
	private static final String WELCOME_MESSAGE = "Welcome to the Echo Server. Type something to see it echoed back to you!\r\n";

	public EchoClientHandler(Socket incomingSocket) throws IOException {
		socket = incomingSocket;
		output = new PrintWriter(incomingSocket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
	}

	public void run() {
		try {
			output.write(WELCOME_MESSAGE);
			output.flush();
			String line = null;
			while ((line = reader.readLine()) != null) {
				boolean quit = false;
				output.write(MESSAGE.replaceAll("\\?", line));
				if (line.trim().equalsIgnoreCase("exit")) {
					output.write(EXIT_MESSAGE);
					quit = true;
				}
				output.flush();
				if (quit) {
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("OUCH! " + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (Exception ee) {
			}
		}
	}

}
