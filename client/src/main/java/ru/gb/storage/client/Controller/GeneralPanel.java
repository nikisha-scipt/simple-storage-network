package ru.gb.storage.client.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.gb.storage.client.Client;
import ru.gb.storage.client.Init;
import ru.gb.storage.common.message.AuthMessage;
import ru.gb.storage.server.jdbc.DataBase;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GeneralPanel implements Initializable {

    @FXML
    public TextField loginInputGeneral;

    @FXML
    public PasswordField passwordInputGeneral;

    @FXML
    public Label regLabel;

    @FXML
    private Button btn;
    private static final Logger LOG = Logger.getLogger(GeneralPanel.class.getName());
    private FileHandler fh;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DataBase dataBase = DataBase.getInstance();
        try {
            dataBase.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btn.setOnMouseClicked(e -> {
            AuthMessage message = new AuthMessage();
            message.setLogin(loginInputGeneral.getText());
            message.setPassword(passwordInputGeneral.getText());
            Client client = Client.getInstance();
            try {
                client.startClient();
                client.getAuth(message);
                if (dataBase.checkLogin(loginInputGeneral.getText())) {
                    Init.getScene("/network.fxml", "network", new Stage());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        regLabel.setOnMouseClicked(e -> {
            try {
                Init.getScene("/reg.fxml", "Registration", new Stage());
                fh = new FileHandler("log.log");
                LOG.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
                LOG.info("Open reg panel");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

}
