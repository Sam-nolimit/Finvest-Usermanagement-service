FROM openjdk:17-jdk-alpine

# Define the JAR_FILE argument and set a default value
ARG JAR_FILE=target/auth-0.0.1-SNAPSHOT.jar

# Copy the jar file to the container
COPY ${JAR_FILE} app.jar

# Set the entrypoint to run the jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
