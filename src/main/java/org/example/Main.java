package org.example;

import org.example.controllers.MainController;
import org.example.database.Database;
import org.example.server.TestServer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            TestServer server = new TestServer(8080);
            Database database = Database.getInstance();

            server.registerController("/", new MainController(database));

            server.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}