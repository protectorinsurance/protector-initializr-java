//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.system.test.config

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer

@Configuration
class KafkaConfig {

    @Autowired
    @Qualifier("schemaRegistryContainer")
    private GenericContainer schemaRegistryContainer

    @Autowired
    @Qualifier("kafkaContainer")
    private KafkaContainer KafkaContainer

    /***
     * TODO: This is where you put kafka consumer factories
     * An example can be found here:
     * https://github.com/protectorinsurance/protector-initializr-java/blob/main/web-test/src/test/groovy/no/protector/initializr/system/test/config/KafkaConfig.groovy
     */

    //INITIALIZR:INITIALIZR-DEMO
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
    //INITIALIZR:INITIALIZR-DEMO

    @Bean
    Map<String, Object> consumerConfigs() {
        String schemaRegistryUrl = "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
        Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaContainer.getBootstrapServers(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.GROUP_ID_CONFIG, "initializr",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class,
                "key.converter.schema.registry.url", schemaRegistryUrl,
                "value.converter.schema.registry.url", schemaRegistryUrl,
                "schema.registry.url", schemaRegistryUrl
        )
    }
}
//INITIALIZR:KAFKA-PRODUCER
