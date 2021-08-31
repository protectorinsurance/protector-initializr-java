package no.protector.initializr.system.test.config


import no.protector.initializr.system.test.provider.FlywayProvider
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.mockserver.client.MockServerClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.testcontainers.containers.*
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
    private static MockServerContainer mockServer
    //INITIALIZR:DATABASE
    private static MSSQLServerContainer mssqlServerContainer
    //INITIALIZR:DATABASE
    //INITIALIZR:KAFKA
    private static KafkaContainer kafkaContainer
    //INITIALIZR:KAFKA

    @Bean
    GenericContainer protectorInitializrContainer() { protectorInitializrContainer }

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
        //INITIALIZR:KAFKA
        kafkaContainer = createKafkaContainer(network)
        //INITIALIZR:KAFKA
        mockServer = createMockServer(network)
        startContainers()
    }

    private static startContainers() {
        mockServer.start()

        //INITIALIZR:KAFKA
        kafkaContainer.start()
        //INITIALIZR:KAFKA

        //INITIALIZR:DATABASE
        mssqlServerContainer.start()
        def flyway = FlywayProvider.build(mssqlServerContainer)
        flyway.clean()
        flyway.migrate()
        //INITIALIZR:DATABASE

        protectorInitializrContainer.start()
    }

    //INITIALIZR:KAFKA
    private static KafkaContainer createKafkaContainer(Network network) {
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
                .withNetwork(network)
                .withNetworkAliases("kafka")
    }
    //INITIALIZR:KAFKA

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
                .withExposedPorts(8080, 8391)
                .withNetwork(network)
                .withNetworkAliases("protector-initializr")
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
                .withDockerfile(Path.of("../Web.SystemTest.Dockerfile")))
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>()
        factory.setConsumerFactory(kafkaConsumer())
        factory
    }


    @Bean
    ConsumerFactory<Integer, String> kafkaConsumer() {
        new DefaultKafkaConsumerFactory<Integer, String>(consumerConfigs())
    }

    @Bean
    Map<String, Object> consumerConfigs() {
        Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.GROUP_ID_CONFIG, "initializr",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        )
    }
}
