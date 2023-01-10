import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Office {
    public static void main(String[] args) throws InterruptedException {

        //creazione della prima Waiting Room in cui ognuno può entrare liberamente
        BlockingQueue <String> waitingRoom = new LinkedBlockingQueue<>(List.of(args));

        //threadPool di lunghezza fissata uguale al numero di sportello
        ThreadPoolExecutor officeBranch = new ThreadPoolExecutor(4, 4, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4));
        //numero preso all'ingresso dal cliente
        int counter = 0;
        String client = "";
        //se non si presenta nessuno per tre secondi lo sportello si chiude
        officeBranch.allowCoreThreadTimeOut(true);
        while(!waitingRoom.isEmpty()) {
            client = waitingRoom.take();
            try {
                //attesa prima di sottomettere un nuovo task
                Thread.sleep(1000);
                //assegnazione di un Task a un Thread di officeBranch
                officeBranch.execute(new Person(client, ++counter));
            } catch (RejectedExecutionException e) {
                System.out.println("task rejected" + e.getMessage());
            }
        }

        //ingresso di altri clienti da servire tastiera di altri clienti da servire
        Scanner input = new Scanner(System.in);
        while(!client.equals("stop")) {
            System.out.println("Inserisci il cliente da servire (stop per uscire):");
            client = input.nextLine();
            try {
                //attesa prima di sottomettere un nuovo task
                Thread.sleep(1000);
                //assegnazione di un Task a un Thread di officeBranch
                officeBranch.execute(new Person(client, ++counter));
            } catch (RejectedExecutionException e) {
                System.out.println("task rejected" + e.getMessage());
            }
        }
        officeBranch.shutdown();
    }
}
