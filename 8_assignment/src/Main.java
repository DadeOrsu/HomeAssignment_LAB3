import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.io.*;
public class Main {
    public static void CopyDirectBuffers(String srcFile, String destFile) throws IOException {
        ReadableByteChannel src = Channels.newChannel(new FileInputStream(srcFile));
        WritableByteChannel dest = Channels.newChannel(new FileOutputStream(destFile));
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        readBuffer(src, dest, buffer);
    }

    public static void CopyNotDirectBuffers(String srcFile, String destFile) throws IOException {
        ReadableByteChannel src = Channels.newChannel(new FileInputStream(srcFile));
        WritableByteChannel dest = Channels.newChannel(new FileOutputStream(destFile));
        ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
        readBuffer(src, dest, buffer);
    }

    public static void CopyBufferedStream(String srcFile, String destFile){
        byte[] buffer = new byte[1000];
        try {
            FileInputStream fis = new FileInputStream(srcFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(destFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int numBytes;
            while ((numBytes = bis.read(buffer))!= -1)
                bos.write(buffer, 0, numBytes);
            bis.close();
            bos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void CopyReadByteStream(String srcFile, String destFile) {
        byte[] buffer = new byte[1000];
        try {
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);
            int numBytes;
            while ((numBytes = fis.read(buffer))!= -1)
                fos.write(buffer, 0, numBytes);
            fis.close();
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void CopyTransferTo(String srcFile, String destFile) throws IOException {
        RandomAccessFile fromFile = new RandomAccessFile(srcFile, "rw");
        FileChannel fromChannel = fromFile.getChannel();
        RandomAccessFile toFile = new RandomAccessFile(destFile, "rw");
        FileChannel toChannel = toFile.getChannel();
        long position = 0;
        long count = fromChannel.size();
        fromChannel.transferTo(position,count,toChannel);
        fromChannel.close();
        toChannel.close();
    }

    private static void readBuffer(ReadableByteChannel src, WritableByteChannel dest, ByteBuffer buffer) throws IOException {
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write (buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining())
            dest.write (buffer);
        src.close();
        dest.close();
    }

    public static void main(String [] argv) throws IOException {
        //file su cui è stato testato il programma: file_800K
        //absolute path file sorgente
       String srcFile = argv[0];
       //absolute path file destinazione
       String destFile = argv[1];
       //strategia di buffering (1,2,3,4,5)
       int bufferingStrategy = Integer.parseInt(argv[2]);
       long startTime = System.currentTimeMillis();
       switch (bufferingStrategy) {
           case 1:
               //17 ms
               CopyDirectBuffers(srcFile, destFile);
               break;
           case 2:
               //20 ms
               CopyNotDirectBuffers(srcFile, destFile);
               break;
           case 3:
               //6 ms
               CopyTransferTo(srcFile, destFile);
               break;
           case 4:
               //14 ms
               CopyBufferedStream(srcFile, destFile);
               break;
           case 5:
               //17 ms
               CopyReadByteStream(srcFile, destFile);
               break;
           default:
               System.err.println("opzione non valida");
               break;
       }
       if(bufferingStrategy > 0 && bufferingStrategy < 6){
           long endTime   = System.currentTimeMillis();
           long totalTime = endTime - startTime;
           System.out.println("strategia scelta: " + bufferingStrategy + " tempo di esecuzione: " + totalTime + "ms");
       }
    }
}
