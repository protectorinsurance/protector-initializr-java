//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.domain.configuration;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "kafka.producer")
    public Properties producerProperties() {
        Properties props = new Properties();
        props.putAll(Map.of(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class
        ));
        return props;
    }
}
//INITIALIZR:KAFKA-PRODUCER
