//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service

import groovy.json.JsonOutput
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import no.protector.initializr.domain.model.Employee
import no.protector.initializr.domain.producer.EmployeeKafkaProducer
import spock.lang.Specification

class EmployeeServiceImplSpec extends Specification {

    def producer = Mock(EmployeeKafkaProducer)
    def service = new EmployeeServiceImpl(producer)

    @Builder(builderStrategy = ExternalStrategy, forClass = Employee)
    class EmployeeBuilder {}

    def "When calling with id 1 hardcoded customer will be returned"() {
        given:
        def employee = JsonOutput.toJson(new EmployeeBuilder()
                .id(1)
                .firstName("Colter")
                .lastName("Wall")
                .build())
        when:
        def result = JsonOutput.toJson(service.getEmployee(1))
        then:
        result == employee
    }
}
//INITIALIZR:INITIALIZR-DEMO