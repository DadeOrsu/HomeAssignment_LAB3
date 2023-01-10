
import java.io.*;
import java.net.InetAddress;


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

    public static void main(String[] args) throws IOException{

            //web.log.txt
            File file=new File(args[0]);    //creates a new file instance
            FileReader fr=new FileReader(file);   //reads the file
            BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
            long startTime = System.currentTimeMillis();
            String line;
            while((line=br.readLine())!=null)
            {
                String[] parts = line.split(" ",2);
                InetAddress ia1 = InetAddress.getByAddress(ip2bytes(parts[0]));

                System.out.println(ia1.getHostName() + " " + parts[1]);      //appends line to string buffer

            }
            long endTime = System.currentTimeMillis();
            System.out.println("Tempo totale: " + (endTime-startTime)/1000 + " sec.");
            fr.close();    //closes the stream and release the resources
    }
}


