FROM gradle:8.3-jdk17 AS build
WORKDIR /workspace

COPY gradle gradle
COPY gradlew .

RUN chmod +x ./gradlew

COPY build.gradle settings.gradle ./

RUN ./gradlew --version || true
RUN ./gradlew --no-daemon dependencies || true

COPY src ./src
RUN ./gradlew -x test bootJar --no-daemon

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

COPY --from=build /workspace/build/libs/mascotas-perdidas-service-0.1.1-SNAPSHOT.jar /app/app.jar

EXPOSE 10000

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-10000} -jar /app/app.jar"]