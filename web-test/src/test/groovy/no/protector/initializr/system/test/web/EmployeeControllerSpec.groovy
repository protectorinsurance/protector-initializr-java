package no.protector.initializr.system.test.web

import no.protector.initializr.domain.model.Employee
import no.protector.initializr.system.test.AbstractSystemSpec
import no.protector.initializr.system.test.RequestService
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

    def "Verify that user with ID 1 can be retrieved from service"() {
        when:
        def employeeResponse = requestService.exchange(new URI("$employeeUri/1"), HttpMethod.GET, Employee)
        then:
        employeeResponse.body.employeeId == 1
    }
}
