package no.protector.initializr.system.test.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

import java.util.concurrent.CountDownLatch

@Component
class KafkaTestConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestConsumer.class);

    CountDownLatch latch = new CountDownLatch(1)
    String payload = null
    String value = null

    @KafkaListener(topics = "initializr-topic")
    void receive(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString())
        this.payload = consumerRecord.toString()
        this.value = consumerRecord.value()
        latch.countDown()
    }
}
