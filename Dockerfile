# 1. Etapa de compilación con JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar el proyecto y compilar
COPY . .
RUN mvn clean package -DskipTests

# 2. Etapa de ejecución con JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar únicamente el jar ejecutable (evitando los archivos .original)
COPY --from=build /app/target/*[!original].jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
