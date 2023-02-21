package org.example.data.repositories;

import org.example.data.model.Employees;
import org.example.database.Database;

public class EmployeesRepository extends JdbcRepository<Employees>{
    public EmployeesRepository(Database database) {
        super(database);
    }
}
