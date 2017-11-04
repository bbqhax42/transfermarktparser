package transfermarkt;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


/**
 * Created by Chris on 25.10.2016.
 */
public class SpielerWechsel {
    int playday = -1, season = -1;
    String version = "A";
    String fileName = null;
    String logName = null;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
    private Map<String, String> loginCookies;


    final String userID = "brotkatenils";
    final String userPass = "dertollenils";
    private PlayerListContainer container;

    private Scanner test = null;

    public SpielerWechsel() {

        try {


            try {

                Connection.Response homePageResponse = Jsoup
                        .connect("http://www.onlinefussballmanager.de")
                        .userAgent(userAgent)
                        .method(Connection.Method.GET)
                        .execute();


                Connection.Response loginResponse = null;

                loginResponse = Jsoup.connect("http://www.onlinefussballmanager.de")
                        .userAgent(userAgent)
                        .data("login", userID)
                        .data("password", userPass)
                        .data("remember_me", "1")
                        .data("LoginButton", "Login")
                        .data("js_activated", "1")
                        .data("legacyLoginForm", "1")
                        .method(Connection.Method.POST)
                        .execute();


                loginCookies = loginResponse.cookies();
                //System.out.println("Login response code: " + loginResponse.statusCode());

                PrintWriter writer = null;

                parsePlaydayAndSeason();
                if (playday == 0) {
                    fileName = "34" + "_" + --season + "spielerlistencontainer" + ".ser";
                    logName = "SW" + "34" + "_" + season + "SpielerWechsel" + ".txt";
                } else {
                    fileName = (playday - 1) + "_" + season + "spielerlistencontainer" + ".ser";
                    logName = "SW" + (playday - 1) + "_" + season + "SpielerWechsel" + ".txt";
                }
                System.out.println("Playday: " + playday);
                container = container.load(fileName);

                System.out.println("Spieltag und Saison parsed from container: " + container.getSeason() + "/" + container.getPlayday());

                if (container.getPlayday() == 35) {
                    container.incrementAge();
                    container.setPlayday(0);
                    container.setSeason(season + 1);
                    System.out.println("New season, incrementing age.");
                }

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
                Thread.sleep(r.nextInt(10000 - 9000) + 1);
                Document transferPage;


                //this is the part
                transferPage = Jsoup.connect("http://www.onlinefussballmanager.de/transfer/spielerwechsel_export.php?select_spieltag=" + playday + "&seite=alle")
                        .userAgent(userAgent)
                        .cookies(loginCookies)
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .get();


                r = new Random();
                Thread.sleep(r.nextInt(10000 - 9000) + 1);


                //opens the previous anaylsis of offered players and marks which players have been sold or not and updates the object file

                Scanner readFile = new Scanner(transferPage.toString());
                if (readFile.hasNextLine()) {
                    //Skips first 25 rows of useless data
                    if (readFile.nextLine().equals("<html>")) {
                        for (int i = 0; i < 24; i++) {
                            if (readFile.hasNextLine())
                                readFile.nextLine();
                        }
                        while (readFile.hasNextLine()) {
                            String name = null, pos = null, seller = null, buyer = null;
                            int power = -1, age = -1, price = -1;

                            //skip position in table, day, season, sales club and buyer club
                            for (int i = 0; i < 3; i++) {
                                if (readFile.hasNextLine())
                                    readFile.nextLine();
                            }

                            //parse seller
                            if (readFile.hasNextLine())
                                seller = readFile.nextLine().replace("<td>", "").replace("</td>", "").trim();
                            //System.out.println(seller);

                            //parse buyer
                            if (readFile.hasNextLine())
                                buyer = readFile.nextLine().replace("<td>", "").replace("</td>", "").trim();
                            //System.out.println(buyer);

                            //parse name
                            if (readFile.hasNextLine())
                                name = readFile.nextLine().replace("<td>", "").replace("</td>", "").trim();
                            //System.out.println(name);

                            //parse pos
                            if (readFile.hasNextLine())
                                pos = readFile.nextLine().replace("<td>", "").replace("</td>", "").trim();
                            //System.out.println(pos);

                            //parse age
                            if (readFile.hasNextLine())
                                age = Integer.parseInt(readFile.nextLine().replace("<td>", "").replace("</td>", "").trim());
                            //System.out.println(age);

                            //parse power
                            if (readFile.hasNextLine())
                                power = Integer.parseInt(readFile.nextLine().replace("<td>", "").replace("</td>", "").trim());
                            //System.out.println(power);
                            String abc = null;
                            //parse final sales price
                            if (readFile.hasNextLine())
                                price = Integer.parseInt(readFile.nextLine().replace("<td>", "").replace("</td>", "").trim());

                            //System.out.println(price);

                            //skip last 2 lines to prepare for next player or end of file
                            for (int i = 0; i < 2; i++) {
                                if (readFile.hasNextLine())
                                    readFile.nextLine();
                            }
                            if (pos != null) {
                                PlayerTM tmpSpieler = null;
                                PlayerList playerList = null;
                                if (container.getPlayday() == 9 || container.getPlayday() == 18 || container.getPlayday() == 27 || container.getPlayday() == 0) {
                                    if (container.getPlayday() == 0) age = age - 1;
                                    for (int i = power - 1; i <= power + 1; i++) {
                                        playerList = container.getSpielerListe(age, i);
                                        if (playerList != null)
                                            tmpSpieler = playerList.findSpielerTM(name, pos);
                                        if (tmpSpieler != null) {
                                            tmpSpieler.setPower(power);
                                            break;
                                        }
                                    }
                                    System.out.println(name + " " + pos);
                                    writer.println(name + " " + pos);
                                    System.out.println(playerList);
                                    writer.println(playerList);
                                } else {
                                    playerList = container.getSpielerListe(age, power);
                                    if (playerList == null) {
                                        tmpSpieler = null;
                                    } else {
                                        tmpSpieler = playerList.findSpielerTM(name, pos);
                                        System.out.println(name + " " + pos);
                                        writer.println(name + " " + pos);
                                        System.out.println(playerList);
                                        writer.println(playerList);
                                    }
                                }
                                if (tmpSpieler == null) {
                                    System.out.println("Update failure");
                                    writer.println("Update failure");
                                }
                                if (tmpSpieler != null) {
                                    System.out.println(tmpSpieler);
                                    System.out.println("New Price: " + price);
                                    writer.println(tmpSpieler);
                                    writer.println("New Price: " + price);
                                    tmpSpieler.setBid(price);
                                    tmpSpieler.setHasBidder(true);
                                    tmpSpieler.setSeller(seller);
                                    tmpSpieler.setBuyer(buyer);
                                    writer.println("Transfered from: " + seller + " to " + buyer);
                                    System.out.println("Update success");
                                    writer.println("Update success");
                                }
                            }
                        }

                    }
                }
                writer.close();
            } catch (
                    IOException e1) {
                e1.printStackTrace();
            }

            container.save("SW" + fileName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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











