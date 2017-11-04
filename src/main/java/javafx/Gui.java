package javafx;

import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import transfermarkt.PlayerTM;
import transfermarkt.Transfermarkt;

import java.io.*;
import java.util.Map;
import java.util.function.Predicate;

public class Gui extends Application {


    private final ObservableList<PlayerTM> data =
            FXCollections.observableArrayList();
    FilteredList<PlayerTM> filteredData = new FilteredList<PlayerTM>(data, e -> true);
    SortedList<PlayerTM> sortedData = new SortedList<PlayerTM>(filteredData);
    BorderPane bp;

    private HBox hbTop = new HBox();
    private TextField hbTopUserNameTextField;
    private Label hbTopUserNameLabel;
    private PasswordField hbTopPasswordField;
    private Button hbTopLoginButton, hbTopLogoutButton;


    private VBox vboxLeft;
    private TextField vboxLeftAgeMin, vboxLeftAgeMax, vboxLeftPowerMin, vboxLeftPowerMax, profile1_ep, profile1_tp, profile1_ep_days, profile1_tp_days, profile1_ep_tu, profile1_tp_tl,
            profile2_ep, profile2_tp, profile2_ep_days, profile2_tp_days, profile2_ep_tu, profile2_tp_tl, profile3_ep, profile3_tp, profile3_ep_days, profile3_tp_days, profile3_ep_tu, profile3_tp_tl;
    private CheckBox vboxLeftPosAll, vboxLeftPosTW, vboxLeftPosLIB, vboxLeftPosLV, vboxLeftPosLMD, vboxLeftPosRMD, vboxLeftPosRV, vboxLeftPosVS, vboxLeftPosLM,
            vboxLeftPosDM, vboxLeftPosZM, vboxLeftPosRM, vboxLeftPosLS, vboxLeftPosMS, vboxLeftPosRS;
    private Button vboxLeftFilterButton, vboxLeftParseButton;


    private Group grp;
    private VBox vboxCenter;
    final Label groupCenterTopLabel = new Label("Transfer Data");
    private TableView<PlayerTM> table;


    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
    private Map<String, String> loginCookies;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Transfermarket Parser");
        bp = new BorderPane();
        bp.setPadding(new Insets(10, 20, 10, 20));
        Scene scene = new Scene(bp);
        bp.setTop(hbTop);
        initHBoxTop();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight() - 50);

        stage.setScene(scene);
        stage.show();
    }


    private void initHBoxTop() {


        initLoginObjects();

        hbTopLogoutButton = new Button("Logout");

        hbTop.getChildren().addAll(hbTopUserNameTextField, hbTopPasswordField, hbTopLoginButton);
        hbTop.setSpacing(2);
        hbTop.setPadding(new Insets(2, 2, 2, 2));
        hbTop.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));


        hbTopLogoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hbTop.getChildren().removeAll(hbTopUserNameLabel, hbTopLogoutButton);
                initLoginObjects();
                hbTop.getChildren().addAll(hbTopUserNameTextField, hbTopPasswordField, hbTopLoginButton);

                loginCookies = null;
                bp.setCenter(new Label());
                bp.setLeft(new Label());
            }
        });


    }

    private void initLoginObjects() {
        hbTopUserNameTextField = new TextField();
        hbTopUserNameTextField.setMaxWidth(120);
        hbTopUserNameTextField.setPromptText("User");
        hbTopUserNameTextField.setText("brotkatenils");
        final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load

        //removes default focus from hbTopUserNameTextField
        hbTopUserNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && firstTime.get()) {
                hbTop.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        });

        hbTopPasswordField = new PasswordField();
        hbTopPasswordField.setMaxWidth(120);
        hbTopPasswordField.setPromptText("Password");
        hbTopPasswordField.setText("dertollenils");


        hbTopLoginButton = new Button("Login");

        hbTopLoginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                onLogin();
            }
        });
    }

    private void onLogin() {
        System.out.println("User: " + hbTopUserNameTextField.getText() + " Passwort: " + hbTopPasswordField.getText());
        try {
            getLoginCookies(hbTopUserNameTextField.getText(), hbTopPasswordField.getText());

            initVBoxLeft();
            initGroupCenter();
            hbTopUserNameLabel = new Label(hbTopUserNameTextField.getText());
            hbTopUserNameLabel.setFont(new Font("Arial", 20));
            hbTopUserNameLabel.setMaxWidth(120);

            hbTop.getChildren().removeAll(hbTopUserNameTextField, hbTopPasswordField, hbTopLoginButton);
            hbTop.getChildren().addAll(hbTopUserNameLabel, hbTopLogoutButton);
            hbTopUserNameTextField = null;
            hbTopPasswordField = null;
            hbTopLoginButton = null;
        } catch (IOException e1) {
            e1.getMessage();
        }
    }

    private void getLoginCookies(String userID, String userPass) throws IOException {
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

        if (loginResponse.cookies().toString().contains("sde=1") && loginResponse.cookies().toString().contains("srv1=1")) {
            loginCookies = loginResponse.cookies();
            System.out.println("Login response message: " + loginResponse.statusMessage());
            System.out.println("Login response code: " + loginResponse.statusCode());
            System.out.println("Login response: " + loginResponse.cookies());
        } else {
            loginCookies = null;
            throw new IOException("Wrong login data");
        }
    }

    private void initVBoxLeft() {
        vboxLeft = new VBox();
        bp.setLeft(vboxLeft);
        Label vboxLeftTitle = new Label("Filtersettings");
        vboxLeftTitle.setFont(new Font("Arial", 20));


        FlowPane flowPanePosition = new FlowPane();
        initFlowPanePosition(flowPanePosition);


        vboxLeftAgeMin = new TextField("17");
        vboxLeftAgeMin.setPromptText("Min Age");
        vboxLeftAgeMin.setMinWidth(65);
        vboxLeftAgeMax = new TextField("36");
        vboxLeftAgeMax.setPromptText("Max Age");
        vboxLeftAgeMax.setMinWidth(65);

        HBox hBoxAge = new HBox();

        hBoxAge.getChildren().add(vboxLeftAgeMin);
        hBoxAge.getChildren().add(vboxLeftAgeMax);

        vboxLeftPowerMin = new TextField("1");
        vboxLeftPowerMin.setPromptText("Pow Min");
        vboxLeftPowerMin.setMinWidth(65);
        vboxLeftPowerMax = new TextField("27");
        vboxLeftPowerMax.setPromptText("Pow Max");
        vboxLeftPowerMax.setMinWidth(65);

        HBox hBoxPower = new HBox();
        hBoxPower.getChildren().add(vboxLeftPowerMin);
        hBoxPower.getChildren().add(vboxLeftPowerMax);

        VBox vBoxTextFields = new VBox();
        vBoxTextFields.setMaxSize(111, 111);
        vBoxTextFields.getChildren().add(hBoxAge);
        vBoxTextFields.getChildren().add(hBoxPower);
        vBoxTextFields.setBackground(new Background(new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)));

        vboxLeftFilterButton = new Button("Filter!");


        vboxLeftFilterButton.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                verifyTextFields();


                filteredData.setPredicate((Predicate<? super PlayerTM>) playerTM -> {

                    if (playerTM.getAge() >= Integer.parseInt(vboxLeftAgeMin.getText()) && playerTM.getAge() <= Integer.parseInt(vboxLeftAgeMax.getText())) {
                        if (playerTM.getPower() >= Integer.parseInt(vboxLeftPowerMin.getText()) && playerTM.getPower() <= Integer.parseInt(vboxLeftPowerMax.getText())) {
                            if (vboxLeftPosTW.isSelected() && playerTM.getPos().equals("TW")) {
                                return true;
                            } else if (vboxLeftPosLIB.isSelected() && playerTM.getPos().equals("LIB")) {
                                return true;
                            } else if (vboxLeftPosLV.isSelected() && playerTM.getPos().equals("LV")) {
                                return true;
                            } else if (vboxLeftPosLMD.isSelected() && playerTM.getPos().equals("LMD")) {
                                return true;
                            } else if (vboxLeftPosRMD.isSelected() && playerTM.getPos().equals("RMD")) {
                                return true;
                            } else if (vboxLeftPosRV.isSelected() && playerTM.getPos().equals("RV")) {
                                return true;
                            } else if (vboxLeftPosVS.isSelected() && playerTM.getPos().equals("VS")) {
                                return true;
                            } else if (vboxLeftPosLM.isSelected() && playerTM.getPos().equals("LM")) {
                                return true;
                            } else if (vboxLeftPosDM.isSelected() && playerTM.getPos().equals("DM")) {
                                return true;
                            } else if (vboxLeftPosZM.isSelected() && playerTM.getPos().equals("ZM")) {
                                return true;
                            } else if (vboxLeftPosRM.isSelected() && playerTM.getPos().equals("RM")) {
                                return true;
                            } else if (vboxLeftPosLS.isSelected() && playerTM.getPos().equals("LS")) {
                                return true;
                            } else if (vboxLeftPosMS.isSelected() && playerTM.getPos().equals("MS")) {
                                return true;
                            } else if (vboxLeftPosRS.isSelected() && playerTM.getPos().equals("RS")) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
                table.refresh();
                }
        }));

        vboxLeftParseButton = new Button("Parse!");

        vboxLeftParseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                verifyTextFields();
                Thread t = new Thread(new Transfermarkt(loginCookies, Integer.parseInt(vboxLeftAgeMin.getText()), Integer.parseInt(vboxLeftAgeMax.getText()),
                        Integer.parseInt(vboxLeftPowerMin.getText()), Integer.parseInt(vboxLeftPowerMax.getText()), userAgent, data, vboxLeftParseButton), "My Thread");
                System.out.println("Parsethread started...");
                t.start();

            }
        });

        VBox vBoxProfile1 = new VBox();
        VBox vBoxProfile2 = new VBox();
        VBox vBoxProfile3 = new VBox();

        initProfiles(vBoxProfile1, vBoxProfile2, vBoxProfile3);

        HBox hBoxLeftButtons = new HBox();
        hBoxLeftButtons.getChildren().add(vboxLeftFilterButton);
        hBoxLeftButtons.getChildren().add(vboxLeftParseButton);
        vboxLeft.getChildren().addAll(vboxLeftTitle, flowPanePosition, vBoxTextFields, hBoxLeftButtons, vBoxProfile1, vBoxProfile2, vBoxProfile3);
        vboxLeft.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        vboxLeft.setMaxWidth(222);
        vboxLeft.setSpacing(10);
        vboxLeft.setPadding(new Insets(20, 2, 2, 2));

    }

    private void initProfiles(VBox vBoxProfile1, VBox vBoxProfile2, VBox vBoxProfile3) {
        Label labelProfile1 = new Label("Profile 1");
        HBox hBoxProfile1EP = new HBox();
        HBox hBoxProfile1TP = new HBox();

        profile1_ep = new TextField();
        profile1_ep.setMaxWidth(35);
        profile1_ep.setPromptText("EP");
        profile1_ep_days = new TextField();
        profile1_ep_days.setMaxWidth(35);
        profile1_ep_days.setPromptText("Days");
        profile1_ep_tu = new TextField();
        profile1_ep_tu.setMaxWidth(35);
        profile1_ep_tu.setPromptText("TU");
        hBoxProfile1EP.getChildren().add(profile1_ep);
        hBoxProfile1EP.getChildren().add(new Label("x"));
        hBoxProfile1EP.getChildren().add(profile1_ep_days);
        hBoxProfile1EP.getChildren().add(new Label("+"));
        hBoxProfile1EP.getChildren().add(profile1_ep_tu);

        profile1_tp = new TextField();
        profile1_tp.setMaxWidth(35);
        profile1_tp.setPromptText("TP");
        profile1_tp_days = new TextField();
        profile1_tp_days.setMaxWidth(35);
        profile1_tp_days.setPromptText("Days");
        profile1_tp_tl = new TextField();
        profile1_tp_tl.setMaxWidth(35);
        profile1_tp_tl.setPromptText("TL");
        hBoxProfile1TP.getChildren().add(profile1_tp);
        hBoxProfile1TP.getChildren().add(new Label("x"));
        hBoxProfile1TP.getChildren().add(profile1_tp_days);
        hBoxProfile1TP.getChildren().add(new Label("+"));
        hBoxProfile1TP.getChildren().add(profile1_tp_tl);

        vBoxProfile1.getChildren().add(labelProfile1);
        vBoxProfile1.getChildren().add(hBoxProfile1EP);
        vBoxProfile1.getChildren().add(hBoxProfile1TP);
        vBoxProfile1.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));


        Label labelProfile2 = new Label("Profile 2");
        HBox hBoxProfile2EP = new HBox();
        HBox hBoxProfile2TP = new HBox();

        profile2_ep = new TextField();
        profile2_ep.setMaxWidth(35);
        profile2_ep.setPromptText("EP");
        profile2_ep_days = new TextField();
        profile2_ep_days.setMaxWidth(35);
        profile2_ep_days.setPromptText("Days");
        profile2_ep_tu = new TextField();
        profile2_ep_tu.setMaxWidth(35);
        profile2_ep_tu.setPromptText("TU");
        hBoxProfile2EP.getChildren().add(profile2_ep);
        hBoxProfile2EP.getChildren().add(new Label("x"));
        hBoxProfile2EP.getChildren().add(profile2_ep_days);
        hBoxProfile2EP.getChildren().add(new Label("+"));
        hBoxProfile2EP.getChildren().add(profile2_ep_tu);

        profile2_tp = new TextField();
        profile2_tp.setMaxWidth(35);
        profile2_tp.setPromptText("TP");
        profile2_tp_days = new TextField();
        profile2_tp_days.setMaxWidth(35);
        profile2_tp_days.setPromptText("Days");
        profile2_tp_tl = new TextField();
        profile2_tp_tl.setMaxWidth(35);
        profile2_tp_tl.setPromptText("TL");
        hBoxProfile2TP.getChildren().add(profile2_tp);
        hBoxProfile2TP.getChildren().add(new Label("x"));
        hBoxProfile2TP.getChildren().add(profile2_tp_days);
        hBoxProfile2TP.getChildren().add(new Label("+"));
        hBoxProfile2TP.getChildren().add(profile2_tp_tl);

        vBoxProfile2.getChildren().add(labelProfile2);
        vBoxProfile2.getChildren().add(hBoxProfile2EP);
        vBoxProfile2.getChildren().add(hBoxProfile2TP);
        vBoxProfile2.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));


        Label labelProfile3 = new Label("Profile 3");
        HBox hBoxProfile3EP = new HBox();
        HBox hBoxProfile3TP = new HBox();


        profile3_ep = new TextField();
        profile3_ep.setMaxWidth(35);
        profile3_ep.setPromptText("EP");
        profile3_ep_days = new TextField();
        profile3_ep_days.setMaxWidth(35);
        profile3_ep_days.setPromptText("Days");
        profile3_ep_tu = new TextField();
        profile3_ep_tu.setMaxWidth(35);
        profile3_ep_tu.setPromptText("TU");
        hBoxProfile3EP.getChildren().add(profile3_ep);
        hBoxProfile3EP.getChildren().add(new Label("x"));
        hBoxProfile3EP.getChildren().add(profile3_ep_days);
        hBoxProfile3EP.getChildren().add(new Label("+"));
        hBoxProfile3EP.getChildren().add(profile3_ep_tu);

        profile3_tp = new TextField();
        profile3_tp.setMaxWidth(35);
        profile3_tp.setPromptText("TP");
        profile3_tp_days = new TextField();
        profile3_tp_days.setMaxWidth(35);
        profile3_tp_days.setPromptText("Days");
        profile3_tp_tl = new TextField();
        profile3_tp_tl.setMaxWidth(35);
        profile3_tp_tl.setPromptText("TL");
        hBoxProfile3TP.getChildren().add(profile3_tp);
        hBoxProfile3TP.getChildren().add(new Label("x"));
        hBoxProfile3TP.getChildren().add(profile3_tp_days);
        hBoxProfile3TP.getChildren().add(new Label("+"));
        hBoxProfile3TP.getChildren().add(profile3_tp_tl);

        vBoxProfile3.getChildren().add(labelProfile3);
        vBoxProfile3.getChildren().add(hBoxProfile3EP);
        vBoxProfile3.getChildren().add(hBoxProfile3TP);
        vBoxProfile3.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void verifyTextFields() {
        if (Integer.parseInt(vboxLeftAgeMin.getText()) > 36) vboxLeftAgeMin.setText("36");
        if (Integer.parseInt(vboxLeftAgeMax.getText()) > 36) vboxLeftAgeMax.setText("36");
        if (Integer.parseInt(vboxLeftAgeMin.getText()) < 17) vboxLeftAgeMin.setText("17");
        if (Integer.parseInt(vboxLeftAgeMax.getText()) < 17) vboxLeftAgeMax.setText("17");
        if (Integer.parseInt(vboxLeftPowerMin.getText()) < 1) vboxLeftPowerMin.setText("1");
        if (Integer.parseInt(vboxLeftPowerMax.getText()) < 1) vboxLeftPowerMax.setText("1");
        if (Integer.parseInt(vboxLeftAgeMin.getText()) > Integer.parseInt(vboxLeftAgeMax.getText())) {
            vboxLeftAgeMax.setText(vboxLeftAgeMin.getText());
        }
        if (Integer.parseInt(vboxLeftPowerMin.getText()) > Integer.parseInt(vboxLeftPowerMax.getText())) {
            if (Integer.parseInt(vboxLeftPowerMin.getText()) > Integer.parseInt(vboxLeftPowerMax.getText())) {
                vboxLeftPowerMax.setText(vboxLeftPowerMin.getText());
            }
        }
        if (profile1_ep.getText().isEmpty()) profile1_ep.setText("0");
        if (profile1_tp.getText().isEmpty()) profile1_tp.setText("0");
        if (profile2_ep.getText().isEmpty()) profile2_ep.setText("0");
        if (profile2_tp.getText().isEmpty()) profile2_tp.setText("0");
        if (profile3_ep.getText().isEmpty()) profile3_ep.setText("0");
        if (profile3_tp.getText().isEmpty()) profile3_tp.setText("0");
        if (profile1_ep_days.getText().isEmpty()) profile1_ep_days.setText("0");
        if (profile1_tp_days.getText().isEmpty()) profile1_tp_days.setText("0");
        if (profile2_ep_days.getText().isEmpty()) profile2_ep_days.setText("0");
        if (profile2_tp_days.getText().isEmpty()) profile2_tp_days.setText("0");
        if (profile3_ep_days.getText().isEmpty()) profile3_ep_days.setText("0");
        if (profile3_tp_days.getText().isEmpty()) profile3_tp_days.setText("0");
        if (profile1_ep_tu.getText().isEmpty()) profile1_ep_tu.setText("0");
        if (profile1_tp_tl.getText().isEmpty()) profile1_tp_tl.setText("0");
        if (profile2_ep_tu.getText().isEmpty()) profile2_ep_tu.setText("0");
        if (profile2_tp_tl.getText().isEmpty()) profile2_tp_tl.setText("0");
        if (profile3_ep_tu.getText().isEmpty()) profile3_ep_tu.setText("0");
        if (profile3_tp_tl.getText().isEmpty()) profile3_tp_tl.setText("0");
    }


    private void initGroupCenter() {
        grp = new Group();
        table = new TableView<PlayerTM>();
        vboxCenter = new VBox();
        bp.setCenter(grp);
        groupCenterTopLabel.setFont(new Font("Arial", 20));

        table.setEditable(false);
        table.setMaxSize(1700, 800);
        table.setMinSize(1700, 800);


        TableColumn posCol = new TableColumn("Pos");
        posCol.setMaxWidth(45);
        posCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, String>("pos"));

        TableColumn ageCol = new TableColumn("Age");
        ageCol.setMaxWidth(45);
        ageCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("age"));

        TableColumn powerCol = new TableColumn("Pow");
        powerCol.setMaxWidth(45);
        powerCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("power"));

        TableColumn epCol = new TableColumn("EP");
        epCol.setMaxWidth(45);
        epCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("ep"));

        TableColumn tpCol = new TableColumn("TP");
        tpCol.setMaxWidth(45);
        tpCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("tp"));

        TableColumn awpCol = new TableColumn("AWP");
        awpCol.setMaxWidth(45);
        awpCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("awp"));

        TableColumn bidCol = new TableColumn("Bid");
        bidCol.setMaxWidth(120);

        bidCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("bid"));
        bidCol.setCellFactory(tc -> new CurrencyCell<>());

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(130);
        nameCol.setMaxWidth(130);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, String>("name"));

        TableColumn urlCol = new TableColumn("Link");
        urlCol.setMinWidth(350);
        urlCol.setMaxWidth(350);
        urlCol.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Hyperlink>("hyperlink"));
        urlCol.setCellFactory(new HyperlinkCell());

        TableColumn epProfile1Col = new TableColumn("EP1");
        epProfile1Col.setMinWidth(45);
        epProfile1Col.setMaxWidth(45);
        epProfile1Col.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("ep"));
        epProfile1Col.setCellFactory(new Callback<TableColumn<PlayerTM, Integer>,
                TableCell<PlayerTM, Integer>>() {
            @Override
            public TableCell<PlayerTM, Integer> call(
                    TableColumn<PlayerTM, Integer> param) {
                return new TableCell<PlayerTM, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        if (!empty) {
                            setTextFill(Color.RED);
                            setText(item + Integer.parseInt(profile1_ep.getText()) * Integer.parseInt(profile1_ep_days.getText()) + Integer.parseInt(profile1_ep_tu.getText()) + "");
                        }
                    }
                };
            }
        });

        TableColumn tpProfile1Col = new TableColumn("TP1");
        tpProfile1Col.setMinWidth(45);
        tpProfile1Col.setMaxWidth(45);
        tpProfile1Col.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("tp"));
        tpProfile1Col.setCellFactory(new Callback<TableColumn<PlayerTM, Integer>,
                TableCell<PlayerTM, Integer>>() {
            @Override
            public TableCell<PlayerTM, Integer> call(
                    TableColumn<PlayerTM, Integer> param) {
                return new TableCell<PlayerTM, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        if (!empty) {
                            setTextFill(Color.RED);
                            setText(item + Integer.parseInt(profile1_tp.getText()) * Integer.parseInt(profile1_tp_days.getText()) + Integer.parseInt(profile1_tp_tl.getText()) + "");
                        }
                    }
                };
            }
        });

        TableColumn epProfile2Col = new TableColumn("EP2");
        epProfile2Col.setMinWidth(45);
        epProfile2Col.setMaxWidth(45);
        epProfile2Col.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("ep"));
        epProfile2Col.setCellFactory(new Callback<TableColumn<PlayerTM, Integer>,
                TableCell<PlayerTM, Integer>>() {
            @Override
            public TableCell<PlayerTM, Integer> call(
                    TableColumn<PlayerTM, Integer> param) {
                return new TableCell<PlayerTM, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        if (!empty) {
                            setTextFill(Color.RED);
                            setText(item + Integer.parseInt(profile2_ep.getText()) * Integer.parseInt(profile2_ep_days.getText()) + Integer.parseInt(profile2_ep_tu.getText()) + "");
                        }
                    }
                };
            }
        });

        TableColumn tpProfile2Col = new TableColumn("TP2");
        tpProfile2Col.setMinWidth(45);
        tpProfile2Col.setMaxWidth(45);
        tpProfile2Col.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("tp"));
        tpProfile2Col.setCellFactory(new Callback<TableColumn<PlayerTM, Integer>,
                TableCell<PlayerTM, Integer>>() {
            @Override
            public TableCell<PlayerTM, Integer> call(
                    TableColumn<PlayerTM, Integer> param) {
                return new TableCell<PlayerTM, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        if (!empty) {
                            setTextFill(Color.RED);
                            setText(item + Integer.parseInt(profile2_tp.getText()) * Integer.parseInt(profile2_tp_days.getText()) + Integer.parseInt(profile2_tp_tl.getText()) + "");
                        }
                    }
                };
            }
        });

        TableColumn epProfile3Col = new TableColumn("EP3");
        epProfile3Col.setMinWidth(45);
        epProfile3Col.setMaxWidth(45);
        epProfile3Col.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("ep"));
        epProfile3Col.setCellFactory(new Callback<TableColumn<PlayerTM, Integer>,
                TableCell<PlayerTM, Integer>>() {
            @Override
            public TableCell<PlayerTM, Integer> call(
                    TableColumn<PlayerTM, Integer> param) {
                return new TableCell<PlayerTM, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        if (!empty) {
                            setTextFill(Color.RED);
                            setText(item + Integer.parseInt(profile3_ep.getText()) * Integer.parseInt(profile3_ep_days.getText()) + Integer.parseInt(profile3_ep_tu.getText()) + "");
                        }
                    }
                };
            }
        });

        TableColumn tpProfile3Col = new TableColumn("TP3");
        tpProfile3Col.setMinWidth(45);
        tpProfile3Col.setMaxWidth(45);
        tpProfile3Col.setCellValueFactory(
                new PropertyValueFactory<PlayerTM, Integer>("tp"));
        tpProfile3Col.setCellFactory(new Callback<TableColumn<PlayerTM, Integer>,
                TableCell<PlayerTM, Integer>>() {
            @Override
            public TableCell<PlayerTM, Integer> call(
                    TableColumn<PlayerTM, Integer> param) {
                return new TableCell<PlayerTM, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        if (!empty) {
                            setTextFill(Color.RED);
                            setText(item + Integer.parseInt(profile3_tp.getText()) * Integer.parseInt(profile3_tp_days.getText()) + Integer.parseInt(profile3_tp_tl.getText()) + "");
                        }
                    }
                };
            }
        });


        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
        table.getColumns().addAll(posCol, ageCol, powerCol, epCol, tpCol, awpCol, bidCol, nameCol, urlCol, epProfile1Col, tpProfile1Col, epProfile2Col, tpProfile2Col, epProfile3Col, tpProfile3Col);

        /*
        name = new TextField();
        name.setPromptText("Name");
        name.setMaxWidth(nameCol.getPrefWidth());
        */

        /*
        final Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                data.add(new PlayerTM(null, null, 0,0,0,0,0,0, false));
            }
        });


        hb.getChildren().addAll(addButton);

        */


        vboxCenter.setSpacing(5);
        vboxCenter.setPadding(new Insets(10, 0, 0, 10));
        vboxCenter.getChildren().addAll(groupCenterTopLabel, table);

        grp.getChildren().addAll(vboxCenter);
        vboxCenter.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
    }


    private void initFlowPanePosition(FlowPane flowPanePosition) {

        FlowPane flowPosLeft = new FlowPane(Orientation.VERTICAL);
        flowPosLeft.setMaxHeight(150);
        flowPosLeft.setVgap(2);


        vboxLeftPosTW = new CheckBox();
        vboxLeftPosLIB = new CheckBox();
        vboxLeftPosLV = new CheckBox();
        vboxLeftPosLMD = new CheckBox();
        vboxLeftPosRMD = new CheckBox();
        vboxLeftPosRV = new CheckBox();
        vboxLeftPosVS = new CheckBox();


        flowPosLeft.getChildren().add(vboxLeftPosTW);
        flowPosLeft.getChildren().add(vboxLeftPosLIB);
        flowPosLeft.getChildren().add(vboxLeftPosLV);
        flowPosLeft.getChildren().add(vboxLeftPosLMD);
        flowPosLeft.getChildren().add(vboxLeftPosRMD);
        flowPosLeft.getChildren().add(vboxLeftPosRV);
        flowPosLeft.getChildren().add(vboxLeftPosVS);
        flowPosLeft.getChildren().add(new Label());


        Label labelTW = new Label("TW");
        labelTW.setFont(new Font("Arial", 15));
        Label labelLIB = new Label("LIB");
        labelLIB.setFont(new Font("Arial", 15));
        Label labelLV = new Label("LV");
        labelLV.setFont(new Font("Arial", 15));
        Label labelLMD = new Label("LMD");
        labelLMD.setFont(new Font("Arial", 15));
        Label labelRMD = new Label("RMD");
        labelRMD.setFont(new Font("Arial", 15));
        Label labelRV = new Label("RV");
        labelRV.setFont(new Font("Arial", 15));
        Label labelVS = new Label("VS");
        labelVS.setFont(new Font("Arial", 15));

        flowPosLeft.getChildren().add(labelTW);
        flowPosLeft.getChildren().add(labelLIB);
        flowPosLeft.getChildren().add(labelLV);
        flowPosLeft.getChildren().add(labelLMD);
        flowPosLeft.getChildren().add(labelRMD);
        flowPosLeft.getChildren().add(labelRV);
        flowPosLeft.getChildren().add(labelVS);
        flowPosLeft.setBackground(new Background(new BackgroundFill(Color.PURPLE, CornerRadii.EMPTY, Insets.EMPTY)));


        FlowPane flowPosRight = new FlowPane(Orientation.VERTICAL);
        flowPosRight.setMaxHeight(150);
        flowPosRight.setVgap(2);

        vboxLeftPosLM = new CheckBox();
        vboxLeftPosDM = new CheckBox();
        vboxLeftPosZM = new CheckBox();
        vboxLeftPosRM = new CheckBox();
        vboxLeftPosLS = new CheckBox();
        vboxLeftPosMS = new CheckBox();
        vboxLeftPosRS = new CheckBox();


        flowPosRight.getChildren().add(vboxLeftPosLM);
        flowPosRight.getChildren().add(vboxLeftPosDM);
        flowPosRight.getChildren().add(vboxLeftPosZM);
        flowPosRight.getChildren().add(vboxLeftPosRM);
        flowPosRight.getChildren().add(vboxLeftPosLS);
        flowPosRight.getChildren().add(vboxLeftPosMS);
        flowPosRight.getChildren().add(vboxLeftPosRS);

        Label labelLM = new Label("LM");
        labelLM.setFont(new Font("Arial", 15));
        Label labelDM = new Label("DM");
        labelDM.setFont(new Font("Arial", 15));
        Label labelZM = new Label("ZM");
        labelZM.setFont(new Font("Arial", 15));
        Label labelRM = new Label("RM");
        labelRM.setFont(new Font("Arial", 15));
        Label labelLS = new Label("LS");
        labelLS.setFont(new Font("Arial", 15));
        Label labelMS = new Label("MS");
        labelMS.setFont(new Font("Arial", 15));
        Label labelRS = new Label("RS");
        labelRS.setFont(new Font("Arial", 15));

        flowPosRight.getChildren().add(new Label());
        flowPosRight.getChildren().add(labelLM);
        flowPosRight.getChildren().add(labelDM);
        flowPosRight.getChildren().add(labelZM);
        flowPosRight.getChildren().add(labelRM);
        flowPosRight.getChildren().add(labelLS);
        flowPosRight.getChildren().add(labelMS);
        flowPosRight.getChildren().add(labelRS);
        fireCheckBoxes(true);
        flowPosRight.setBackground(new Background(new BackgroundFill(Color.MAGENTA, CornerRadii.EMPTY, Insets.EMPTY)));


        flowPanePosition.setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
        flowPanePosition.setOrientation(Orientation.HORIZONTAL);
        flowPanePosition.setHgap(22);
        flowPanePosition.setMaxWidth(140);
        flowPanePosition.getChildren().add(flowPosLeft);
        flowPanePosition.getChildren().add(flowPosRight);


    }

    private void fireCheckBoxes(boolean val) {
        vboxLeftPosTW.setSelected(val);
        vboxLeftPosLIB.setSelected(val);
        vboxLeftPosLV.setSelected(val);
        vboxLeftPosLMD.setSelected(val);
        vboxLeftPosRMD.setSelected(val);
        vboxLeftPosRV.setSelected(val);
        vboxLeftPosVS.setSelected(val);
        vboxLeftPosLM.setSelected(val);
        vboxLeftPosDM.setSelected(val);
        vboxLeftPosZM.setSelected(val);
        vboxLeftPosRM.setSelected(val);
        vboxLeftPosLS.setSelected(val);
        vboxLeftPosMS.setSelected(val);
        vboxLeftPosRS.setSelected(val);
    }
}
