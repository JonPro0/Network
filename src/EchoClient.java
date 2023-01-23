import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) {
        try{
            Socket socket = new Socket("10.2.129.148", 10007);
            Scanner reader = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            Scanner tastatur = new Scanner(System.in);

            String input = reader.nextLine();
            System.out.println("Recieved: " + input);



            input = reader.nextLine();
            System.out.println("Recieved: " + input);
            String botschaft = "";

            while(!botschaft.equals("quit")){
                System.out.print("EINGABE: ");
                botschaft = tastatur.nextLine();

                writer.println(botschaft);
                writer.flush();

                input = reader.nextLine();
                System.out.println("Recieved: " + input);
            }



            socket.close();
            System.out.println("Verbindung getrennt!");
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
