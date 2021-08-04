package no.protector.initializr.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile

import java.nio.file.Path

@Configuration
class ContainerConfig {

    private static Network network

    private static GenericContainer protectorInitializrContainer

    @Bean
    GenericContainer protectorInitializrContainer() { protectorInitializrContainer }

    static {
        network = Network.newNetwork()
        protectorInitializrContainer = createProtectorInitializrContainer(network)
        startContainers()
    }

    private static startContainers() {
        protectorInitializrContainer.start()
    }

    private static GenericContainer createProtectorInitializrContainer(Network network) {
        createBaseProtectorInitializrContainer()
                .withExposedPorts(8080, 8391)
                .withNetwork(network)
                .withNetworkAliases("protector-initializr")
                .waitingFor(Wait.forHttp("/actuator/health").forPort(8391).forStatusCode(200))
    }

    private static GenericContainer createBaseProtectorInitializrContainer() {
        new GenericContainer(new ImageFromDockerfile()
                .withDockerfile(Path.of("../Web.SystemTest.Dockerfile")))
    }

    static GenericContainer getProtectorInitializerContainer() {
        protectorInitializrContainer
    }
}
