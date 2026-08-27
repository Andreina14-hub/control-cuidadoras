# Etapa 1: Compilar la aplicación Spring Boot
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el código fuente
COPY . .

# Compilar omitiendo pruebas unitarias de forma limpia
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Etapa 2: Crear la imagen liviana de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto por defecto
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
