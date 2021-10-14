//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service;

import no.protector.initializr.domain.model.Employee;

public interface EmployeeService {
    Employee getEmployee(int employeeId);
    void saveEmployee(Employee employee);
}
//INITIALIZR:INITIALIZR-DEMO
