package transfermarkt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Chris on 23.10.2016.
 */
public class PlayerList implements Serializable {

    private static final long serialVersionUID = 9208573032759089194L;
    private int age, power;
    private ArrayList<PlayerTM> allPlayerTM;

    public PlayerList(int age, int power) {
        setAge(age);
        setPower(power);
        allPlayerTM = new ArrayList<PlayerTM>();

    }



    public void clear() {
        allPlayerTM.clear();
    }

    public int getAge() {
        return age;
    }

    public int getPower() {
        return power;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int size() {
        return allPlayerTM.size();
    }


    public PlayerTM findSpielerTM(String name, String pos) {
        PlayerTM tmpPlayer;
        for (int i = 0; i <= allPlayerTM.size() - 1; i++) {
            //System.out.println("size" + allPlayerTM.size());
            tmpPlayer = allPlayerTM.get(i);
            if (tmpPlayer.getName().replaceAll("[^a-zA-Z]+", "").equals(name.replaceAll("[^a-zA-Z]+", "")) && tmpPlayer.getPos().replaceAll("[^a-zA-Z]+", "").equals(pos.replaceAll("[^a-zA-Z]+", ""))) {
                return tmpPlayer;
            }
        }
        return null;
    }

    public void add(PlayerTM playerTM) {
        if (!this.allPlayerTM.contains(playerTM)) this.allPlayerTM.add(playerTM);
    }

    public Iterator<PlayerTM> iterator() {
        return allPlayerTM.iterator();
    }

    public String toString() {
        return ("Spielerliste: " + age + "/" + power);
    }


    public void incrementAge() {
        PlayerTM tmpPlayer;
        for (int i = 0; i <= allPlayerTM.size() - 1; i++) {
            //System.out.println("size" + allPlayerTM.size());
            tmpPlayer = allPlayerTM.get(i);
            tmpPlayer.setAge(tmpPlayer.getAge()+1);
        }
    }

}
