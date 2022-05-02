package ru.gb.storage.server.jdbc;

import java.io.IOException;
import java.sql.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class DataBase {

    private static DataBase instance = null;
    private static final Logger LOG = Logger.getLogger(DataBase.class.getName());
    FileHandler fh;
    private Connection connection;
    private Statement statement;

    private DataBase() {
        try {
            fh = new FileHandler("Log.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Connecting at database");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
            LOG.info("Access connect");
        }
        return instance;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:D:\\java\\GeekBrains\\java\\network-storage-template-master\\server\\clients.db");
        connection.setAutoCommit(true);
        statement = connection.createStatement();
    }

    public void addClient(final String login, final String password) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO client (login, password) VALUES (?, ?)")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            LOG.info("Add a new client in db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void selectByLogin(String login) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM client WHERE login like ?")) {
            preparedStatement.setString(1, login);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.printf("%d - %s - %s - %s\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void selectAllClients() {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `client`")) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.printf("%d - %s - %s - %s\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByLogin(String login) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM client WHERE login like ?")) {
            preparedStatement.setString(1, login);
            preparedStatement.executeUpdate();
            LOG.info("delete client");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
