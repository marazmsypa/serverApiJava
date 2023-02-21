package org.example.data.repositories;

import org.example.database.Database;

public class DivisionsRepository extends JdbcRepository{
    public DivisionsRepository(Database database) {
        super(database);
    }
}
