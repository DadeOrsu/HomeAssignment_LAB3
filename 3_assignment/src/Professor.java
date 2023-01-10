public class Professor implements Runnable{

    public ComputerManager computerManager;
    public int identifier;
    public int k;
    public Professor(ComputerManager computerManager, int identifier, int k){

        this.computerManager = computerManager;
        this.identifier = identifier;
        this.k = k;
    }

    public void run() {
        while(this.k > 0){
            //acquisizione
            this.computerManager.reserveProfessor(this.identifier);


            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }

            //rilascio

            this.computerManager.releaseProfessor(this.identifier);
            this.k--;

            //tra un accesso e l'altro aspetta tre secondi
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}