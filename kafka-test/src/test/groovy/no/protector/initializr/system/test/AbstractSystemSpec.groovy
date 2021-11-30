package no.protector.initializr.system.test

import no.protector.initializr.system.test.config.ContainerConfig
import no.protector.initializr.system.test.config.PersistenceConfig
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.mssql.InsertIdentityOperation
import org.dbunit.operation.CompositeOperation
import org.mockserver.client.MockServerClient
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import java.sql.Connection

@EnableKafka
@SpringBootTest
@EnableSharedInjection
@ContextConfiguration(classes = [ContainerConfig, PersistenceConfig])
abstract class AbstractSystemSpec extends Specification {

    //INITIALIZR:DATABASE
    @Autowired
    JdbcDatabaseTester databaseTester

    @Autowired
    Connection connection
    //INITIALIZR:DATABASE

    @Shared
    @Autowired
    private MockServerClient mockServerClient

    def setupSpec() {
        mockServerClient.reset()
    }

//INITIALIZR:DATABASE
    def cleanAndInsertDataset(String dataset) {
        cleanAndInsertDatasets(dataset)
    }

    def cleanAndInsertDatasets(String... datasets) {
        disableConstraints()
        try {
            System.out.println(datasets.toString())
            datasets
                    .collect {
                        new FlatXmlDataSetBuilder().setColumnSensing(true).build(
                                AsyncTestUtils.class.getResourceAsStream("/dataset/$it"))
                    }
                    .each {
                        new CompositeOperation(
                                new InsertIdentityOperation(InsertIdentityOperation.CLEAN_INSERT),
                                new InsertIdentityOperation(InsertIdentityOperation.REFRESH)
                        ).execute(databaseTester.connection, it)
                    }
        }
        finally {
            enableConstraints()
        }
    }

    def disableConstraints() {
        connection.createStatement().execute('EXEC sp_msforeachtable "ALTER TABLE ? NOCHECK CONSTRAINT all"')
    }

    def enableConstraints() {
        connection.createStatement().execute('EXEC sp_msforeachtable "ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all"')
    }
//INITIALIZR:DATABASE
}
