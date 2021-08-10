package no.protector.initializr.system.test.config

import no.protector.initializr.system.test.provider.FlywayProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MSSQLServerContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

import java.nio.file.Path

@Configuration
@ComponentScan(value = "no.protector.initializr.system.test")
class ContainerConfig {

    private static Network network
    private static GenericContainer protectorInitializrContainer
    //INITIALIZR:DATABASE
    private static MSSQLServerContainer mssqlServerContainer
    //INITIALIZR:DATABASE

    @Bean
    GenericContainer protectorInitializrContainer() { protectorInitializrContainer }
    //INITIALIZR:DATABASE
    @Bean
    MSSQLServerContainer mssqlServerContainer() { mssqlServerContainer }
    //INITIALIZR:DATABASE

    static {
        network = Network.newNetwork()
        protectorInitializrContainer = createProtectorInitializrContainer(network)
        mssqlServerContainer = createMSSQLServerContainer(network)
        startContainers()
    }

    private static startContainers() {
        //INITIALIZR:DATABASE
        mssqlServerContainer.start()
        def flyway = FlywayProvider.build(mssqlServerContainer)
        flyway.clean()
        flyway.migrate()
        //INITIALIZR:DATABASE
        protectorInitializrContainer.start()
    }

    //INITIALIZR:DATABASE
    private static MSSQLServerContainer createMSSQLServerContainer(Network network) {
        new MSSQLServerContainer(databaseImageName())
                .acceptLicense()
                .withNetworkAliases("mssql")
                .withNetwork(network)
                .withExposedPorts(1433) as MSSQLServerContainer
    }


    private static DockerImageName databaseImageName() {
        DockerImageName
                .parse("mcr.microsoft.com/mssql/server")
                .withTag("2019-latest")
    }
    //INITIALIZR:DATABASE

    private static GenericContainer createProtectorInitializrContainer(Network network) {
        createBaseProtectorInitializrContainer()
                .withExposedPorts(8080, 8391)
                .withNetwork(network)
                .withNetworkAliases("protector-initializr")
                .waitingFor(Wait.forHttp("/actuator/health").forPort(8391).forStatusCode(200))
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("db_write.properties"),
                        "/var/run/secrets/db_write")
    }

    private static GenericContainer createBaseProtectorInitializrContainer() {
        new GenericContainer(new ImageFromDockerfile()
                .withDockerfile(Path.of("../Web.SystemTest.Dockerfile")))
    }

    static GenericContainer getProtectorInitializerContainer() {
        protectorInitializrContainer
    }
}
