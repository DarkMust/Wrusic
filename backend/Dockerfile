# Stage 1: Build using Maven and Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy and build the project
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with slim JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the built JAR
COPY --from=build /app/target/*.jar app.jar

# Set environment variables for Last.fm API
ENV LASTFM_API_KEY=af2c2b998a230dfb3ed5a2bf254c8a78

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]