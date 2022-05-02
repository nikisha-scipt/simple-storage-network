package ru.gb.storage.client.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.gb.storage.client.App;

import java.io.IOException;
import java.net.URL;
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
    private static String login;
    private String password;
    private static final Logger LOG = Logger.getLogger(GeneralPanel.class.getName());
    FileHandler fh;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regLabel.setOnMouseClicked(e -> {
            try {
                App.getScene("/reg.fxml", "Registration", new Stage());
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

    public static String getLogin() {

        return login;
    }
}
