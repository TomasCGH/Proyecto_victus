# ================================
# 🧱 Dockerfile multi-stage para message-service
# ================================
# - Builder: usa Maven + Eclipse Temurin 21 para compilar sin tests (cache de dependencias)
# - Runtime: imagen ligera basada en eclipse-temurin:21-jre-alpine
# - Incluye el agente OpenTelemetry (OTel Java Agent) en /otel/otel-javaagent.jar
# ================================

# ---------------------------
# Stage: builder
# ---------------------------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copiar sólo pom.xml primero para aprovechar cache de dependencias
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# Copiar código fuente y otros ficheros necesarios para compilar
COPY src ./src
COPY mvnw ./mvnw
COPY .mvn .mvn

# Compilar el jar sin ejecutar tests
RUN mvn -B -DskipTests -Dmaven.test.skip=true package

# Preparar artefacto final
RUN mkdir -p /workspace/app && cp target/*.jar /workspace/app/app.jar

# ---------------------------
# Stage: runtime
# ---------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar utilidades y preparar el usuario
RUN apk add --no-cache curl \
    && addgroup -S app && adduser -S app -G app \
    && mkdir -p /otel \
    && curl -sSL -o /otel/otel-javaagent.jar "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.32.0/opentelemetry-javaagent.jar" \
    && chown -R app:app /app /otel

# Copiar el jar compilado desde el builder
COPY --from=builder /workspace/app/app.jar /app/app.jar

# Ejecutar como usuario no-root
USER app

# Puerto por defecto
EXPOSE 8082

# Healthcheck usando Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# ================================
# Variables de entorno (modificables en contenedor)
# ================================
ENV SERVER_PORT=8082
ENV SPRING_APPLICATION_NAME=message-service

# OpenTelemetry (monitoring opcional)
ENV OTEL_SERVICE_NAME=message-service
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_TRACES_SAMPLER=parentbased_always_on

# Ejecutar aplicación con el agente OTel
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -javaagent:/otel/otel-javaagent.jar -jar /app/app.jar"]
