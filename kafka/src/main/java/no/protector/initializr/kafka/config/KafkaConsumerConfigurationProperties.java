//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(value = "kafka")
public class KafkaConsumerConfigurationProperties {

    private static final String SCHEMA_REGISTRY_URL = "schema.registry.url";

    private String bootstrapServers;
    private String clientId;
    private String schemaRegistryUrl;

    public Map<String, Object> toConsumerConfig() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.CLIENT_ID_CONFIG, clientId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class,
                SCHEMA_REGISTRY_URL, schemaRegistryUrl);
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setSchemaRegistryUrl(String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
}
//INITIALIZR:KAFKA-PRODUCER
