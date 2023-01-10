import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;


public class DungeonAdventures {

    private static class Player{
        private final String playerName;
        private final int maxHealthPoints;
        private int healthPoints;
        private int potionPoints;
        public Player(String playerName){
            this.playerName = playerName;
            this.maxHealthPoints =ThreadLocalRandom.current().nextInt(50, 100 + 1);
            this.healthPoints = this.maxHealthPoints;
            this.potionPoints = ThreadLocalRandom.current().nextInt(20, 50 + 1);
        }

        public int drinkPotion(){
            int healthGained = 0;
            if(this.potionPoints > 0 && this.healthPoints < this.maxHealthPoints) {
                healthGained = ThreadLocalRandom.current().nextInt(0, this.potionPoints + 1);
                if(this.healthPoints + healthGained > this.maxHealthPoints)
                    this.healthPoints =  this.maxHealthPoints;
                this.healthPoints += healthGained;
                this.potionPoints -= healthGained;
            }
            return healthGained;
        }

        public int attack(Monster monster){
            int lostHP_monster = ThreadLocalRandom.current().nextInt(0, monster.healthPoints + 1);
            monster.healthPoints -= lostHP_monster;
            return lostHP_monster;
        }

        public String getPlayerName(){return this.playerName;}
        public int getHealthPoints() {
            return healthPoints;
        }
    }



    private static class Monster{
        private int healthPoints;
        public Monster(){
            this.healthPoints = ThreadLocalRandom.current().nextInt(50, 100 + 1);
        }

        public int attack(Player player){
            int lostHP_player = ThreadLocalRandom.current().nextInt(0, player.healthPoints + 1);
            player.healthPoints -= lostHP_player;
            return lostHP_player;
        }

        public int getHealthPoints() {
            return healthPoints;
        }
    }


    private static class Service implements Runnable {
        private final Socket socket;
        Service(Socket socket) {
            this.socket = socket; }
        public void run() {
            System.out.println("Currently serving: " + this.socket.toString());
            try (Scanner in = new Scanner(socket.getInputStream());
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
            {
             String name = in.nextLine();
             System.out.println(this.socket + ": " + name + " entra in partita.");
             Player player = new Player(name);
             Monster monster = new Monster();
             out.println("Un Mostro si palesa di fronte a " + player.playerName + "("+player.healthPoints + " HP)! Cosa deve fare?");

             int playerChoice;
                 do {
                     playerChoice = in.nextInt();
                     switch (playerChoice) {
                         case 1:
                             int monsterDamage = monster.attack(player);
                             int playerDamage = player.attack(monster);
                             if(monster.getHealthPoints() == 0){
                                 monster = new Monster();
                                 out.println(player.getPlayerName() + " ha vinto! Si è presentato un nuovo Mostro(" + monster.getHealthPoints()+" HP)! cosa deve fare?");
                             }

                            else if(player.getHealthPoints() == 0){
                                 out.println("GAME OVER");
                             }

                             else out.println(player.getPlayerName() + " si scontra col Mostro: " + player.getPlayerName() + " subisce " + monsterDamage + " danni(" + player.getHealthPoints() + " HP)." +
                                     " Il Mostro subisce " + playerDamage + " danni(" + monster.getHealthPoints() + " HP).");
                             break;
                         case 2:
                             if(player.potionPoints !=0) {
                                 int currentPlayerHealth = player.getHealthPoints();
                                 out.println(player.getPlayerName() + " ha recuperato un po' di vita!(" + currentPlayerHealth + "+" + player.drinkPotion() + ")");
                             }
                             else out.println(player.getPlayerName() + " ha finito la pozione!");
                             break;
                         case 3:
                             out.println("GAME OVER");
                             break;
                         default:
                             out.println("Scelta errata (1,2,3)");
                     }
                 }while (playerChoice !=3);

            } catch (Exception e) { System.out.println("Error:" + socket); }
        }
    }

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(10000)) {
            System.out.println("Server di DungeonAdventures sta aspettando sull'indirizzo ip: "+ InetAddress.getLocalHost());
            ExecutorService pool = Executors.newCachedThreadPool();
            while (true) {
                pool.execute(new Service(listener.accept()));
            }
        }
    }
}