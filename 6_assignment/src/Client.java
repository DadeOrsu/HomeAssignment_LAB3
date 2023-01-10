import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
public class Client {
    public static void main(String[] args) throws Exception {
        //chiamata da terminale con ip localhost: java Client 127.0.0.1
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        Scanner scanner=null;
        Scanner in=null;
        try (Socket socket = new Socket(args[0], 10000)) {
            System.out.println("Benvenuto in DungeonAdventures! Inserisci il tuo nome giocatore!");
            scanner = new Scanner(System.in);
            in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                    String line= scanner.nextLine();
                    out.println(line);
                    String response = in.nextLine();
                    System.out.println(response);
                    if (response.contentEquals("GAME OVER")) break;
                    System.out.println(" 1) attacca \n 2) bevi pozione \n 3) fuggi");
            }
        }
        finally {
            assert scanner != null;
            scanner.close();
            assert in != null;
            in.close();}
    }
}