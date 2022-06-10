FROM eclipse-temurin:11-jdk-alpine AS builder
COPY . /usr/src/
RUN --mount=type=cache,target=/root/.gradle \
    cd /usr/src/ && \
    backend/gradlew -i --no-daemon clean shadowjar

FROM eclipse-temurin:11-jre-alpine
COPY --from=0 /usr/src/backend/build/libs/*.jar /usr/app/backend.jar
COPY --from=0 /usr/src/plugins/*-plugin/build/libs/*.jar /usr/app/plugins/
EXPOSE 8080

RUN adduser -s /bin/false -S -D -H app
WORKDIR /usr/app/
USER app

CMD ["java", "-jar", "backend.jar"]