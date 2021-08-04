package no.protector.initializr.domain.service

import groovy.json.JsonOutput
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import no.protector.initializr.domain.model.Employee
import spock.lang.Specification

class EmployeeServiceImpSpec extends Specification {

    def service = new EmployeeServiceImpl()

    @Builder(builderStrategy = ExternalStrategy, forClass = Employee)
    class EmployeeBuilder {}

    def "When calling with id 1 hardcoded customer will be returned"() {
        given:
        def employee = JsonOutput.toJson(new EmployeeBuilder()
                .employeeId(1)
                .firstName("Colter")
                .lastName("Wall")
                .build())
        when:
        def result = JsonOutput.toJson(service.getEmployee(1))
        then:
        result == employee
    }

    def "Calling with id not 1 will result in exception"(int id) {
        when:
        service.getEmployee(id)
        then:
        thrown IllegalArgumentException
        where:
        id  | _
        0   | _
        -1  | _
        3   | _
        100 | _
    }
}
