//INITIALIZR:DATABASE
package no.protector.initializr.system.test.config

import groovy.sql.Sql
import org.dbunit.JdbcDatabaseTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.MSSQLServerContainer

import java.sql.Connection
import java.sql.DriverManager

@Configuration
class PersistenceConfig {

    @Autowired
    @Qualifier("mssqlServerContainer")
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

    @Bean
    Sql datasource() {
        Sql.newInstance(
                mssqlServerContainer.getJdbcUrl(),
                'SA',
                'A_Str0ng_Required_Password',
                mssqlDriver
        )
    }
}
//INITIALIZR:DATABASE