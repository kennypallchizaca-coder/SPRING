# =========================
# Stage 1: Build
# =========================
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copiar todo el proyecto (evita errores por archivos faltantes)
COPY . .

# Construir el JAR ejecutable
RUN gradle bootJar --no-daemon

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/build/libs/*.jar app.jar

# Puerto estándar para Spring Boot
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java","-jar","app.jar"]
