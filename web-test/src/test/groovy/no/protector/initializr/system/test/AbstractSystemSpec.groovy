package no.protector.initializr.system.test

import no.protector.initializr.system.test.config.ContainerConfig
import no.protector.initializr.system.test.config.EndpointConfig
import no.protector.initializr.system.test.extensions.SystemTestContainerLogging
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

@Testcontainers
@SpringBootTest
@SystemTestContainerLogging
@ContextConfiguration(classes = [ContainerConfig, EndpointConfig])
abstract class AbstractSystemSpec extends Specification {
}
