//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.domain.configuration;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    private static final int BATCH_SIZE = 16384;
    private static final int BUFFER_MEMORY = 33_554_432;

    private final KafkaProperties kafkaProperties;

    public KafkaConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(kafkaProperties.buildAdminProperties());
    }

    private Map<String, Object> getDefaultProducerConfig() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.putAll(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.RETRIES_CONFIG, 0,
                ProducerConfig.BATCH_SIZE_CONFIG, BATCH_SIZE,
                ProducerConfig.LINGER_MS_CONFIG, 1,
                ProducerConfig.BUFFER_MEMORY_CONFIG, BUFFER_MEMORY,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class
        ));
        return props;
    }

    /**
     * TODO: Implement producer templates and factories
     * Examples can be found here:
     * https://github.com/protectorinsurance/protector-initializr-java/blob/main/domain/src/main/java/no/protector/initializr/domain/configuration/KafkaConfiguration.java
     * <p>
     * An examples of an implemented producer can be found here:
     * https://github.com/protectorinsurance/protector-initializr-java/tree/main/domain/src/main/java/no/protector/initializr/domain/producer/EmployeeKafkaProducer.java
     */

    //INITIALIZR:INITIALIZR-DEMO
    @Bean
    public ProducerFactory<Integer, String> employeeProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getDefaultProducerConfig());
    }

    @Bean
    public KafkaTemplate<Integer, String> employeeKafkaTemplate(
            ProducerFactory<Integer, String> employeeProducerFactory) {
        return new KafkaTemplate<>(employeeProducerFactory);
    }
    //INITIALIZR:INITIALIZR-DEMO
}
//INITIALIZR:KAFKA-PRODUCER
