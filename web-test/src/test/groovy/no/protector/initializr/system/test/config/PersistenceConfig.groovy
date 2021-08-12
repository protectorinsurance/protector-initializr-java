//INITIALIZR:DATABASE
package no.protector.initializr.system.test.config

import org.dbunit.JdbcDatabaseTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.MSSQLServerContainer

import java.sql.Connection
import java.sql.DriverManager

@Configuration
class PersistenceConfig {

    @Autowired
    MSSQLServerContainer mssqlServerContainer

    private mssqlDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"

    @Bean
    JdbcDatabaseTester databaseTester() {
        new JdbcDatabaseTester(
                mssqlDriver,
                mssqlServerContainer.getJdbcUrl(),
                mssqlServerContainer.username,
                mssqlServerContainer.password)
    }

    @Bean
    Connection connection() {
        Class.forName(mssqlDriver)
        DriverManager.getConnection(
                mssqlServerContainer.getJdbcUrl(),
                mssqlServerContainer.username,
                mssqlServerContainer.password)

    }
}
//INITIALIZR:DATABASE