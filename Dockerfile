# Etapa de build
FROM maven:latest AS build
COPY . /app
WORKDIR /app
# Copiar o diretório lib
COPY lib/ /app/lib/
# Instalar o JAR no repositório local do Maven
RUN mvn install:install-file -Dfile=/app/lib/JxInsta-v1.0-beta-2.jar -DgroupId=com.errorxcode -DartifactId=jxinsta -Dversion=v1.0-beta-2 -Dpackaging=jar
# Construir o projeto
RUN mvn clean package -DskipTests

# Etapa de runtime
FROM amazoncorretto:21.0.8-alpine3.22
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]