package no.protector.initializr.kafka.consumer;

import no.protector.initializr.domain.service.EmployeeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConsumer {
    private final EmployeeService employeeService;

    public EmployeeConsumer(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    //Pretend call that we're creating an employee
    @KafkaListener(
            topics = "read-employee-consumer",
            groupId = "initializr-employee-consumer",
            containerFactory = "employeeListenerContainerFactory",
            clientIdPrefix = "initializr")
    void createEmployee(@Payload int employeeId,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                        Acknowledgment ack) {
        employeeService.getEmployee(employeeId);
        ack.acknowledge();
    }
}
