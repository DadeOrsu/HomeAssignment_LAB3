import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class Main {
    enum Reason_Type { BONIFICO, ACCREDITO, BOLLETTINO, F24, PAGOBANCOMAT }
    public static Map<Reason_Type, LongAdder> occurrences = new ConcurrentHashMap<>();

    public static class Movement {
        public final String date;
        public final Reason_Type reason;
        public Movement(String date, Reason_Type reason){
            this.date = date;
            this.reason = reason;
        }
    }

    public static class BankAccount implements Runnable {
        public final String owner;
        public final Movement[] records;

        public BankAccount(String owner, Movement[] records) {
            this.owner = owner;
            this.records = records;
        }

        public void run() {
            for (Movement record: records)
                //Incremento nella HashMap di un determinato Reason_Type
                occurrences.computeIfAbsent(record.reason, type -> new LongAdder()).increment();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String filename = args[0];
        JsonReader reader;
        Gson gson = new Gson();
        ExecutorService service = Executors.newFixedThreadPool(20);
        try {
            reader = new JsonReader(new FileReader(filename));
            reader.beginArray();
            //lettura del conto corrente e conteggio effettuato dai threads di service.
            while (reader.hasNext())
                service.execute(gson.fromJson(reader, BankAccount.class));
            reader.endArray();
            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        service.shutdown();
        if (!service.awaitTermination(60000, TimeUnit.SECONDS))
            System.err.println("I task non sono stati completati in tempo.");
        System.out.println("Occorrenze trovate in " + filename);
        //stampa a video delle occorrenze trovate nel file json in ingresso.
        for (Reason_Type reason : occurrences.keySet()) {
            System.out.println(reason + " --> " + occurrences.get(reason));
        }
    }
}