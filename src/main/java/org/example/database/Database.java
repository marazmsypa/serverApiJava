package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database instance;

    public static Database getInstance() throws SQLException {
        if (instance == null)
            instance = new Database();

        return instance;
    }

    private final Connection connection;

    private Database() throws SQLException {
        this.connection = DriverManager.getConnection(
                "jdbc:mariadb://xsql-prdb-clone:3306/user1",
                "user1", "Wsr_user1"
        );
    }

    public Connection getConnection() {
        return connection;
    }
}
