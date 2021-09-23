//INITIALIZR:INITIALIZR-DEMO
//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.domain.producer;

import no.protector.initializr.domain.model.Employee;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Service
public class EmployeeKafkaProducer {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeKafkaProducer.class);
    private final KafkaProducer<Integer, String> producer;

    public EmployeeKafkaProducer(Properties producerProperties) {
        this.producer = new KafkaProducer<>(producerProperties);
    }

    public void employeeRead(Employee employee) {
        try {
            ProducerRecord<Integer, String> record =
                    new ProducerRecord<>("employee-read", employee.getId(), employee.getLastName());
            producer.send(record).get();

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
