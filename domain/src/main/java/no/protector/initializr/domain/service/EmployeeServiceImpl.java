//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service;

import no.protector.initializr.domain.model.Employee;
import no.protector.initializr.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public record EmployeeServiceImpl(EmployeeRepository employeeRepository)
        implements EmployeeService {

    @Override
    public Employee getEmployee(int employeeId) {
        return employeeRepository.findById(employeeId).orElse(null);
    }
}
//INITIALIZR:INITIALIZR-DEMO