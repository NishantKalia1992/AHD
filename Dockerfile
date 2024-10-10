# Step 1: Use an official OpenJDK image as the base
FROM openjdk:17-jdk-slim

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the JAR file from the target folder into the container
COPY target/homedesire-0.0.1.jar app.jar

# Step 4: Expose the port that your Spring Boot app runs on
EXPOSE 8081

# Step 5: Run the application by pointing to 'app.jar'
cmd ["java", "-jar", "app.jar"]


