import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class POP3Client {
   private Socket socket;
   private Scanner reader;
   private PrintWriter writer;


   public POP3Client(){
       verbinden();
       anmelden();
       listeMail();
   }



    private void verbinden() {
       try {
           socket = new Socket("10.2.129.148", 10110);
           reader = new Scanner(socket.getInputStream());
           writer = new PrintWriter(socket.getOutputStream());
       } catch (IOException e) {
           System.err.println("Fehler beim verbinden");
           System.exit(1);
       }
   }

    private void anmelden() {
       
    }


    public static void main(String[] args) {
        try{
            Socket socket = new Socket("10.2.129.148", 10110);
            Scanner reader = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

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
