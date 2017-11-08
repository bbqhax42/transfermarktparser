package transfermarkt;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Hyperlink;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Created by Chris on 23.10.2016.
 */
public class PlayerTM implements Serializable {
    private static final long serialVersionUID = 1088637054893063932L;
    private transient SimpleStringProperty name = null;
    private transient SimpleStringProperty  pos = null, seller=null, buyer=null;
    private transient SimpleIntegerProperty  id, age, power, ep, tp, awp, bid;
    private transient SimpleBooleanProperty hasBidder;
    private transient Hyperlink hyperlink;


    public PlayerTM(String name, String pos, int id, int age, int power, int ep, int tp, int bid, boolean hasBidder) {
        setName(name);
        setPos(pos);
        setId(id);
        setAge(age);
        setPower(power);
        setEp(ep);
        setTp(tp);
        setAwp(ep, tp);
        setBid(bid);
        setHasBidder(hasBidder);
        setHyperlink(id);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeUTF(name.getValueSafe());
        s.writeUTF(pos.getValueSafe());
        s.writeInt(id.get());
        s.writeInt(age.get());
        s.writeInt(power.get());
        s.writeInt(ep.get());
        s.writeInt(tp.get());
        s.writeInt(awp.get());
        s.writeInt(bid.get());
        s.writeBoolean(hasBidder.get());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        name.set(s.readUTF());
        pos.set(s.readUTF());
        seller.set(s.readUTF());
        buyer.set(s.readUTF());
        id.set(s.readInt());
        age.set(s.readInt());
        power.set(s.readInt());
        ep.set(s.readInt());
        tp.set(s.readInt());
        awp.set(s.readInt());
        bid.set(s.readInt());
        hasBidder.set(s.readBoolean());
        // set values in the same order as writeObject()
    }

    public Hyperlink getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(int id) {
        this.hyperlink= new Hyperlink("http://www.onlinefussballmanager.de/player/"+id);
    }

    public String getSeller() {
        return seller.getValue();
    }

    public void setSeller(String seller) {
        this.seller=new SimpleStringProperty(seller);
    }

    public String getBuyer() {
        return buyer.getValue();
    }

    public void setBuyer(String buyer) {
        this.buyer=new SimpleStringProperty(buyer);
    }

    public String getPos() {
        return pos.getValue();
    }

    public void setPos(String pos) {
        this.pos=new SimpleStringProperty(pos);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name=new SimpleStringProperty(name);
    }

    public void setAge(int age) {
        this.age= new SimpleIntegerProperty(age);
    }

    public void setAwp(int ep, int tp) {
        if(ep!=0&&tp!=0){
        this.awp=new SimpleIntegerProperty((ep * tp * 2) / (ep + tp));}
        else this.awp=new SimpleIntegerProperty(0);
    }

    public void setEp(int ep) {
        this.ep=new SimpleIntegerProperty(ep);
    }

    public void setBid(int bid) {
        this.bid=new SimpleIntegerProperty(bid);
    }

    public void setId(int id) {
        this.id=new SimpleIntegerProperty(id);
    }


    public void setPower(int power) {
        this.power=new SimpleIntegerProperty(power);
    }

    public void setTp(int tp) {
        this.tp=new SimpleIntegerProperty(tp);
    }

    public void setHasBidder(boolean hasBidder) {
        this.hasBidder= new SimpleBooleanProperty(hasBidder);
    }

    public int getAge() {
        return age.get();
    }

    public int getAwp() {
        return awp.get();
    }

    public int getEp() {
        return ep.get();
    }

    public int getBid() {
        return bid.get();
    }

    public int getId() {
        return id.get();
    }

    public boolean isHasBidder() {
        return hasBidder.get();
    }

    public int getPower() {
        return power.get();
    }

    public int getTp() {
        return tp.get();
    }


    private String bidString() {
        StringBuilder lul = new StringBuilder();
        lul.append(NumberFormat.getIntegerInstance().format(bid.get())).setLength(11);
        for (int i = 0; i < 11; i++) {
            int rofl = lul.charAt(i);
            if (rofl == 0) {
                lul.replace(i, i + 1, " ");
            }
        }
        return lul.toString();
    }

    private String eptpawpString() {
        StringBuilder lul = new StringBuilder();
        lul.append(NumberFormat.getIntegerInstance().format(ep.get())).append("/").append(NumberFormat.getIntegerInstance().format(tp.get())).append(" ").append(NumberFormat.getIntegerInstance().format(awp.get())).setLength(20);
        for (int i = 19; i >= 0; i--) {
            int rofl = lul.charAt(i);
            if (rofl == 0) {
                lul.replace(i, i + 1, " ");
            }
        }
        return lul.toString();
    }

    private String nameString() {
        StringBuilder lul = new StringBuilder();
        lul.append(name.get()).setLength(25);
        for (int i = 0; i < 25; i++) {
            int rofl = lul.charAt(i);
            if (rofl == 0) {
                lul.replace(i, i + 1, " ");
            }
        }
        return lul.toString();
    }

    private String posString() {
        StringBuilder lul = new StringBuilder();
        lul.append(pos.get()).setLength(3);
        for (int i = 0; i < 3; i++) {
            int rofl = lul.charAt(i);
            if (rofl == 0) {
                lul.replace(i, i + 1, " ");
            }
        }
        return lul.toString();
    }

    private String agepowerString() {
        StringBuilder lul = new StringBuilder();
        lul.append(age.get()).append("/").append(power.get()).setLength(5);
        int rofl = lul.charAt(4);
        if (rofl == 0) {
            lul.replace(4, 4 + 1, " ");
        }
        return lul.toString();
    }



    @Override
    public String toString() {
        return posString() + " " + nameString() + " "+ agepowerString() + " " + eptpawpString() + " " + bidString();
    }
}
