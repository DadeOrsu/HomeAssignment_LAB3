import java.util.Comparator;
import java.util.concurrent.*;

public class Laboratorio {
    public static void main(String[] args){
        //inserimento da linea di comando dei parametri
        int students = Integer.parseInt(args[0]);
        int thesisStudents = Integer.parseInt(args[1]);
        int professors = Integer.parseInt(args[2]);

        //numero generato casualmente che indica il numero di accessi compreso tra 1 e 10
        int k = ThreadLocalRandom.current().nextInt(1, 11);

        //creazione del tutor a cui si devono rivolgere gli utenti
        ComputerManager tutor = new ComputerManager(20);

        int users = students + thesisStudents + professors;

        //il tutor assegna i computers agli utenti con priorità gestita da una Priority BlockingQueue
        ThreadPoolExecutor userExecutor = new ThreadPoolExecutor(users, users, 0, TimeUnit.SECONDS, new PriorityBlockingQueue<>(10, new UserComparator()));

        for (int i = 0; i < students; i++)
            userExecutor.execute(new Student(tutor, i,k));
        for (int i = 0; i < thesisStudents; i++)
            userExecutor.execute(new ThesisStudent(tutor, students + professors + i, k));
        for (int i = 0; i < professors; i++)
            userExecutor.execute(new Professor(tutor, students + i, k));



        //attende la terminazione di tutti i tasks
        userExecutor.shutdown();
    }

    //classe utilizzata dalla PriorityBlockingQueue per ordinare i tasks con diversa priorità
    static class UserComparator implements Comparator<Runnable> {
        public int compare(Runnable o1, Runnable o2) {
            if (o1 == null && o2 == null)
                return 0;
            else if (o1 == null)
                return -1;
            else if (o2 == null)
                return 1;
            else {
                int p1 = o1 instanceof Professor? Thread.MAX_PRIORITY: o1 instanceof ThesisStudent? Thread.NORM_PRIORITY: Thread.MIN_PRIORITY;
                int p2 = o2 instanceof Professor? Thread.MAX_PRIORITY: o2 instanceof ThesisStudent? Thread.NORM_PRIORITY: Thread.MIN_PRIORITY;
                return Integer.compare(p2, p1);
            }
        }
    }
}
