# =====================================================================
# MiCoach — Backend (Spring Boot, monolito modular Gradle)
# Build: docker build -t micoach-backend -f backend/Dockerfile backend
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
RUN addgroup -S micoach && adduser -S micoach -G micoach
COPY --from=build /workspace/app/build/libs/micoach.jar app.jar
USER micoach

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
