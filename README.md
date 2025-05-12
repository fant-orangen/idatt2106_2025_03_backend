# Backend

## Overview
This is a Spring Boot backend service built with Java 21. The application provides a RESTful API with security features, WebSocket support, and cloud storage integration.

## Technologies
- **Java Version**: 21
- **Framework**: Spring Boot 3.4.4
- **Build Tool**: Maven
- **Database**: JPA with H2 Database (Development)
- **Security**: Spring Security with JWT Authentication
- **Documentation**: OpenAPI (Swagger)
- **Testing**: JUnit, Mockito, TestContainers
- **Cloud Storage**: Cloudinary Integration
- **Communication**: WebSocket Support
- **Code Quality**: JaCoCo for test coverage
- **Development Tools**: Spring DevTools, Lombok

## Prerequisites
- Java 21 JDK
- Maven 3.x
- Docker (optional, for containerization)

## Getting Started

### Local Development Setup
1. Clone the repository
2. Navigate to the project directory
```bash
cd backend
```
3. Build the project
```bash
mvn clean install
```
4. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Docker Deployment
The project includes a Dockerfile for containerized deployment:

1. Build the Docker image
```bash
docker build -t backend-service .
```

2. Run the container
```bash
docker run -p 8080:8080 backend-service
```

## API Documentation
Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Features
- RESTful API endpoints
- JWT-based authentication and authorization
- WebSocket support for real-time communication
- File upload capabilities with Cloudinary integration
- Email service integration
- Comprehensive test coverage with JaCoCo reports
- Hot-reload for development with Spring DevTools

## Testing
The project uses multiple testing frameworks:
- JUnit and Mockito for unit testing
- TestContainers for integration testing
- Spring Security Test for security testing
- Rest Assured for API testing

Run tests with:
```bash
mvn test
```

Generate test coverage report:
```bash
mvn verify
```
JaCoCo test coverage reports will be generated in `target/site/jacoco/index.html`

## Project Structure
- `src/main/java` - Application source code
- `src/main/resources` - Configuration files and static resources
- `src/test` - Test cases
- `target` - Compiled bytecode and build artifacts

## Security
The application implements Spring Security with JWT authentication. Protected endpoints require a valid JWT token in the Authorization header.

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
