public class ThesisStudent implements Runnable{

    public ComputerManager computerManager;
    public int identifier;
    public int k;
    public ThesisStudent(ComputerManager computerManager, int identifier, int k){
        this.computerManager = computerManager;
        this.identifier = identifier;
        this.k = k;
    }


    public void run() {
        while (this.k > 0) {
            //acquisizione
            this.computerManager.reserveThesisStudent(this.identifier);

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }


            //rilascio
            this.computerManager.releaseThesisStudent(this.identifier);
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