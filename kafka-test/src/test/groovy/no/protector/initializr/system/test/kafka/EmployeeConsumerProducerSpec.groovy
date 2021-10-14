//INITIALIZR:INITIALIZR-DEMO
//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.system.test.kafka

import no.protector.initializr.system.test.AbstractSystemSpec
import no.protector.initializr.system.test.consumer.KafkaTestConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate

import java.util.concurrent.TimeUnit

/**
 * Verifies functionality when Kafka consumer and producer is selected
 */
class EmployeeConsumerProducerSpec extends AbstractSystemSpec {

    @Autowired
    KafkaTemplate employeeKafkaTemplate

    @Autowired
    private KafkaTestConsumer consumer

    def "When employee created it send out a message"() {
        given:
        cleanAndInsertDataset("EmployeeDataset.xml")
        when:
        employeeKafkaTemplate.send("create-employee-consumer", "Yolo Swaggins,Lord Of The Bling")
        consumer.latch.await(1000, TimeUnit.MILLISECONDS)
        then:
        consumer.value == "2,Yolo Swaggins,Lord Of The Bling"
    }
}
//INITIALIZR:KAFKA-PRODUCER
//INITIALIZR:INITIALIZR-DEMO
