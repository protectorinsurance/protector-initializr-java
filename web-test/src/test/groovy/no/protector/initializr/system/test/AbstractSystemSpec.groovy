package no.protector.initializr.system.test

import no.protector.initializr.system.test.config.ContainerConfig
import no.protector.initializr.system.test.config.EndpointConfig
import no.protector.initializr.system.test.config.PersistenceConfig
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.mssql.InsertIdentityOperation
import org.dbunit.operation.CompositeOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

import java.sql.Connection

@Testcontainers
@SpringBootTest
@ContextConfiguration(classes = [ContainerConfig, EndpointConfig, PersistenceConfig])
abstract class AbstractSystemSpec extends Specification {

    //INITIALIZER TAG: DATABASE
    @Autowired
    JdbcDatabaseTester databaseTester

    @Autowired
    Connection connection

    def cleanAndInsertDataset(String dataset) {
        cleanAndInsertDatasets(dataset)
    }

    def cleanAndInsertDatasets(String... datasets) {
        disableConstraints()
        try {
            System.out.println(datasets.toString())
            datasets
                    .collect {
                        new FlatXmlDataSetBuilder().build(
                                RequestService.class.getResourceAsStream("/dataset/$it"))
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
    //INITIALIZER TAG: DATABASE
}
