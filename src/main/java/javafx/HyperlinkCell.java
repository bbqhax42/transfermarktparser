package javafx;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import transfermarkt.PlayerTM;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HyperlinkCell implements Callback<TableColumn<PlayerTM, Hyperlink>, TableCell<PlayerTM, Hyperlink>> {


    /*
    Method which is called when a HyperlinkCell is being created
     */
    @Override
    public TableCell<PlayerTM, Hyperlink> call(TableColumn<PlayerTM, Hyperlink> arg) {
        TableCell<PlayerTM, Hyperlink> cell = new TableCell<PlayerTM, Hyperlink>() {
            @Override
            protected void updateItem(Hyperlink item, boolean empty) {
                setGraphic(item);

                /*
                Makes Hyperlinks openable in your webbrowser
                 */
                if(item!=null){
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        try {
                            Desktop.getDesktop().browse(new URI(item.getText()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                });}

            }
        };
        return cell;
    }
}