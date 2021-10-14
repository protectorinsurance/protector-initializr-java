package no.protector.initializr.kafka.consumer;

import no.protector.initializr.domain.model.Employee;
import no.protector.initializr.domain.service.EmployeeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConsumer {

    private final EmployeeService employeeService;

    public EmployeeConsumer(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @SuppressWarnings("java:S109")
    @KafkaListener(
            topics = "create-employee-consumer",
            groupId = "initializr-employee-consumer",
            containerFactory = "employeeListenerContainerFactory",
            clientIdPrefix = "initializr")
    void createEmployee(@Payload String employeeCSV, Acknowledgment ack) {
        String[] employeeParts = employeeCSV.split(",");
        Employee employee = new Employee();
        employee.setFirstName(employeeParts[0]);
        employee.setLastName(employeeParts[1]);
        employeeService.saveEmployee(employee);
        ack.acknowledge();
    }
}
