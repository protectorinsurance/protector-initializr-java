//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.system.test.config


import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer

@Configuration
class KafkaConfig {

    @Autowired
    @Qualifier("schemaRegistryContainer")
    private GenericContainer schemaRegistryContainer

    @Autowired
    @Qualifier("kafkaContainer")
    private KafkaContainer kafkaContainer

    /***
     * TODO: This is where you put kafka consumer factories
     * An example can be found here:
     * https://github.com/protectorinsurance/protector-initializr-java/blob/main/web-test/src/test/groovy/no/protector/initializr/system/test/config/KafkaConfig.groovy
     */

    //INITIALIZR:INITIALIZR-DEMO
    @Bean
    ProducerFactory<Integer, String> kafkaConsumer() {
        new DefaultKafkaProducerFactory<Integer, String>(producerConfigs())
    }

    @Bean
    KafkaTemplate<Integer, String> employeeKafkaTemplate(
            ProducerFactory<Integer, String> employeeProducerFactory) {
        return new KafkaTemplate<>(employeeProducerFactory)
    }
    //INITIALIZR:INITIALIZR-DEMO

    @Bean
    Map<String, Object> producerConfigs() {
        String schemaRegistryUrl = "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
        Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                ProducerConfig.CLIENT_ID_CONFIG, "kafka-system-tests",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                "schema.registry.url", schemaRegistryUrl)
    }
}
//INITIALIZR:KAFKA-PRODUCER
