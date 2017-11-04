package transfermarkt;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.h2.tools.Server;

/**
 * Created by Chris on 01.11.2016.
 */
public class Database {

    private Connection conn = null;
    private Server server = null;
    private int playday = -1, season = -1;

    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        conn = DriverManager.
                getConnection("jdbc:h2:~/ofm", "Chris", "asdf1234");
    }


    public Database(int playday, int season) throws ClassNotFoundException, SQLException {
        this.playday =playday;
        this.season =season;
        System.out.println("Spieltag und Saison retrieved: " + season + "/" + playday);
        Class.forName("org.h2.Driver");
        conn = DriverManager.
                getConnection("jdbc:h2:~/ofm", "Chris", "asdf1234");
    }


    public void closeConn() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startServer() throws SQLException {
        server = Server.createWebServer().start();
    }

    public void stopServer() {
        server.stop();
    }

    public void addPlayerList(PlayerList list) throws SQLException {
        Statement statement = conn.createStatement();

        for (Iterator<PlayerTM> iter = list.iterator(); iter.hasNext(); ) {
            PlayerTM tmp = iter.next();
            StringBuilder tmpString = new StringBuilder();
            String command = "INSERT INTO SPIELER VALUES(" + tmp.getId() + ", '" + tmp.getPos() + "', " + (playday) + ", " + season + ", " + tmp.getAge() + ", " + tmp.getPower() + " , " + tmp.getEp() + ",  " + tmp.getTp() + ", " + tmp.getAwp() + ", " + tmp.getBid() + ", " + tmp.isHasBidder() + ", '" + tmp.getSeller() + "', '" + tmp.getBuyer() + "');";
            statement.executeUpdate(command);
            //System.out.println(command);
        }

    }


    public void createExcelSheet(String command) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(command);

        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        // sheet name needed
        Sheet sheet = wb.createSheet("SHEET NAME");
        sheet.setColumnWidth(0, 8 * 256);
        sheet.setColumnWidth(1, 5 * 256);
        sheet.setColumnWidth(2, 8 * 256);
        sheet.setColumnWidth(3, 8 * 256);
        sheet.setColumnWidth(4, 8 * 256);
        sheet.setColumnWidth(5, 8 * 256);
        sheet.setColumnWidth(6, 20 * 256);
        sheet.setColumnWidth(7, 10 * 256);
        sheet.setColumnWidth(8, 5 * 256);
        sheet.setColumnWidth(9, 5 * 256);
        sheet.setColumnWidth(10, 12 * 256);
        sheet.setColumnWidth(11, 28 * 256);
        sheet.setColumnWidth(12, 28 * 256);

        Row row = sheet.createRow((short) 0);
        row.createCell(0).setCellValue("Pos");
        row.createCell(1).setCellValue("Age");
        row.createCell(2).setCellValue("Power");
        row.createCell(3).setCellValue("Ep");
        row.createCell(4).setCellValue("Tp");
        row.createCell(5).setCellValue("Awp");
        row.createCell(6).setCellValue("Bid");
        row.createCell(7).setCellValue("Hasbidder");
        row.createCell(8).setCellValue("Day");
        row.createCell(9).setCellValue("Season");
        row.createCell(10).setCellValue("ID");
        row.createCell(11).setCellValue("Buyer");
        row.createCell(12).setCellValue("Seller");
        CellStyle styleMoney = wb.createCellStyle();
        CellStyle styleNumber = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        styleMoney.setDataFormat(format.getFormat("_-* #,## \\€_-;-* #,## \\€_-;_-* \"-\"?? \\€_-;_-@_-"));
        styleNumber.setDataFormat(format.getFormat("#,##0"));

        int i = 2;
        while (rs.next()) {
            row = sheet.createRow(i++);


            Cell cell = row.createCell
                    (0);
            cell.setCellValue(rs.getString(2));

            cell = row.createCell(1);
            cell.setCellValue(rs.getInt(5));

            cell = row.createCell(2);
            cell.setCellValue(rs.getInt(6));

            cell = row.createCell(3);
            cell.setCellValue(rs.getInt(7));
            cell.setCellStyle(styleNumber);

            cell = row.createCell(4);
            cell.setCellValue(rs.getInt(8));
            cell.setCellStyle(styleNumber);

            cell = row.createCell(5);
            cell.setCellValue(rs.getInt(9));
            cell.setCellStyle(styleNumber);

            cell = row.createCell(6);
            cell.setCellValue(rs.getInt(10));
            cell.setCellStyle(styleMoney);

            cell = row.createCell(7);
            cell.setCellValue(rs.getBoolean(11));

            cell = row.createCell(8);
            cell.setCellValue(rs.getInt(3));

            cell = row.createCell(9);
            cell.setCellValue(rs.getInt(4));

            cell = row.createCell(10);
            cell.setCellValue(rs.getInt(1));

            cell = row.createCell(11);
            cell.setCellValue(rs.getString(12));

            cell = row.createCell(12);
            cell.setCellValue(rs.getString(13));

        }

//select * from spieler order by pos asc, age asc, power asc, day asc, bid desc,  hasbidder desc
        for (int j = 0; j < 11; j++) {
            sheet.autoSizeColumn(j, false);
        }

        try {
            FileOutputStream fileOut = new FileOutputStream("workbook.xlsx");
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
