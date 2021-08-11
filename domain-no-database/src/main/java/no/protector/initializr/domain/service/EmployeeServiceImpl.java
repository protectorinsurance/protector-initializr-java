//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service;

import no.protector.initializr.domain.model.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Override
    public Employee getEmployee(int employeeId) {
        if (employeeId != 1)
            throw new IllegalArgumentException("Id must be 1");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("Colter");
        employee.setLastName("Wall");
        return employee;
    }
}
//INITIALIZR:INITIALIZR-DEMO