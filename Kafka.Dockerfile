FROM azul/zulu-openjdk-alpine:16.0.1 as initializr
COPY  /kafka/build/libs/kafka.jar .
COPY  /kafka/build/libs/elastic-apm-agent.jar /elasticapm.properties /
VOLUME ["/tmp","/var/log/", "/etc/protector/config"]
# HEALTHCHECK CMD curl --fail http://localhost:8391/actuator/health || exit 1
ENTRYPOINT ["java","-Xmx4096m","-Dfile.encoding=UTF-8", "-javaagent:/elastic-apm-agent.jar", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/kafka.jar"]