//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.web.controller;

import no.protector.initializr.domain.model.Employee;
import no.protector.initializr.domain.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("{id}")
    public Employee getById(@PathVariable int id) {
        return employeeService.getEmployee(id);
    }
}
//INITIALIZR:INITIALIZR-DEMO
