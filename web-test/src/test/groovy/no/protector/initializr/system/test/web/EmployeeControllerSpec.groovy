package no.protector.initializr.system.test.web

import no.protector.initializr.system.test.AbstractSystemSpec
import no.protector.initializr.system.test.RequestService
import no.protector.initializr.domain.model.Employee
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod

class EmployeeControllerSpec extends AbstractSystemSpec {

    @Autowired
    URL initializrBaseUrl

    @Autowired
    RequestService requestService

    URI employeeUri

    def setup() {
        employeeUri = new URI("$initializrBaseUrl/employee")
    }

    def "test"() {
        when:
        def employeeResponse = requestService.exchange(new URI("$employeeUri/1"), HttpMethod.GET, Employee)
        then:
        employeeResponse.body.employeeId == 1
    }
}
