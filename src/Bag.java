import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class Bag {
    private String name;
    private Random rand = new Random();
    private ArrayList<Integer> pebbles = new ArrayList<Integer>();

    //constructor assigns name and fills from csv
    public Bag(String name, String filename) throws IOException {
        this.name = name;
        if(!filename.equals("none"))
            fillBag(filename);
    }

    //fill bag from csv when init. game
    public void fillBag(String filename) throws IOException {
        //read the csv as single line
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            ArrayList<String> myList = new ArrayList<>(Arrays.asList(line.split(",")));

            //add each item into pebbles list
            for (String item:myList) {
                item = item.replace(" ","");
                addPebble(Integer.parseInt(item));
            }
        }
        //catch error
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            //print nono restart code
        }
    }

    //fill bag with another bag of pebbles, i.e when black bag empty
    public void fillBag(ArrayList<Integer> weights){
        this.pebbles=weights;
    }

    public void emptyBag(){
        this.pebbles.clear();
    }

    //add a single pebble to bag
    public void addPebble(int weight){
        this.pebbles.add(weight);
    }

    //remove a single pebble random from bag
    public int takePebble(){
        int weight = rand.nextInt(pebbles.size());
        int wweight = this.pebbles.get(weight);
        this.pebbles.remove(weight);
        return wweight;
    }

    //get name
    public String getName() {
        return name;
    }

    //get weights
    public ArrayList<Integer> getWeights(){
        return pebbles;
    }

}

//for tests
//check files being read proper
//check bag methods work proper
//check right bags being init. proper