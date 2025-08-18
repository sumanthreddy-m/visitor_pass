
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application's executable JAR file
COPY target/visitor-pass-management-*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 4032


# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
