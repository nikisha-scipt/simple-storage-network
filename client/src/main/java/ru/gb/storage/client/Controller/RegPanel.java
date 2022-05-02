package ru.gb.storage.client.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.gb.storage.server.jdbc.DataBase;

import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegPanel implements Initializable {

    @FXML
    private Button btnReg;

    @FXML
    private TextField logIn;

    @FXML
    private PasswordField passIn;
    private DataBase dataBase;
    private String login;
    private String password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnReg.setOnAction(e -> {
            login = logIn.getText();
            password = passIn.getText();
            dataBase = DataBase.getInstance();
            try {
                dataBase.connect();
                dataBase.addClient(login, passwordOfDef(password));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }


    public static String passwordOfDef(String password) {
        MessageDigest messageDigest;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(password.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInteger = new BigInteger(1, digest);
        StringBuilder m5dHex = new StringBuilder(bigInteger.toString(16));

        while (m5dHex.length() < 32) {
            m5dHex.insert(0, "0");
        }

        return m5dHex.toString();
    }

}