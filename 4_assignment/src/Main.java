import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //creazione della hashmap
        ConcurrentHashMap<Character, LongAdder> map = new ConcurrentHashMap<>();
        //creazione del thread pool
        ExecutorService service = Executors.newCachedThreadPool();
        Scanner scanner = new Scanner(System.in);

        LinkedList<File> files = new LinkedList<>();
        System.out.println("Inserisci i path da processare (stop per uscire):");
        while(true) {
            String arg = scanner.nextLine();
            if(arg.equals("stop")){
                break;
            }
            files.push(new File(arg));
        }

        while(!files.isEmpty()){
            service.execute(new Counter(files.pop(), map));
        }
        service.shutdown();

        if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("Pool did not terminate.");
        }
        //creazione del file con i risultati
        PrintWriter writer = new PrintWriter("occurrences.txt", StandardCharsets.UTF_8);
        for (Character c: map.keySet()) {
            writer.println(c+ ", " + map.get(c));
        }

        System.out.println("Le occorrenze rilevate si trovano in occurrences.txt");
        writer.close();
    }
}
