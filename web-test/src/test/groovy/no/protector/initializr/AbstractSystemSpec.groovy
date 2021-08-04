package no.protector.initializr

import no.protector.initializr.config.ContainerConfig
import no.protector.initializr.extensions.SystemTestContainerLogging
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

@Testcontainers
@SpringBootTest
@SystemTestContainerLogging
@ContextConfiguration(classes = [ContainerConfig])
abstract class AbstractSystemSpec extends Specification {
}
