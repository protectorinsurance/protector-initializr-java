package no.protector.initializr.web

import no.protector.initializr.AbstractSystemSpec
import org.springframework.beans.factory.annotation.Autowired
import org.testcontainers.containers.GenericContainer

class EmployeeControllerSpec extends AbstractSystemSpec {

    @Autowired
    GenericContainer protectorInitializrContainer

    def "test"() {
        expect:
        protectorInitializrContainer != null
    }
}
