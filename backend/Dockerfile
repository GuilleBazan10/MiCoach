# =====================================================================
# KineticOs — Backend (Spring Boot, monolito modular Gradle)
# Build: docker build -t kineticos-backend -f backend/Dockerfile backend
# =====================================================================

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY settings.gradle build.gradle gradle.properties ./
COPY modules ./modules
COPY app ./app

RUN ./gradlew --no-daemon :app:bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S kineticos && adduser -S kineticos -G kineticos
COPY --from=build /workspace/app/build/libs/kineticos.jar app.jar
USER kineticos

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
