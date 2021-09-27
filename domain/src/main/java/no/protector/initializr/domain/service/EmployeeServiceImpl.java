//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service;

import no.protector.initializr.domain.model.Employee;
import no.protector.initializr.domain.producer.EmployeeKafkaProducer;
import no.protector.initializr.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public record EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeKafkaProducer employeeKafkaProducer)
        implements EmployeeService {

    @Override
    public Employee getEmployee(int employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        employeeKafkaProducer.employeeRead(employee);
        return employee;
    }
}
//INITIALIZR:INITIALIZR-DEMO
