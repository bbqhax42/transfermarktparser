package transfermarkt;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Chris on 23.10.2016.
 */
public class PlayerListContainer implements Serializable {

    private static final long serialVersionUID = 7219632990740128143L;
    private static PlayerListContainer unique = null;
    private ArrayList<PlayerList> allSpielerListen;
    private int playday, season;

    private PlayerListContainer() {
        allSpielerListen = new ArrayList<PlayerList>();
        int i;


        for (int j = 17; j <= 36; j++) {//17/1-36/27
            for (i = 1; i <= 27; i++) {
                //System.out.println("Adding playerlist with Age: " + j + " and Power: " + i);
                this.allSpielerListen.add(new PlayerList(j, i));
            }
        }

    }

    public static PlayerListContainer instance() {
        if (unique == null)
            unique = new PlayerListContainer();
        return unique;
    }

    public void incrementAge() {
        for (int j = 17; j <= 36; j++) {//17/1-36/27
            for (int i = 1; i <= 27; i++) {
                PlayerList tmpList = getSpielerListe(j, i);
                if (tmpList != null) tmpList.incrementAge();
            }
        }
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getPlayday() {
        return playday;
    }

    public void setPlayday(int playday) {
        this.playday = playday;
    }

    public int size() {
        return allSpielerListen.size();
    }

    public boolean isEmpty() {
        return allSpielerListen.isEmpty();
    }

    public PlayerList getSpielerListe(int age, int power) {
        //System.out.println("Age: " + age + " Power: " + power);
        for (int i = 0; i < allSpielerListen.size() - 1; i++) {
            PlayerList tmp = allSpielerListen.get(i);
            if (tmp.getAge() == age && tmp.getPower() == power) {
                //System.out.println("Playerlist found");
                return tmp;
            }
        }
        return null;
    }

    public boolean add(PlayerList playerList) {
        return allSpielerListen.add(playerList);
    }


    public void clear() {
        for (int i = 0; i < allSpielerListen.size() - 1; i++) {
            PlayerList tmp = allSpielerListen.get(i);
            tmp.clear();
        }
    }


    public Iterator<PlayerList> iterator() {
        return allSpielerListen.iterator();
    }

    public static PlayerListContainer load(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        ois = new ObjectInputStream(new FileInputStream(fileName));
        unique = (PlayerListContainer) ois.readObject();
        ois.close();
        return unique;
    }

    public void save(String fileName) throws IOException {
        //saves everything into an object so the spielerwechsel class can compare the data later on
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(unique);
        oos.close();

    }

    public void saveDB(Database db) {
        for (Iterator<PlayerList> iter = this.iterator(); iter.hasNext(); ) {
            try {
                db.addPlayerList(iter.next());
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

}
