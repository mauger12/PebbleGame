import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PebbleGame {
    //lists and vars
    static ArrayList<PebbleGame.Player> players = new ArrayList<PebbleGame.Player>();
    public static ArrayList<Bag> bags = new ArrayList<Bag>(); //needs to be atomic
    static ArrayList<Thread> threads = new ArrayList<Thread>();
    Random rand = new Random();
    int discardMarker = 0;

    //initialise the bags from csv
    public void initialiseBags(String f1,String f2,String f3) throws IOException {
        bags.clear();
        bags.add(new Bag("X",f1));
        bags.add(new Bag("Y",f2));
        bags.add(new Bag("Z",f3));
        bags.add(new Bag("A","none"));
        bags.add(new Bag("B","none"));
        bags.add(new Bag("C","none"));
    }

    public void initialisePlayers(int numPlayers){
        players.clear();
        for(int i = 0; i < numPlayers; i++){
            players.add(new Player(i));
            Thread player = new Thread(players.get(i));
            threads.add(player);
        }

        for(Thread player : threads){
            player.start();
        }
    }

    //main class
    public static void main(String[] args) throws IOException {
        PebbleGame game = new PebbleGame();
        Scanner sc = new Scanner(System.in);
        boolean setup = false;
        String f1= "", f2= "", f3 = "";
        int i = 0;

        System.out.println("""
                Welcome to PebbleGame!!
                You will be asked to enter the number of players.
                and then for the location of three files in turn containing comma separated integer values for the pebble weights.
                The integer values must be strictly positive.
                The game will then be simulated, and output written to files in this directory.""");

        while(!setup){
            setup = true;
            System.out.println("""
                
                Please enter the number of players:""");

            try{
                String playerCount = sc.next();
                inputCheck(playerCount);
                i = Integer.parseInt(playerCount);
                System.out.println("Please enter location of bag 0 to load:");
                f1 = sc.next();
                inputCheck(f1);
                System.out.println("Please enter location of bag 1 to load:");
                f2 = sc.next();
                inputCheck(f2);
                System.out.println("Please enter location of bag 2 to load:");
                f3 = sc.next();
                inputCheck(f3);

                game.initialiseBags(f1,f2,f3);
                //check if num pebbles >= players*11
                int amount = bags.get(0).getWeights().size() + bags.get(1).getWeights().size() + bags.get(2).getWeights().size();
                if(amount<=11*i){
                    System.out.println("Not enough pebbles for number of players, retry");
                    setup = false;
                }
            }
            catch (Exception e){
                //if input not good
                setup = false;
            }
        }
        game.initialisePlayers(i);
    }

    public static void inputCheck(String inp){
        if(inp.equals("E")){
            System.exit(0);
        }
    }

    //player class
    public class Player implements Runnable{
        //lists and vars
        private ArrayList<Integer> hand = new ArrayList<Integer>();
        private Integer playNum;
        private Integer tc = 0;

        //constructor fills player hand
        Player(Integer playNum){
            this.playNum = playNum;
            try {
                FileWriter logsetup = new FileWriter("player" + this.playNum + ".txt", false);
                logsetup.write("");
                logsetup.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            fillHand();
            while(!hasWon()){
                discardPebble(discardMarker);
                takePebble();
                tc++;
            }
            System.out.println("Player" + playNum + " has won! Ending game.");
            System.exit(0);
        }

        private ArrayList<Integer> getHand(){
            return hand;
        }

        //fill hand of player with 10 pebbles from black bags
        private void fillHand() {
            for(int x=0;x<10;x++){
                takePebble();
            }
        }

        //take a pebble from a random black bag
        private synchronized void takePebble(){
            boolean Picked = false;
            while(!Picked){
                int bagMarker = rand.nextInt(3);
                if(!bags.get(bagMarker).getWeights().isEmpty()){
                    hand.add(bags.get(bagMarker).takePebble());
                    discardMarker = bagMarker;
                    takeLog(true,hand.get(hand.size() - 1),bags.get(discardMarker).getName() );
                    Picked = true;
                }
                else{
                    //if a black bag is empty, fill it with white bag at +3 index of itself
                    bags.get(bagMarker).fillBag(bags.get(bagMarker+3).getWeights());
                    //empty the white bag
                    bags.get(bagMarker+3).emptyBag();
                }
            }
        }

        //discard a pebble from hand into a certain white bag
        private synchronized void discardPebble(int discardMarker){
            int pebble = rand.nextInt(10);
            int ppebble = hand.get(pebble);
            bags.get(discardMarker+3).addPebble(ppebble);
            hand.remove(pebble);
            takeLog(false, ppebble, bags.get(discardMarker+3).getName());
        }

        //check if player has won
        private Boolean hasWon(){
            Integer sum = 0;
            for(Integer i : hand)
                sum += i;

            if(sum==100){
                return true;
            }
            return false;
        }

        private void takeLog(boolean take, int pebble, String bagname){
            FileWriter fileWriter;

            //File f = new File("player" + this.playNum + ".txt");
            try {
                fileWriter = new FileWriter("player" + this.playNum + ".txt",true);
                if (take) {
                    fileWriter.write("Player" + playNum + " has drawn a " + pebble + " from bag " + bagname + "\n");
                } else {
                    fileWriter.write("Player" + playNum + " has discarded a " + pebble + " to bag " + bagname + "\n");
                }
                fileWriter.write(hand.toString() + "\n");
                fileWriter.close();
            }
            catch (IOException e){
            }
        }
    }
}