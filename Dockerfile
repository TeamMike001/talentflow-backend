# Dockerfile
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy Maven wrapper files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable (FIX)
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests -B

EXPOSE 8080
CMD ["java", "-jar", "target/*.jar"]