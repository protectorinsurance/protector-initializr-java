package no.protector.initializr.kafka.config;

import no.protector.initializr.domain.configuration.KafkaConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {
    @Bean
    public ConsumerFactory<Integer, String> employeeProducerFactory(
            KafkaConfigurationProperties kafkaConsumerConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigurationProperties.toProducerConfig());
    }
}
