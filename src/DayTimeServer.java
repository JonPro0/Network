import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Date;

public class DayTimeServer {

    public static final int PORT = 10013;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Day Time Server gestartet auf port" + PORT);
        } catch (IOException e) {
            System.err.println("FEHLER: Port " + PORT + " bereits geöffnet");
            System.exit(1);
        }

        // accept wartet 1. auf einen Client, der sich verbinden will
        // und 2. stellt die Verbindung her

        while(true){

        try {
            Socket socket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Neue Verbindung von " + socket.getInetAddress().toString().substring(1));

            writer.println(new Date());
            writer.println(LocalDateTime.now());
            writer.flush();
            socket.close();
        } catch (IOException e) {
            System.err.println("Fehler beim Verbinden mit dem neuen Client");
        }
        }
    }
}
