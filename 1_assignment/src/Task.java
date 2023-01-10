import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

//class Task contenente la procedura da far eseguire ai threads
public class Task implements Runnable {
    final String file;
    final String gzipFile;

    public Task(String file, String gzipFile) {
        this.file = file;
        this.gzipFile = gzipFile;
    }

    public void run() {
        try {
            System.out.println(Thread.currentThread().getName());
            FileInputStream fIS = new FileInputStream(file);
            FileOutputStream fOS = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fOS);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fIS.read(buffer)) != -1)
                gzipOS.write(buffer, 0, len);
            //chiude le risorse
            gzipOS.close();
            fOS.close();
            fIS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
