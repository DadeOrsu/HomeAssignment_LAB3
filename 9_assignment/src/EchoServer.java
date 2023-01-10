import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
    public static final int PORT =8000;
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress("localhost", PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel client = channel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    if (new String(buffer.array()).trim().equals("exit")) {
                        //alla ricezione del comando "exit" SocketChannel client viene chiuso
                        client.close();
                    } else {
                        //lettura del messaggio da SocketChannel
                        client.read(buffer);
                        buffer.flip();
                        //risposta del server contenente il messaggio letto
                        client.write(buffer);
                        buffer.clear();
                    }
                }
                keyIterator.remove();
            }
        }
    }
}