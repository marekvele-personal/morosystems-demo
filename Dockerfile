# syntax=docker/dockerfile:1.27

# -------- Stage 1: dependencies (cache) --------
FROM eclipse-temurin:25.0.4_7-jdk AS deps
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
RUN chmod +x gradlew
COPY build.gradle settings.gradle gradle.properties* ./

# Stáhnout závislosti (bez sestavení aplikace)
RUN --mount=type=cache,target=/root/.gradle,id=gradle-cache \
    ./gradlew --no-daemon -v

# -------- Stage 2: build --------
FROM eclipse-temurin:25.0.4_7-jdk AS build
WORKDIR /workspace

COPY . .
COPY --from=deps /workspace/gradlew ./gradlew
COPY --from=deps /workspace/gradle ./gradle

# Pokud používáte multi-module, upravte úlohu (bootJar/jar)
RUN --mount=type=cache,target=/root/.gradle,id=gradle-cache \
    ./gradlew --no-daemon -x test bootJar

# Zkopírujeme vytvořený JAR do jednotného názvu
RUN JAR_FILE=$(ls build/libs/*-boot.jar 2>/dev/null || ls build/libs/*.jar | head -n1) && \
    echo "Using JAR: $JAR_FILE" && \
    cp "$JAR_FILE" /workspace/app.jar

# -------- Stage 3: runtime --------
FROM eclipse-temurin:25.0.4_7-jre AS runtime
ENV TZ=UTC \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom" \
    JAVA_TOOL_OPTIONS=""

WORKDIR /app
RUN useradd -r -u 10001 -g root appuser
USER 10001

COPY --from=build /workspace/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar" ]
