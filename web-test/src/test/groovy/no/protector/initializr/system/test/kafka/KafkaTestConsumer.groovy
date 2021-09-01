//INITIALIZR:KAFKA
package no.protector.initializr.system.test.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

import java.util.concurrent.CountDownLatch

/**
 * This is a simple kafka consumer that stores the latest message received. It is, however, very basic and should
 * work for most kinds of test, but for more advanced types of test that involved more than a single message
 * you might want to implement your own consumer for the various topics, or implement a queue.
 *
 * Usage:
 * <pre>
 * {@code
 * class mySpec extends AbstractSystemSpec {
*     @Autowired
*     private KafkaTestConsumer consumer
*     ...
*     def some_test() {
*          given:
*          ...
*          when:
*          ...
*          consumer.latch.await(1000, TimeUnit.MILLISECONDS)
*          then:
*          consumer.value == "Some value"
*     }
 * }
 * }
 * </pre>
 */
@Component
class KafkaTestConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestConsumer.class);

    //Since Kafka is async we have to insert some delay into our checks
    CountDownLatch latch = new CountDownLatch(1)
    String payload = null
    String value = null

    //TODO: Topics needs changing
    @KafkaListener(topics = "employee-read")
    void receive(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString())
        this.payload = consumerRecord.toString()
        this.value = consumerRecord.value()
        latch.countDown()
    }
}
//INITIALIZR:KAFKA
