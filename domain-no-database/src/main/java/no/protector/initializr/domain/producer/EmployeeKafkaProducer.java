//INITIALIZR:INITIALIZR-DEMO
//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.domain.producer;

import no.protector.initializr.domain.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class EmployeeKafkaProducer {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeKafkaProducer.class);
    private final KafkaTemplate<Integer, String> employeeKafkaTemplate;

    public EmployeeKafkaProducer(KafkaTemplate<Integer, String> employeeKafkaTemplate) {
        this.employeeKafkaTemplate = employeeKafkaTemplate;
    }

    public void employeeRead(Employee employee) {
        try {
            employeeKafkaTemplate.send("employee-read", employee.getId(), employee.getLastName()).get();

        } catch (ExecutionException e) {
            LOG.error("Could not send message", e);
        } catch (InterruptedException e) {
            LOG.error("Sending was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
//INITIALIZR:KAFKA-PRODUCER
//INITIALIZR:INITIALIZR-DEMO
