# ---- Build Stage ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY settings.gradle.kts build.gradle.kts ./
COPY cli/ cli/
COPY core/ core/
COPY plugins/ plugins/
COPY runtime/ runtime/
COPY packaging/ packaging/
COPY distribution/ distribution/
COPY testing/ testing/

RUN ./gradlew build -x javadoc --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S voxen && adduser -S voxen -G voxen

COPY --from=builder /workspace/cli/build/libs/*.jar app.jar

RUN chown -R voxen:voxen /app

USER voxen

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--help"]
