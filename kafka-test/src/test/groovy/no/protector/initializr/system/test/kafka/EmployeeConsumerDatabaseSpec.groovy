//INITIALIZR:INITIALIZR-DEMO
//INITIALIZR:DATABASE
package no.protector.initializr.system.test.kafka

import groovy.sql.Sql
import no.protector.initializr.system.test.AbstractSystemSpec
import no.protector.initializr.system.test.AsyncTestUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate

import java.util.concurrent.TimeUnit

/**
 * Verifies functionality when Kafka consumer and databases is selected
 */
class EmployeeConsumerDatabaseSpec extends AbstractSystemSpec {

    @Autowired
    KafkaTemplate employeeKafkaTemplate

    @Autowired
    Sql datasource

    @Autowired
    AsyncTestUtils asyncTestUtils

    def "When employee created it should be stored in database"() {
        given:
        cleanAndInsertDataset("EmployeeDataset.xml")
        when:
        employeeKafkaTemplate
                .send("create-employee-consumer", "Yolo Swaggins,Lord Of The Bling")
                .get(5, TimeUnit.SECONDS)
        then:
        def result = asyncTestUtils.execute(10, {
            datasource.firstRow("SELECT * FROM Employee WHERE First_name = 'Yolo Swaggins' ")
        })
        then:
        result.First_Name == "Yolo Swaggins"
        result.Last_Name == "Lord Of The Bling"
    }
}
//INITIALIZR:DATABASE
//INITIALIZR:INITIALIZR-DEMO
