//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
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

    public Map<String, Object> toProducerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.CLIENT_ID_CONFIG, clientId,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
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
