package org.example.database;

import org.example.database.config.ConnectionSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database instance;

    public static Database getInstance(ConnectionSettings settings) throws SQLException {
        if (instance == null)
            instance = new Database(settings);
        return instance;
    }

    private final Connection connection;

    private Database(ConnectionSettings settings) throws SQLException {
        this.connection = DriverManager.getConnection(
                settings.url,
                settings.user,
                settings.password
        );
    }

    public Connection getConnection() {
        return connection;
    }
}
