package no.protector.initializr.system.test.config

import no.protector.initializr.system.test.provider.FlywayProvider
import org.mockserver.client.MockServerClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.*
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

import java.nio.file.Path

@Configuration
@ComponentScan(value = "no.protector.initializr.system.test")
class ContainerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerConfig.class)

    private static Network network
    private static GenericContainer protectorInitializrContainer
    private static MockServerContainer mockServer
    //INITIALIZR:DATABASE
    private static MSSQLServerContainer mssqlServerContainer
    //INITIALIZR:DATABASE
    private static KafkaContainer kafkaContainer
    private static GenericContainer schemaRegistryContainer

    @Bean(name = "protectorInitializrContainer")
    GenericContainer protectorKafkaInitializrContainer() { protectorInitializrContainer }

    @Bean("schemaRegistryContainer")
    GenericContainer schemaRegistryContainer() { schemaRegistryContainer }

    @Bean("kafkaContainer")
    KafkaContainer kafkaContainer() { kafkaContainer }

    @Bean
    MockServerClient mockServerClient() {
        new MockServerClient(mockServer.host, mockServer.serverPort)
    }

    //INITIALIZR:DATABASE
    @Bean
    MSSQLServerContainer mssqlServerContainer() { mssqlServerContainer }
    //INITIALIZR:DATABASE

    static {
        network = Network.newNetwork()
        protectorInitializrContainer = createProtectorInitializrContainer(network)
        //INITIALIZR:DATABASE
        mssqlServerContainer = createMSSQLServerContainer(network)
        //INITIALIZR:DATABASE
        kafkaContainer = createKafkaContainer(network)
        schemaRegistryContainer = createSchemaRegistryContainer(network)
        mockServer = createMockServer(network)
        startContainers()
    }

    private static startContainers() {
        mockServer.start()
        kafkaContainer.start()
        schemaRegistryContainer.start()
        //INITIALIZR:DATABASE
        mssqlServerContainer.start()
        def flyway = FlywayProvider.build(mssqlServerContainer)
        flyway.clean()
        flyway.migrate()
        //INITIALIZR:DATABASE
        protectorInitializrContainer.start()
        protectorInitializrContainer.followOutput(new Slf4jLogConsumer(LOG))
    }

    private static KafkaContainer createKafkaContainer(Network network) {
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
                .withNetwork(network)
                .withNetworkAliases("kafka")
    }

    private static GenericContainer createSchemaRegistryContainer(Network network) {
        new GenericContainer(DockerImageName.parse("confluentinc/cp-schema-registry:latest"))
                .withNetwork(network)
                .withNetworkAliases("schema-registry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "kafka:9092")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "localhost")
                .withEnv("SCHEMA_REGISTRY_DEBUG", "true")
                .waitingFor(Wait.forHttp("/subjects").forPort(8081).forStatusCode(200))
                .withExposedPorts(8081)
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

    private static MockServerContainer createMockServer(Network network) {
        new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver")
                .withTag("mockserver-5.11.2"))
                .withNetwork(network)
                .withNetworkAliases("mockserver")
    }

    private static GenericContainer createProtectorInitializrContainer(Network network) {
        createBaseProtectorInitializrContainer()
                .withExposedPorts(8391)
                .withNetwork(network)
                .withNetworkAliases("protector-initializr-kafka")
                .withEnv("spring_profiles_active", "system-test")
                .waitingFor(Wait.forHttp("/actuator/health").forPort(8391).forStatusCode(200))
        //INITIALIZR:DATABASE
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("db_write.properties"),
                        "/var/run/secrets/db_write")
        //INITIALIZR:DATABASE
    }

    private static GenericContainer createBaseProtectorInitializrContainer() {
        new GenericContainer(new ImageFromDockerfile()
                .withDockerfile(Path.of("../Kafka.Dockerfile")))
    }
}
