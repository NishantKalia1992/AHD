# Use an official OpenJDK image as the base
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/homedesire-0.0.1.jar app.jar

# Copy the wait-for-it.sh script into the container
COPY wait-for-it.sh /wait-for-it.sh

# Give execute permissions to the wait-for-it.sh script
RUN chmod +x /wait-for-it.sh

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# Use wait-for-it.sh to wait for MySQL to be ready, then start Spring Boot
CMD ["./wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "app.jar"]
