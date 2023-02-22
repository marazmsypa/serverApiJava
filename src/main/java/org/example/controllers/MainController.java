package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.data.model.Employees;
import org.example.data.repositories.EmployeesRepository;
import org.example.database.Database;
import org.example.server.annotations.Controller;
import org.example.server.annotations.Get;
import org.example.server.annotations.Post;

public class MainController implements Controller {
    private final EmployeesRepository repository;

    public MainController(Database database) {
        this.repository = new EmployeesRepository(database);
    }

    @Get(url = "/test")
    public Employees get(HttpExchange exchange) {
        Employees employees = repository.findById(1);
        System.out.println(employees);

        return employees;
    }

    @Post(url = "/search")
    public String post(HttpExchange exchange) {
        return "This is the response";
    }
}
