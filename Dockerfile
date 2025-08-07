# Stage 1: Build the application using Gradle
FROM openjdk:21-slim
WORKDIR /opt/dividendoptimizer
COPY build/libs/dividendoptimizer-0.0.1.jar dividendoptimizer.jar
EXPOSE 8081

# Use renamed jar in the entrypoint
ENTRYPOINT ["java", "-jar", "dividendoptimizer.jar"]

# docker run -d -p 8081:8080 --name dividend-optimizer-app dividend-optimizer
