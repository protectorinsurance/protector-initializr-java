//INITIALIZR:KAFKA
package no.protector.initializr.domain.configuration;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import no.protector.initializr.domain.model.Employee;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cloud.stream.schema.client.EnableSchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableSchemaRegistryClient
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;
    private final KafkaUser kafkaUser;

    public KafkaConfiguration(KafkaProperties kafkaProperties,
                              KafkaUser kafkaUser) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaUser = kafkaUser;
    }

    public KafkaStreamsConfiguration kafkaStreamsProperties() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildStreamsProperties());

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());

        props.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        props.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, kafkaUser.getUsernameAndPassword(":"));

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(kafkaProperties.buildAdminProperties());
    }

    @Bean
    public ProducerFactory<String, Employee> employeeProducerFactory() {
        Map<String, Object> configProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Employee> employeeKafkaTemplate(
            ProducerFactory<String, Employee> employeeProducerFactory) {
        return new KafkaTemplate<>(employeeProducerFactory);
    }

}
//INITIALIZR:KAFKA
