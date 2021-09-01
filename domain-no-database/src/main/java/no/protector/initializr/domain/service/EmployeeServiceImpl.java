//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service;

import no.protector.initializr.domain.model.Employee;
import no.protector.initializr.domain.producer.EmployeeKafkaProducer;
import org.springframework.stereotype.Service;

@Service
public record EmployeeServiceImpl(EmployeeKafkaProducer kafkaProducer) implements EmployeeService {
    @Override
    public Employee getEmployee(int employeeId) {
        if (employeeId != 1)
            throw new IllegalArgumentException("Id must be 1");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("Colter");
        employee.setLastName("Wall");
        kafkaProducer.employeeRead(employee);
        return employee;
    }
}
//INITIALIZR:INITIALIZR-DEMO