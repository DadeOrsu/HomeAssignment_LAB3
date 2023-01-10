public class Person implements Runnable {
    public String name;
    public int number;
    public Person(String name, int number){
        this.name = name;
        this.number = number;
    }
    public void run(){
        //Metto per ipotesi tre secondi per soddisfare una richiesta
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() +" ha servito "+ this.name + "(n. "+this.number+")");
    }
}
