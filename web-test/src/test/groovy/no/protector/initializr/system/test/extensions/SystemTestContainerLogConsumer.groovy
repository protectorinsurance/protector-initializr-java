package no.protector.initializr.system.test.extensions

import org.testcontainers.containers.output.BaseConsumer
import org.testcontainers.containers.output.OutputFrame

import java.nio.charset.StandardCharsets

class SystemTestContainerLogConsumer extends BaseConsumer<SystemTestContainerLogConsumer> {

    private ByteArrayOutputStream stringBuffer = new ByteArrayOutputStream()

    @Override
    void accept(OutputFrame outputFrame) {
        try {
            if (outputFrame.getBytes() != null) {
                stringBuffer.write(outputFrame.getBytes())
                stringBuffer.flush()
            }
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

    void clearLogs() {
        stringBuffer.reset()
    }

    boolean hasLogs() {
        stringBuffer.toByteArray().size() != 0
    }

    String toString() {
        byte[] bytes = stringBuffer.toByteArray()
        return new String(bytes, StandardCharsets.UTF_8)
    }
}
