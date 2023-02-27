package org.example;

import org.example.controllers.EmployeesController;
import org.example.database.Database;
import org.example.database.config.ConnectionSettings;
import org.example.server.TestServer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            ConnectionSettings settings = new ConnectionSettings(
                    "jdbc:mariadb://xsql-prdb-clone:3306/user1",
                    "user1",
                    "Wsr_user1",
                    "user1");

            Database database = Database.getInstance(settings);

            TestServer server = new TestServer(8080);
            server.registerController("/employees", new EmployeesController(database));
            server.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}