FROM azul/zulu-openjdk-alpine:11.0.10 as initializr
COPY  /web/build/libs/web.jar .
VOLUME ["/tmp","/var/log/", "/etc/protector/config"]
HEALTHCHECK CMD curl --fail http://localhost:8391/actuator/health || exit 1
ENTRYPOINT ["java","-Xmx4096m","-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/web.jar"]