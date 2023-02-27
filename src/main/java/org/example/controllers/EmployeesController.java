package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.data.model.Employees;
import org.example.data.repositories.EmployeesRepository;
import org.example.database.Database;
import org.example.database.QueryBuilder;
import org.example.server.annotations.Controller;
import org.example.server.annotations.Get;
import org.example.server.annotations.Post;
import org.example.server.model.RequestData;

import java.util.List;

public class EmployeesController implements Controller {
    private final EmployeesRepository repository;
    public EmployeesController(Database database) {
        this.repository = new EmployeesRepository(database);
    }

    // baseUrl + routeUrl
    // baseUrl -> routeUrl
    @Post(url = "/employees/save")
    public Employees save() {
        Employees employees = new Employees();
        employees.setName("Test employee");
        employees.setSurname("Surname");
        employees.setPatronymic("Utoma");
        employees.setCode(324234);

        return repository.save(employees);
    }

    @Get(url = "/employees")
    public List<Employees> getAll(RequestData requestData) {
        return repository.findAll(new QueryBuilder(QueryBuilder.Querytype.SELECT));
    }

    // /employees/{id} != /employees/5

    @Get(url = "/employees/{id}")
    public Employees getOneById(RequestData requestData, HttpExchange exchange) {
        int id = Integer.parseInt(requestData.getPathParams().get("id"));
        return repository.findById(id);
    }

    @Post(url = "/search")
    public String post(HttpExchange exchange) {

        return "This is the response";
    }
}
