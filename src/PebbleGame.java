import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PebbleGame {
    //lists and vars
    ArrayList<PebbleGame.Player> players = new ArrayList<PebbleGame.Player>();
    ArrayList<Bag> bags = new ArrayList<Bag>();
    Random rand = new Random();
    int discardMarker = 3;

    //initialise the bags from csv
    public void initialiseBags() throws IOException {
        bags.add(new Bag("X","example_file_2.csv"));
        bags.add(new Bag("Y","example_file_2.csv"));
        bags.add(new Bag("Z","example_file_2.csv"));
        bags.add(new Bag("A","none"));
        bags.add(new Bag("B","none"));
        bags.add(new Bag("C","none"));
    }

    //initialisePlayers(){}

    //main class
    public static void main(String[] args) throws IOException {
        PebbleGame game = new PebbleGame();
        game.initialiseBags();

        System.out.println("""
                Welcome to PebbleGame!!
                You will be asked to enter the number of players.
                and then for the location of three files in turn containing comma seperated interger values for the pebble weights.
                The integer values must be strictly positive
                The game will then be simulated, and output wirrten to files in this directory.

                Please enter the number of players:""");
        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();

    }

    //player class
    public class Player {
        //lists and vars
        ArrayList<Integer> Hand = new ArrayList<Integer>();

        //constructor fills player hand
        Player(){
            fillHand();
        }

        //fill hand of player with 10 pebbles from black bags
        public void fillHand() {
            for(int x=0;x<10;x++){
                takePebble();
            }
        }

        //take a pebble from a random black bag
        private void takePebble(){
            boolean Picked = false;
            while(!Picked){
                int bagMarker = rand.nextInt(2);
                if(!bags.get(bagMarker).getWeights().isEmpty()){
                    Hand.add(bags.get(bagMarker).takePebble());
                    discardMarker = bagMarker;
                    Picked = true;
                }
            }
        }

        //discard a pebble from hand into a certain white bag
        private void discardPebble(int discardMarker, int pebble){
            switch (discardMarker) {
                case 0 -> {
                    bags.get(3).addPebble(pebble);
                    Hand.remove((Integer) pebble);
                }
                case 1 -> {
                    bags.get(4).addPebble(pebble);
                    Hand.remove((Integer) pebble);
                }
                case 2 -> {
                    bags.get(5).addPebble(pebble);
                    Hand.remove((Integer) pebble);
                }
            }
        }

        //check if player has won
        private Boolean hasWon(){
            Integer sum = 0;
            for(Integer i : Hand)
                sum += i;

            if(sum==100){
                //END ALL THE THREADS BRO YEEHAW notfiy
                //print weiner
                return true;
            }
            return false;
        }
    }
}