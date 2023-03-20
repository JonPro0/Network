package PojektChatServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Beliebig viele Clients können sich über Port 10666 verbinden und miteinander
 * chatten. Nachrichten werden asynchron empfangen und gesendet
 *
 * Erweiterungen sind Befehle, die Aktionen auslösen
 * /name XXX : Ändere den Namen des Users auf XXX
 */
public class ChatServer {
    public static final int PORT = 10666;
    private ServerSocket serverSocket;
    private ArrayList<ChatServerClientThread> clientThreads;

    public ChatServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            clientThreads = new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Fehler beim Öffnen von Port " + PORT);
            System.exit(1);
        }
    }

    public void run() {
        System.out.println("ChatServer gestartet auf Port " + PORT);

        while (true) {
            try {
                // Warte auf Client
                Socket neueClientSocket = serverSocket.accept();
                // Wenn verbunden, starte einen ClientThread
                var neuerClient = new ChatServerClientThread(this,
                        neueClientSocket);
                clientThreads.add(neuerClient);
                neuerClient.start();
                System.out.println("Client Nr. " + clientThreads.size() + " " +
                        "verbunden!");
            } catch (IOException e) {
                System.err.println("Fehler bei Verbindung mit Client");
            }
        }
    }

    public void sendeAnAlle(ChatServerClientThread client, String botschaft) {
        for (var empfaenger: clientThreads) {
            empfaenger.sende(client.getName() + ": " + botschaft);
        }
    }

    public static void main(String[] args) {
        new ChatServer().run();
    }
}

class ChatServerClientThread extends Thread {
    private ChatServer server;
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String name;
    private int correctanswer;
    private String question;



    public ChatServerClientThread(ChatServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;

        try {
            name = clientSocket.getInetAddress().getCanonicalHostName();
            reader =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer =
                    new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {

            System.err.println("Fehler beim Schreiben auf dem Client " + name);
            System.exit(2);
        }
    }

    public void sende(String botschaft) {
        writer.println(botschaft);
        writer.flush();
    }

    @Override
    public void run() {
        String empfangen;
        System.out.println("ClientThread gestartet mit: " + name);
        writer.println("Willkommen beim Chat-Server! Gib QUIT ein um zu " +
                "beenden, Gib /help ein, um die restlichen Befehle zu sehen");
        writer.flush();

        while (true) {
            try {
                empfangen = reader.readLine();
                System.out.println(name + ": " + empfangen);

                if (empfangen == null || empfangen.equalsIgnoreCase("QUIT")) {
                    break;
                }

                if (empfangen.equalsIgnoreCase("/help")) {
                    writer.println(
                            "/name -> Ändere deinen Namen \n" +
                            "/stelleMatheQuiz -> Stelle den anderen Nutzern eine Mathe Frage\n" +
                            "/antworteAufQuiz -> Antworte auf ein Quiz, falls eins vorhanden ist");
                    writer.flush();

                }

                if (empfangen.equalsIgnoreCase("/stelleMatheQuiz")) {
                    writer.println("Welche einfache Mathe Frage willst du den anderen stellen?");
                    writer.flush();
                    question = reader.readLine();
                    writer.println("Gib mir die richtige Antwort auf deine Frage,\n" +
                            "damit ich die anderen Nutzer prüfen kann \n" +
                            question);
                    writer.flush();
                    convertAnswertoInt(reader.readLine());


                }

                if (empfangen.equalsIgnoreCase("/name")) {
                    writer.println("Wie möchtest du heißen?");
                    writer.flush();
                    name = reader.readLine();
                    this.setName(name);
                }

                if (empfangen.equalsIgnoreCase("/antworteAufQuiz")) {
                    if(question == null){
                        writer.println("Es gibt gerade keine Frage, auf die du antworten könntest");
                        writer.flush();
                    } else {
                        writer.println(question);
                        writer.flush();
                        checkAnswer(reader.readLine());
                    }
                }

                server.sendeAnAlle(this, empfangen);
            } catch (IOException e) {
                System.err.println("Fehler beim Empfangen: Client hat " +
                        "die Verbindung beendet");
                break;
            }
        }

        try {

            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            // Ignore silently
        }

        System.out.println("ClientThread beendet: " + name);
    }

    private void checkAnswer(String response) {
        if (isNumeric(response)) {
            int userResponse = Integer.parseInt(response);
            if (userResponse == correctanswer) {
                writer.println("Du hast richtig geantwortet. Herzlichen Glückwunsch!!");
                writer.flush();
            } else {
                writer.println("Du hast falsch geantwortet, vielleicht klappt es beim nächsten Mal.");
                writer.flush();
            }
            question = null;
        } else {
            writer.println("Das war keine Zahl, bitte gib mir die richtige Antwort!!");
            writer.flush();
            try {
                response = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            checkAnswer(response);
        }
    }

    private void convertAnswertoInt(String readLine) throws IOException {
        if (isNumeric(readLine)){
            correctanswer = Integer.parseInt(readLine);
            writer.println("Korrekte Antwort gespeichert!");
            writer.flush();
        } else {
            writer.println("Das war keine Zahl, bitte gib mir die richtige Antwort!!");
            writer.flush();
            readLine = reader.readLine();
            convertAnswertoInt(readLine);
        }
    }

    public static boolean isNumeric(String strNum) {
        try {
            int zahl = Integer.parseInt(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }


}
