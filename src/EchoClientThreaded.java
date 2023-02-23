import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoClientThreaded {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Scanner tastatur;
    private EchoClientEmpfangsThread empfangsThread;

    public EchoClientThreaded() {
        tastatur = new Scanner(System.in);

        try {
            socket = new Socket("10.2.129.148", 10007);
            reader =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException exception) {
            System.err.println("Fehler beim Verbinden. Beende Programm");
            System.exit(1);
        }
    }

    public void starteChat() {
        // 1. Starte Empfangsthread
        empfangsThread = new EchoClientEmpfangsThread(reader);
        empfangsThread.start();

        // 2. Lies Eingaben über Tastatur ein und
        //    sende sie über den writer
        //    es sei denn, es wird "quit" eingegeben
        sende();

    }

    private void sende() {
        System.out.println("Starte Senden...");
        System.out.println("Gib QUIT ein, um zu beenden");

        String eingabe;

        while (true) {
             eingabe = tastatur.nextLine();

             if(empfangsThread.isAlive())

            if (eingabe.equalsIgnoreCase("quit")) {
                System.exit(1);
            }

            writer.println(eingabe);
            writer.flush();
        }
    }

    public static void main(String[] args) {
        new EchoClientThreaded().starteChat();
    }
}

class EchoClientEmpfangsThread extends Thread {
    BufferedReader reader;

    public EchoClientEmpfangsThread(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        String response;

        while (true) {
            try {
                response = reader.readLine();
                if(response == null){
                    break;
                }
                System.out.println("EMPFANGEN: " + response);
            } catch (IOException e) {
                // Thread beenden
                return;
            }
        }
    }
}