import java.util.Arrays;

public class ComputerManager {
    public int size;
    public int userCounter;
    public Computer[] computers;
    public int professors;
    public ComputerManager(int size){
        this.size = size;
        this.userCounter = 0;
        this.professors =0;
        computers = new Computer[size];
        Arrays.fill(this.computers,new ComputerManager.Computer());
    }

    public synchronized void reserveStudent(int userID) {
        //attende finché c'è almeno un professore o finché l'aula è piena
        while (this.professors > 0 || this.userCounter == this.computers.length)
            try {
                wait();}
            catch(Exception e) {
                e.printStackTrace();
            }
        //cerca un posto libero e lo occupa
        for(ComputerManager.Computer e:this.computers){
            if(e.free) {
                e.setPossessor(userID);
                break;
            }
        }
        this.userCounter++;
        System.out.println("lo studente "+userID+" ha preso un pc.");
        notifyAll();
    }

    public synchronized void releaseStudent(int userID){
        //precondizione: il laboratorio deve essere popolato
        while (this.userCounter == 0)
            try {
                wait();}
            catch(Exception e) {
                e.printStackTrace();
            }

        //lo studente libera il proprio posto
        for(ComputerManager.Computer e:this.computers)
            if(e.possessor == userID) {
                e.unsetPossessor();
                break;
            }
        this.userCounter--;
        System.out.println("lo studente "+userID+" ha rilasciato un pc.");
        notifyAll();
    }

    public synchronized void reserveThesisStudent(int userID) {
        //precondizioni: non devono esserci professori
        while (this.professors > 0 || !this.computers[userID % this.size].free)
            try {
                wait();}
            catch(Exception e) {
                e.printStackTrace();
            }
        this.computers[userID % this.size].setPossessor(userID);
        this.userCounter++;
        System.out.println("Il tesista "+userID+" ha preso il pc " + userID % this.size + ".");
        notifyAll();
    }

    public synchronized void releaseThesisStudent(int userID) {
        //precondizioni: il pc deve essere stato occupato da userID
        while (this.computers[userID % this.size].possessor != userID)
            try {
                wait();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        this.computers[userID % this.size].unsetPossessor();
        this.userCounter--;

        System.out.println("Il tesista "+userID+" ha rilasciato il pc "+ userID % this.size + ".");
        notifyAll();
    }

    public synchronized void reserveProfessor(int userID) {
        //precondizioni: l'aula deve essere vuota
        while (this.userCounter > 0)
            try {
                wait();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        this.professors++;
        System.out.println("Il professore "+userID+" si è riservato l'aula.");
        notifyAll();
    }

    public synchronized void releaseProfessor(int userID) {
        //precondizioni: l'aula deve essere occupata da almeno un professore
        while (this.professors == 0)
            try {
                wait();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        this.professors--;
        System.out.println("Il professore "+userID+" sta lasciando l'aula.");
        notifyAll();
    }
    public static class Computer{
        public boolean free;
        public int possessor;


        public Computer(){
            this.free = true;
            this.possessor = -1;
        }

        public void setPossessor(int newPossessor){
            this.free = false;
            this.possessor = newPossessor;
        }

        public void unsetPossessor(){
            this.free = true;
            this.possessor = -1;
        }
    }
}
