import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class EchoClient {
    public static final int PORT =8000;
    public static void main(String[] args) throws IOException {
        SocketChannel client = null;
        ByteBuffer buffer;

        //creazione del SocketChannel
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", PORT));
            System.out.println("Connesso al server sulla porta " + PORT + ", (exit per uscire)");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner input = new Scanner(System.in);
        String msg;
        //invio del messaggio preso da tastiera a EchoServer
        do {
            msg = input.nextLine();
            buffer = ByteBuffer.wrap(msg.getBytes());
            String answer;
            try {
                assert client != null;
                //scrittura su SocketChannel
                client.write(buffer);
                buffer.clear();
                //lettura della risposta del server sul SocketChannel
                client.read(buffer);
                answer = new String(buffer.array()).trim();
                System.out.println("echoed by server: " + answer);
                buffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while(!msg.equals("exit"));
        //chiusura del SocketChannel
        client.close();
    }
}