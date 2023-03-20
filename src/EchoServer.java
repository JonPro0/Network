import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class EchoServer {
    public static final int PORT = 10007;
    ServerSocket serverSocket;

    public EchoServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Echo-Server gestartet auf Port " + PORT);
        } catch (IOException e) {
            System.err.println("Fehler: Port " + PORT + " kann nicht geöffnet werden");
            System.exit(1);
        }
    }

    public void start() {


        while (true) {
            try {
                Socket client = serverSocket.accept();
                behandleClient(client);


            } catch (IOException e) {
                System.err.println("Fehler beim Verbinden mit dem neuen Client");
                e.printStackTrace();
                System.exit(1);
            }
        }

    }

    private void behandleClient(Socket client) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            System.out.println("Neue Verbindung mit Cliient: " + client.getInetAddress().toString().substring(1));

            String response;

            while (true) {
                response = reader.readLine();

                if (response == null || response.equalsIgnoreCase("QUIT")) break;

                System.out.println("Empfangen: " + response);
                writer.write(response + "\n\r");
                writer.flush();
            }
            reader.close();
            writer.close();
            client.close();
        } catch (IOException e) {
            System.err.println("HILFE");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EchoServer().start();
    }
}


