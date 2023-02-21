package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.database.Database;
import org.example.server.controller.Controller;
import org.example.server.controller.Get;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController implements Controller {
    private final Database database;

    public MainController(Database database) {
        this.database = database;
    }

    @Get(url = "/test")
    public String get(HttpExchange exchange) {
        Connection connection = database.getConnection();
        try (Statement statement = connection.createStatement()) {
            String selectSql = "select * from user1.groups";

            StringBuilder builder = new StringBuilder();

            try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                while (resultSet.next()) {
                    builder.append(resultSet.getString("name"));
                    builder.append("\n");
                }
            }

            return builder.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Get(url = "/search")
    public String search(HttpExchange exchange) {
        System.out.println("Seaerch called");

        return "This is the response";
    }
}
