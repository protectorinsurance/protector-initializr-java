//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.kafka.consumer;

import no.protector.initializr.domain.model.Employee;
import no.protector.initializr.domain.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeConsumer.class);

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
        LOG.info("Got the gosh darn message");
        String[] employeeParts = employeeCSV.split(",");
        Employee employee = new Employee();
        employee.setFirstName(employeeParts[0]);
        employee.setLastName(employeeParts[1]);
        LOG.info("Created the gosh darn object");
        employeeService.saveEmployee(employee);
        LOG.info("Saved the badboy dab dab");
        ack.acknowledge();
        LOG.info("awknoledged that B");
    }
}
//INITIALIZR:INITIALIZR-DEMO

