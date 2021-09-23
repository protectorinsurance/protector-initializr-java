//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.domain.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfiguration {

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
    public ProducerFactory<Integer, String> employeeProducerFactory(
            KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigurationProperties.toProducerConfig());
    }

    @Bean
    public KafkaTemplate<Integer, String> employeeKafkaTemplate(
            ProducerFactory<Integer, String> employeeProducerFactory) {
        return new KafkaTemplate<>(employeeProducerFactory);
    }
    //INITIALIZR:INITIALIZR-DEMO
}
//INITIALIZR:KAFKA-PRODUCER
