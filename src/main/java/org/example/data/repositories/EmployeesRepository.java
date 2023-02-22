package org.example.data.repositories;

import org.example.data.model.Employees;
import org.example.database.Database;
import org.example.database.QueryBuilder;

public class EmployeesRepository extends JdbcRepository<Employees, Integer> {
    public EmployeesRepository(Database database) {
        super(database);
    }

    @Override
    public Class<Employees> getModelClass() {
        return Employees.class;
    }

    public Employees findByName(String name) {
        QueryBuilder query = new QueryBuilder();

        query.addCondition("name", "=", name);
        query.addColumn("id");

        return super.findOne(query, Employees.class);
    }
}
