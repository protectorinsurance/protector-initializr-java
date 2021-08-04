package no.protector.initializr.extensions

import groovy.util.logging.Slf4j
import no.protector.initializr.config.ContainerConfig
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.testcontainers.containers.output.OutputFrame

@Slf4j
class SystemTestErrorListener extends AbstractRunListener {

    private SystemTestContainerLogConsumer systemTestContainerLogConsumer

    SystemTestErrorListener() {
        systemTestContainerLogConsumer = new SystemTestContainerLogConsumer()
        ContainerConfig.getProtectorInitializerContainer()
                .followOutput(systemTestContainerLogConsumer, OutputFrame.OutputType.STDOUT)
    }

    @Override
    void beforeFeature(FeatureInfo feature) {
        systemTestContainerLogConsumer.clearLogs()
    }

    @Override
    void error(ErrorInfo error) {
        if (!systemTestContainerLogConsumer.hasLogs())
            return
        println("_____________________________________")
        println("CONTAINER LOGS:")
        println("for test '${error.method.name}'")
        println("_____________________________________")
        println(systemTestContainerLogConsumer.toString())
        println("_____________________________________")
    }
}
