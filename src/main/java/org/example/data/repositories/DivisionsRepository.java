package org.example.data.repositories;

import org.example.data.model.Divisions;
import org.example.data.repositories.internal.JdbcRepository;
import org.example.database.Database;

public class DivisionsRepository extends JdbcRepository<Divisions, Integer> {
    public DivisionsRepository(Database database) {
        super(database);
    }

    @Override
    public Class<Divisions> getModelClass() {
        return Divisions.class;
    }
}
