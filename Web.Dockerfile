FROM openjdk:17-buster
COPY /web/build/libs/web.jar .
COPY /web/build/libs/elastic-apm-agent.jar /elasticapm.properties /
HEALTHCHECK CMD curl --fail http://localhost:8391/actuator/health || exit 1
ENTRYPOINT ["java","-Xmx4096m","-Dfile.encoding=UTF-8", "-javaagent:/elastic-apm-agent.jar", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/web.jar"]