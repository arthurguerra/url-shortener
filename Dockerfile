FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAR_NAME=urlshortener.jar
COPY --from=build /app/target/$JAR_NAME $JAR_NAME

EXPOSE ${SERVER_PORT}

CMD java -jar $JAR_NAME