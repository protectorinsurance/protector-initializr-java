package no.protector.initializr.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String> employeeListenerContainerFactory(
            ConsumerFactory<Integer, String> employeeConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(employeeConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> employeeConsumerFactory(
            KafkaConsumerConfigurationProperties kafkaConsumerConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigurationProperties.toConsumerConfig());
    }
}
