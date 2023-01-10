import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


import static java.util.concurrent.Executors.newCachedThreadPool;

public class Weblog {
    static byte[] ip2bytes(String ip) {
        String[] address = ip.split("\\.");
        if (address.length != 4)
            throw new IllegalArgumentException("ip should be a x.x.x.x");
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            int a = Integer.parseInt(address[i]);
            if (a > 255)
                throw new IllegalArgumentException("not a legal ip address");
            bytes[i] = (byte) a;
        }
        return bytes;
    }

    private static class logPrinter implements Runnable{

        final String line;

        public logPrinter(String line) {
            this.line = line;
        }
        @Override
        public void run() {
            String[] parts = line.split(" ",2);
            InetAddress ia1 = null;
            try {
                ia1 = InetAddress.getByAddress(ip2bytes(parts[0]));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            System.out.println(ia1.getHostName() + " " + parts[1]);      //appends line to string buffer
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        File file=new File(args[0]);    //creates a new file instance
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream

        ExecutorService tp = newCachedThreadPool();
        String line;
        long startTime = System.currentTimeMillis();
        while((line=br.readLine())!=null)
        {
            tp.execute(new logPrinter(line));
        }
        tp.shutdown();

        if (!tp.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("La pool non è terminata.");
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Tempo totale: " + (endTime-startTime)/1000 + " sec.");
        fr.close();    //closes the stream and release the resources
    }
}