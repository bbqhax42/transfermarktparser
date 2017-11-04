package transfermarkt;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by Chris on 22.10.2016.
 */
public class Transfermarkt implements Runnable{

    private int playday = -1, season = -1;

    private String fileName;
    private String logName;
    private Map<String, String> loginCookies;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
    final int waitTimeLow = 5500; //Minimum wait time between Server requests
    final int waitTimeHigh = 7000; //Maximum wait time between Server requests
    private int minAge = 17; //Minimum age to be parsed
    private int maxAge = 36; //Maximum age to be parsed
    private int minPower = 1; //Minimum power to be parsed
    private int maxPower = 27; //Maximum power to be parsed
    final int maxPower17 = 8; //Maximum power for age 17
    final int maxPower18 = 10; //Maximum power for age 18
    final int maxPower19 = 11; //Maximum power for age 19
    final int maxPower20 = 12; //Maximum power for age 20
    final int maxPower21 = 15; //Maximum power for age 21
    final int maxPower22 = 16; //Maximum power for age 22
    final int maxPower23 = 17; //Maximum power for age 23
    final int maxPower24 = 18; //Maximum power for age 24
    final int maxPower25 = 19; //Maximum power for age 25
    final int maxPower26 = 20; //Maximum power for age 26
    final int maxPower27 = 21; //Maximum power for age 27
    final int maxPower28 = 22; //Maximum power for age 28
    final int maxPower29 = 23; //Maximum power for age 29
    final int maxPower30 = 24; //Maximum power for age 30
    private ObservableList<PlayerTM> data;
    private PlayerListContainer container = PlayerListContainer.instance();
    Button button;


    public Transfermarkt(Map<String, String> loginCookies, int minAge, int maxAge, int minPower, int maxPower, String userAgent, ObservableList<PlayerTM> data, Button button) {
        this.loginCookies=loginCookies;
        this.minAge=minAge;
        this.maxAge=maxAge;
        this.minPower=minPower;
        this.maxPower=maxPower;
        this.userAgent=userAgent;
        this.data=data;
        container.clear();
        this.button=button;
    }

    @Override
    public void run() {
        button.setDisable(true);
        try {
            parsePlaydayAndSeason();


            PrintWriter writer = null;
            fileName = playday + "_" + season + "spielerlistencontainer" + ".ser";
            logName = playday + "_" + season + "log" + ".txt";
            try {
                writer = new PrintWriter(logName, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (writer == null) {
                System.out.println("Writer not intialised");
            }

            Random r = new Random();
            Thread.sleep(r.nextInt(waitTimeHigh - waitTimeLow) + waitTimeLow);

            for (int searchAge = minAge; searchAge <= maxAge; searchAge++) {

                powerLoop:
                for (int searchPower = minPower; searchPower <= maxPower; searchPower++) {

                    if (searchAge == 17 && searchPower >= maxPower17) {
                        break;
                    }
                    if (searchAge == 18 && searchPower >= maxPower18) {
                        break;
                    }
                    if (searchAge == 19 && searchPower >= maxPower19) {
                        break;
                    }
                    if (searchAge == 20 && searchPower >= maxPower20) {
                        break;
                    }
                    if (searchAge == 21 && searchPower >= maxPower21) {
                        break;
                    }
                    if (searchAge == 22 && searchPower >= maxPower22) {
                        break;
                    }
                    if (searchAge == 23 && searchPower >= maxPower23) {
                        break;
                    }
                    if (searchAge == 24 && searchPower >= maxPower24) {
                        break;
                    }
                    if (searchAge == 25 && searchPower >= maxPower25) {
                        break;
                    }
                    if (searchAge == 26 && searchPower >= maxPower26) {
                        break;
                    }
                    if (searchAge == 27 && searchPower >= maxPower27) {
                        break;
                    }
                    if (searchAge == 28 && searchPower >= maxPower28) {
                        break;
                    }
                    if (searchAge == 29 && searchPower >= maxPower29) {
                        break;
                    }
                    if (searchAge == 30 && searchPower >= maxPower30) {
                        break;
                    }


                    System.out.println("Attempting to parse " + searchAge + "/" + searchPower);
                    writer.println("Attempting to parse " + searchAge + "/" + searchPower);
                    Document transferPage;
                    try {
                        transferPage = Jsoup.connect("http://www.onlinefussballmanager.de/010_transfer/transfermarkt.php?seite=1&orderby=7&submit2=Suchen&suche_gestartet=1&alt_bis=" + searchAge + "&alt_von=" + searchAge + "&staerke_von=" + searchPower + "&nation=999&staerke_bis=" + searchPower + "&woche_von=7&woche_bis=7&max_gebot=300000000&rel_mw_abstand=alle&suchpos0=0&suchpos1=1&suchpos2=2&suchpos3=3&suchpos4=4&suchpos5=5&suchpos6=6&suchpos7=7&suchpos8=8&suchpos9=9&suchpos10=10&suchpos11=11&suchpos12=12&suchpos13=13&suchpos14=14")
                                .userAgent(userAgent)
                                .timeout(0)
                                .cookies(loginCookies)
                                .get();
                    } catch (SocketTimeoutException e) {
                        //pause 3-4 min einfuegen

                        System.out.println("SocketTimeoutException detected. Retrying.");
                        Thread.sleep(r.nextInt(waitTimeHigh - waitTimeLow) + waitTimeLow);
                        transferPage = Jsoup.connect("http://www.onlinefussballmanager.de/010_transfer/transfermarkt.php?seite=1&orderby=7&submit2=Suchen&suche_gestartet=1&alt_bis=" + searchAge + "&alt_von=" + searchAge + "&staerke_von=" + searchPower + "&nation=999&staerke_bis=" + searchPower + "&woche_von=7&woche_bis=7&max_gebot=300000000&rel_mw_abstand=alle&suchpos0=0&suchpos1=1&suchpos2=2&suchpos3=3&suchpos4=4&suchpos5=5&suchpos6=6&suchpos7=7&suchpos8=8&suchpos9=9&suchpos10=10&suchpos11=11&suchpos12=12&suchpos13=13&suchpos14=14")
                                .userAgent(userAgent)
                                .cookies(loginCookies)
                                .get();
                    }

                    writer.println("http://www.onlinefussballmanager.de/010_transfer/transfermarkt.php?seite=1&orderby=7&submit2=Suchen&suche_gestartet=1&alt_bis=" + searchAge + "&alt_von=" + searchAge + "&staerke_von=" + searchPower + "&nation=999&staerke_bis=" + searchPower + "&woche_von=7&woche_bis=7&max_gebot=300000000&rel_mw_abstand=alle&suchpos0=0&suchpos1=1&suchpos2=2&suchpos3=3&suchpos4=4&suchpos5=5&suchpos6=6&suchpos7=7&suchpos8=8&suchpos9=9&suchpos10=10&suchpos11=11&suchpos12=12&suchpos13=13&suchpos14=14");

                    //Wait time between Server requests
                    Thread.sleep(r.nextInt(waitTimeHigh - waitTimeLow) + waitTimeLow);


                    //Amount of players in the table
                    Elements tableSize = transferPage.select("#transfermarkt > div.bold > div > table > tbody > tr > td > table.standardHeader > tbody > tr > td > span");
                    int amountOfPlayersInTable = 0, totalPlayersInTheTable = -1, verifierInt = -1;
                    for (Element ele : tableSize) {
                        //writer.println(ele.html());
                        amountOfPlayersInTable = Integer.parseInt(ele.html().replace("Die Suche hat ", "").replace(" Treffer ergeben:", "").trim()); //0-n
                        verifierInt = amountOfPlayersInTable;//verifies that all players have been read
                        //System.out.println(loopSize);
                    }
                    if ((amountOfPlayersInTable / 30) > 0) {
                        totalPlayersInTheTable = amountOfPlayersInTable;
                        amountOfPlayersInTable = 30;
                        //System.out.println(totalPlayersInTheTable/30+1); //amount of pages
                        //System.out.println(totalPlayersInTheTable%30); //entries on lastpage
                    }


                    ArrayList<PlayerTM> tmpStorage = new ArrayList<PlayerTM>();
                    int age = -1, power = -1;
                    //Offset of +2 required

                    for (int k = 1; k <= (totalPlayersInTheTable / 30 + 1); k++) {//Opens all following pages if there are any more available
                        if (!(k == 1)) {
                            if (k == (totalPlayersInTheTable / 30 + 1)) {//checks if the current page is the last page
                                amountOfPlayersInTable = totalPlayersInTheTable % 30;//get the entry amount on the last page
                            }
                            Thread.sleep(r.nextInt(waitTimeHigh - waitTimeLow) + waitTimeLow);
                            transferPage = Jsoup.connect("http://www.onlinefussballmanager.de/010_transfer/transfermarkt.php?seite=" + k + "&orderby=7&submit2=Suchen&suche_gestartet=1&alt_bis=" + searchAge + "&alt_von=" + searchAge + "&staerke_von=" + searchPower + "&nation=999&staerke_bis=" + searchPower + "&woche_von=7&woche_bis=7&max_gebot=300000000&rel_mw_abstand=alle&suchpos0=0&suchpos1=1&suchpos2=2&suchpos3=3&suchpos4=4&suchpos5=5&suchpos6=6&suchpos7=7&suchpos8=8&suchpos9=9&suchpos10=10&suchpos11=11&suchpos12=12&suchpos13=13&suchpos14=14")
                                    .userAgent(userAgent)
                                    .cookies(loginCookies)
                                    .timeout(0)
                                    .get();
                            //pause 3-4 min einfuegen
                            writer.println("http://www.onlinefussballmanager.de/010_transfer/transfermarkt.php?seite=" + k + "&orderby=7&submit2=Suchen&suche_gestartet=1&alt_bis=" + searchAge + "&alt_von=" + searchAge + "&staerke_von=" + searchPower + "&nation=999&staerke_bis=" + searchPower + "&woche_von=7&woche_bis=7&max_gebot=300000000&rel_mw_abstand=alle&suchpos0=0&suchpos1=1&suchpos2=2&suchpos3=3&suchpos4=4&suchpos5=5&suchpos6=6&suchpos7=7&suchpos8=8&suchpos9=9&suchpos10=10&suchpos11=11&suchpos12=12&suchpos13=13&suchpos14=14");
                        }


                        for (int i = 2; i < amountOfPlayersInTable + 2; i++) {


                            int id = -1, ep = -1, tp = -1, bid = -1;
                            String name = null, position = null;
                            boolean hasBidder = false;

                            //All informations about the player with child id i
                            String tablePositionSelector = "#transfermarkt > div.bold > div > table > tbody > tr > td > table.shadow > tbody > tr:nth-child(" + i + ") > td:nth-child(2) > span";
                            String tablePlayerIDSelector = "#transfermarkt > div.bold > div > table > tbody > tr > td > table.shadow > tbody > tr:nth-child(" + i + ") > td:nth-child(3) > table > tbody > tr > td:nth-child(2) > a";
                            String tableAgeSelector = "#transfermarkt > div.bold > div > table > tbody > tr > td > table.shadow > tbody > tr:nth-child(" + i + ") > td:nth-child(4)";
                            String tablePowerSelector = "#transfermarkt > div.bold > div > table > tbody > tr > td > table.shadow > tbody > tr:nth-child(" + i + ") > td:nth-child(5) > strong > font";
                            String tableEPTPSelector = "#transfermarkt > div.bold > div > table > tbody > tr > td > table.shadow > tbody > tr:nth-child(" + i + ") > td:nth-child(6)";
                            String tablePriceSelector = "#transfermarkt > div.bold > div > table > tbody > tr > td > table.shadow > tbody > tr:nth-child(" + i + ") > td:nth-child(7) > nobr";
                            String tableBidderSelector = "#transfermarkt > div.bold > div > table > tbody > tr > td > table.shadow > tbody > tr:nth-child(" + i + ") > td:nth-child(9)";

                            Elements tablePosition = transferPage.select(tablePositionSelector);
                            for (Element ele : tablePosition) {
                                position = ele.html();
                                //System.out.println("Position: " + ele.html());
                            }


                            Elements tablePlayerID = transferPage.select(tablePlayerIDSelector);
                            playerIDAndName:
                            for (Element ele : tablePlayerID) {
                                name = ele.html(); // Retrieves the name
                                //System.out.println(ele.html());
                                String tmpText = ele.toString(); //Retrieves following format: <a class="black_small" href="/player/164258700-Abuhena-Noor" onclick="NewWindow(this.href,'164258700','400','400','yes');return false" onmouseover="Tip('<table cellspacing=1 cellpadding=0 height=100><tr><td colspan=2 valign=top><font color=white><strong>Abuhena Noor</strong> <br>Position: Torwart<br>Herkunft: <img src=/bilder/fahnen3d_nationen/bangladesch.gif height=15 width=28 border=0 align=absmiddle vspace=10 /> <font class=minitext color=#dddddd>(Bangladesch)</font></font><br></td></tr><tr><td valign=bottom><font color=white>Stärke:</font></td><td valign=bottom><font color=#f0ff00 size=4><strong>4</strong></font></td></tr><tr><td valign=top><font color=white>Alter:</font></td><td valign=top><font color=#f0ff00><strong>17 Jahre</a></strong></font></td></tr><tr><td valign=top><font color=white>Gehalt:</font></td><td valign=top><font color=#f0ff00><strong>815 €</strong></font></td></tr><tr><td valign=top><font color=white>Erfahrungspunkte:&nbsp;&nbsp;</font></td><td valign=top><font color=#f0ff00><strong>485</strong></font></td></tr><tr><td valign=top><font color=white>Trainingspunkte:</font></td><td valign=top><font color=#f0ff00><strong>823</strong></font></td></tr><tr><td valign=top><font color=white>Marktwert:</font></td><td valign=top><font color=#f0ff00><strong>2.369.513 €</strong></font></td></tr></table><font color=white><b></b><br><font class=minitext>Der SpielerFU steht auf dem Transfermarkt und wechselt an den Höchstbietenden.</font><br><br></a></a>',WIDTH,204,BGIMG,'/java/tooltipback_spieler.jpg',FADEIN,200,FADEOUT,200,BORDERWIDTH,0)">Abuhena Noor</a>
                                StringBuilder tmp = new StringBuilder();
                                tmp.append(tmpText).setLength(46); //Shortens the String to <a class="black_small" href="/player/164258700
                                tmpText = tmp.substring(37); //Shortens the String to just the ID
                                id = Integer.parseInt(tmpText);
                                //System.out.println("ID: " + id);
                                break playerIDAndName;
                            }

                            Elements tableAge = transferPage.select(tableAgeSelector);
                            for (Element ele : tableAge) {
                                age = Integer.parseInt(ele.html());
                                //System.out.println("Age: " + age);
                            }

                            Elements tablePower = transferPage.select(tablePowerSelector);
                            for (Element ele : tablePower) {
                                power = Integer.parseInt(ele.html());
                                //System.out.println("Power: " + power);
                            }

                            Elements tableEPTP = transferPage.select(tableEPTPSelector);
                            for (Element ele : tableEPTP) {
                                String tmp = ele.html();
                                String[] tmpAry = tmp.split(" / ");
                                ep = Integer.parseInt(tmpAry[0].replace(".", "").trim());
                                tp = Integer.parseInt(tmpAry[1].replace(".", "").trim());
                                //System.out.println("EP: " + NumberFormat.getIntegerInstance().format(ep) + " TP: " + NumberFormat.getIntegerInstance().format(tp));
                            }

                            Elements tablePrice = transferPage.select(tablePriceSelector);
                            for (Element ele : tablePrice) {
                                bid = Integer.parseInt(ele.html().replace(".", "").replace("€", "").trim());
                                //System.out.println("Bid: " + NumberFormat.getIntegerInstance().format(bid));
                            }

                            Elements tableBidder = transferPage.select(tableBidderSelector);
                            for (Element ele : tableBidder) {
                                hasBidder = ele.hasText();
                                //System.out.println("Has Bidder: " + ele.hasText());
                            }
                            PlayerTM tmpSpieler = new PlayerTM(name, position, id, age, power, ep, tp, bid, hasBidder);
                            data.add(tmpSpieler);
                            writer.println(tmpSpieler);
                            System.out.println(tmpSpieler);
                            tmpStorage.add(tmpSpieler);


                        }
                    }


                    //Like this we need only a single search for the List that stores each player type -> optimation is strong with this one
                    PlayerList tmpListe = null;
                    if (age != -1 && power != -1) {
                        tmpListe = container.getSpielerListe(age, power);


                    }
                    if (age == -1 && power == -1) {
                        System.out.println(searchAge + "/" + searchPower + " does not exist on market");
                        writer.println(searchAge + "/" + searchPower + " does not exist on market");
                        //Optimation possible by breaking out if certain powers don't exist multiple times in a row!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    }
                    if (tmpListe != null) {
                        for (int i = 0; i < tmpStorage.size(); i++) {
                            tmpListe.add(tmpStorage.get(i));
                        }
                        System.out.println("Verifier: " + verifierInt + " tmpListe.size(): " + tmpListe.size() + " Check: " + (verifierInt == tmpListe.size()));
                        writer.println("Verifier: " + verifierInt + " tmpListe.size(): " + tmpListe.size() + " Check: " + (verifierInt == tmpListe.size()));
                        //writer.println(tmpListe);
                    }

                    //System.out.println(tmpStorage.size()); //Does it contain all elements?
                }
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            container.setPlayday(playday + 1);
            System.out.println(container.getPlayday());
            container.setSeason(season);
            System.out.println("Saving file to: " +fileName);
            Random r = new Random();
            Thread.sleep(r.nextInt(25 - 15) + 15);
            System.out.println("Saving finished");
            container.save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    button.setDisable(false);

    }

    private void parsePlaydayAndSeason() throws IOException {
        Document playdaypage;
        //parsing the playday
        playdaypage = Jsoup.connect("http://www.onlinefussballmanager.de/head-int.php?spannend=0")
                .userAgent(userAgent)
                .cookies(loginCookies)
                .ignoreContentType(true)
                .get();

        Elements playdays = playdaypage.select("body > div.headFrame.pos-rel.clearfix > div.float.bgFront.pos-rel > div.pos-abs.yellow.clearfix.infoBlock > p > span:nth-child(1)");
        for (Element ele : playdays) {
            this.playday = Integer.parseInt(ele.html().trim());
        }
        if (this.playday == -1) {//date could not be parsed
            System.exit(-1);
        }


        Elements seasons = playdaypage.select("body > div.headFrame.pos-rel.clearfix > div.float.bgFront.pos-rel > div.pos-abs.yellow.clearfix.infoBlock > p > span:nth-child(2)");
        for (Element ele : seasons) {
            this.season = Integer.parseInt(ele.html().trim());
        }
        if (this.playday == -1) {//date could not be parsed
            System.exit(-1);
        }

        System.out.println("Spieltag und Saison detected: " + season + "/" + playday);


    }
}