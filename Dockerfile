# Use an older base image for testing purposes
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/payment-processing-service-*.jar app.jar

# Copy configuration
COPY src/main/resources/config.yml config.yml

# Expose port
EXPOSE 8080

# Set JVM options for outdated configurations
ENV JAVA_OPTS="-Xmx1024m -Xms512m -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar server config.yml"]