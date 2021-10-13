package no.protector.initializr.system.test.kafka

import no.protector.initializr.system.test.AbstractSystemSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate

class EmployeeConsumerSpec extends AbstractSystemSpec {

    @Autowired
    KafkaTemplate employeeKafkaTemplate

    def "This is my madlad test"() {
        given:
        employeeKafkaTemplate.send("read-employee-consumer", 1)
        expect:
        true
    }
}
