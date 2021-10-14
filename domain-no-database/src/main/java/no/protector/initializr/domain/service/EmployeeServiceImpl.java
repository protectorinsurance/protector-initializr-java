//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service;

import no.protector.initializr.domain.model.Employee;
import no.protector.initializr.domain.producer.EmployeeKafkaProducer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    protected static final Map<Integer, Employee> EMPLOYEES = new HashMap<>();

    private final EmployeeKafkaProducer kafkaProducer;

    public EmployeeServiceImpl(EmployeeKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("Colter");
        employee.setLastName("Wall");
        EMPLOYEES.put(1, employee);
    }

    @Override
    public Employee getEmployee(int employeeId) {
        Employee employee = EMPLOYEES.get(employeeId);
        kafkaProducer.employeeRead(employee);
        return employee;
    }

    @Override
    public void saveEmployee(Employee employee) {
        int max = EMPLOYEES.keySet().stream().mapToInt(i -> i).max().orElse(-1);
        employee.setId(max + 1);
        EMPLOYEES.put(employee.getId(), employee);
        kafkaProducer.employeeCreated(employee);
    }
}
//INITIALIZR:INITIALIZR-DEMO
