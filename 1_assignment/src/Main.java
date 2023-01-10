import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //creazione del thread pool di 10 threads
        ExecutorService service = Executors.newFixedThreadPool(10);
        System.out.println("Inserisci un path (exit per uscire)");
        while(true){
            Scanner scanner = new Scanner(System.in);
            String directoryPath = scanner.nextLine();
            if(directoryPath.equals("exit")) break;
            File dir = new File(directoryPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                //scorrimento dei files nella directory
                for (File child : directoryListing) {
                    //se non è una directory, il file viene compresso
                    if(!child.isDirectory()) {
                        String file = directoryPath + "/" + child.getName();
                        String gzipFile = file + ".gz";
                        //esecuzione del task da parte di un thread del pool
                        service.execute(new Task(file, gzipFile));
                    }
                }
            } else {
                // Gestione del caso in cui il path inserito non sia una directory
                System.out.println("Il path inserito non è una directory, riprova.");
            }
        }
        //aspetta finché non tutti i tasks non sono stati completati
        service.shutdown();
    }
}

