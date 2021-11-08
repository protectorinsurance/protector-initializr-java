package no.protector.initializr.system.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.testcontainers.containers.GenericContainer

import java.util.concurrent.TimeoutException

@Component
class AsyncTestUtils {

    @Autowired
    GenericContainer protectorInitializrContainer

    /**
     * Repeats some operations for a number of seconds until it has a list with
     * Values or a value that is not null.
     *
     * The idea is that it will return a value as soon as it is ready, so that we
     * avoid `Thread.Sleep` and `Wait` that slows down the tests.
     *
     * It is a naive implementation, so don't put long-running tasks here.
     *
     * Here's an example usage that will time out after 10 seconds:
     * <pre>
     * {@code
     * def result = asyncTestUtils.execute(10, {*  datasource.firstRow("SELECT * FROM Employee WHERE Id = 1")
     *})
     *}
     * </pre>
     *
     * @param timeoutSeconds - Number of seconds before an exception is thrown
     * @param operation - A closure that has some return value
     * @return
     */
    def <T> T execute(int timeoutSeconds, Closure<T> operation) {
        T result = null
        long startTime = System.currentTimeSeconds()
        while (!isValidValue(result)) {
            if ((System.currentTimeSeconds() - startTime) > timeoutSeconds)
                throw new TimeoutException(getExceptionMessage())
            result = operation()
        }
        result
    }

    private String getExceptionMessage() {
        """Could not find valid value \n Here's the application logs: \n ${protectorInitializrContainer.logs}"""
    }

    private static <T> boolean isValidValue(T value) {
        if (value == null)
            return false
        if (value instanceof Iterable)
            return ((Iterable) value).size() > 1
        return true
    }
}
