//INITIALIZR:INITIALIZR-DEMO
//INITIALIZR:KAFKA
package no.protector.initializr.system.test.web

import no.protector.initializr.domain.model.Employee
import no.protector.initializr.system.test.AbstractSystemSpec
import no.protector.initializr.system.test.RequestService
import no.protector.initializr.system.test.kafka.KafkaTestConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod

import java.util.concurrent.TimeUnit

class EmployeeKafkaProducerSpec extends AbstractSystemSpec {

    @Autowired
    URL initializrBaseUrl

    @Autowired
    RequestService requestService

    @Autowired
    private KafkaTestConsumer consumer

    URI employeeUri

    def setup() {
        employeeUri = new URI("$initializrBaseUrl/employee")
    }

    def "Verify that employee read message goes out when fetching a customer"() {
        //INITIALIZR:DATABASE
        given:
        cleanAndInsertDataset("EmployeeDataset.xml")
        //INITIALIZR:DATABASE
        when:
        requestService.exchange(new URI("$employeeUri/1"), HttpMethod.GET, Employee)
        consumer.latch.await(1000, TimeUnit.MILLISECONDS)
        then:
        //.replaceAll("[^a-zA-Z0-9 ]+","")
        consumer.value == 'Wall'
    }
}
//INITIALIZR:KAFKA
//INITIALIZR:INITIALIZR-DEMO
