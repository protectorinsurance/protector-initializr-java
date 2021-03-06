//INITIALIZR:INITIALIZR-DEMO
package no.protector.initializr.domain.service

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import no.protector.initializr.domain.model.Employee
import no.protector.initializr.domain.producer.EmployeeKafkaProducer
import no.protector.initializr.domain.repository.EmployeeRepository
import spock.lang.Specification

class EmployeeServiceImplSpec extends Specification {

    def repository = Mock(EmployeeRepository)
    def producer = Mock(EmployeeKafkaProducer)
    def service = new EmployeeServiceImpl(repository, producer)

    @Builder(builderStrategy = ExternalStrategy, forClass = Employee)
    class EmployeeBuilder {}

    def "When calling with id 1 hardcoded customer will be returned"() {
        given:
        def employee = new EmployeeBuilder()
                .id(1)
                .firstName("Colter")
                .lastName("Wall")
                .build()
        repository.findById(1) >> Optional.of(employee)
        expect:
        service.getEmployee(1) == employee
    }
}
//INITIALIZR:INITIALIZR-DEMO