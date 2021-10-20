package no.protector.initializr.kafka.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class KafkaConsumerHealthIndicator implements HealthIndicator {

    private final KafkaListenerEndpointRegistry registry;

    public KafkaConsumerHealthIndicator(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    public static Status containerStatus(MessageListenerContainer container) {
        if (container.isRunning()) {
            return Status.UP;
        }
        if (container.isContainerPaused()) {
            return Status.OUT_OF_SERVICE;
        }
        return Status.DOWN;
    }

    @Override
    @SuppressWarnings("java:S2221")
    public Health health() {
        try {
            return new Health.Builder()
                    .status(aggregateStatus())
                    .withDetails(aggregateDetails())
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    private Status aggregateStatus() {
        Set<Status> statusSet = this.registry.getAllListenerContainers().stream()
                .map(KafkaConsumerHealthIndicator::containerStatus)
                .collect(Collectors.toSet());
        return StatusAggregator.getDefault().getAggregateStatus(statusSet);
    }

    private Map<String, Object> aggregateDetails() {
        return registry.getAllListenerContainers().stream()
                .collect(Collectors.toMap(
                        MessageListenerContainer::getListenerId,
                        container -> Map.of("isRunning", container.isRunning(),
                                "isPaused", container.isContainerPaused())
                ));
    }

}
