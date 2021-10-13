//INITIALIZR:DATABASE
package no.protector.initializr.system.test.provider

import org.flywaydb.core.Flyway
import org.testcontainers.containers.MSSQLServerContainer

abstract class FlywayProvider {

    static build(MSSQLServerContainer mssqlServerContainer) {
        Flyway.configure()
                .locations("filesystem:../flyway")
                .validateOnMigrate(true)
                .cleanDisabled(false)
                .baselineOnMigrate(true)
                .dataSource(mssqlServerContainer.getJdbcUrl(), mssqlServerContainer.username, mssqlServerContainer.password)
                .load()
    }
}
//INITIALIZR:DATABASE