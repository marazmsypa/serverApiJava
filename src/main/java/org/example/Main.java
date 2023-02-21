package org.example;

import org.example.data.model.Employees;
import org.example.data.repositories.EmployeesRepository;
import org.example.database.Database;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Database database = Database.getInstance();

            EmployeesRepository repository = new EmployeesRepository(database);

            List<Employees> employees = repository.list(Employees.class, "employees");

            for (Employees e : employees) {
                System.out.println(e);
            }

            /*List<Employees> employees = fetchEmployees(database.getConnection());
            for (Employees e : employees) {
                System.out.println(e);
            }*/

            /*TestServer server = new TestServer(8080);
            server.registerController("/", new MainController(database));
            server.start();*/
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}