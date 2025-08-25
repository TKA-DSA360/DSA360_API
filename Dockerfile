# Use an OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /usr/src/app

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8091

# Copy the compiled Java application JAR file into the container
COPY ./target/dsa360.jar .

# Define the command to run the Java application
CMD ["java", "-jar", "dsa360.jar"]
