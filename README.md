# HarmoniApp - Backend

## Overview

HarmoniApp backend is a Java Spring Boot application that manages employee schedules, absences, real-time chat with Microsoft API integration for translation, and real-time notifications and much more. It also simplifies the management of employee schedules and workflows.

## Technologies Used

- **Java 21**

- **Spring Boot**

- **Spring Data JPA (Hibernate)**

- **WebSocket**

- **PostgreSQL**

- **Microsoft Translator API**

- **Maven**

- **Spring Test (for integration and application testing)**

- **JUnit (for unit testing)**

## Getting Started

### Prerequisites

1. Java 21 or later installed

2. Maven installed

3. PostgreSQL database configured

### Installation

1. Clone the repository:

```bash
  git clone https://github.com/HarmoniApp/backend
  ```

2. Navigate to the project directory:
```bash
cd backend
```

3. Create a .env file in the root directory and configure the necessary environment variables:
```env
API_MS_TRANSLATOR_KEY = {api_key}
DB_USER = {username}
DB_PASS = {password}
```

4. Install dependencies and build the project:
```bash
mvn clean install
```
5. Run the application:
```bash
mvn spring-boot:run
```
6. Run test
```bash
mvn test 
```

The backend will run on http://localhost:8080 by default.

## Project Structure
```
backend/
├── LICENSE.md
├── README.md
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── org
│   │   │       └── harmoniapp
│   │   │           ├── HarmoniWebApiApplication.java  # Main class
│   │   │           ├── configuration                  # App configuration (security, WebSocket)
│   │   │           ├── contracts                      # DTOs and contracts
│   │   │           ├── controllers                    # REST controllers
│   │   │           ├── entities                       # JPA entities
│   │   │           ├── enums                          # Enum definitions
│   │   │           ├── exception                      # Custom exceptions
│   │   │           ├── exceptionhandling              # Global exception handling
│   │   │           ├── filter                         # Security filters (JWT, CSRF)
│   │   │           ├── geneticalgorithm               # AI-based scheduling logic
│   │   │           ├── repositories                   # JPA repositories
│   │   │           ├── services                       # Business logic
│   │   │           └── utils                          # Utility classes
│   │   └── resources
│   │       ├── application.properties         # Default configuration
│   │       ├── application_prod.properties    # Production configuration
│   │       └── static/userPhoto               # Default user images
│   └── test
│       └── java/org/harmoniapp                # Unit and integration tests
└── target                                     # Compiled classes and packaged app
```

### Key Functionalities

- **Employee Management**: CRUD operations for managing employees.

- **Schedule Management**: Create, update, publish, import, and export work schedules.

- **Absence Management**: Manage employee absences with validation.

- **Real-Time Chat**: WebSocket-based chat with Microsoft Translator API integration.

- **Notifications**: Real-time notifications for important events.

- **AI-Powered Scheduling (PlannerAI)**: Advanced schedule generation using a genetic algorithm for optimized schedules planning.

- **Customization Options**: Personalize the application through role management, predefined shifts, departments, and contract types.

### Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

### License

This project is licensed under the Apache 2.0 with Commons Clause. See the LICENSE file for more details.

## Dependencies
This project uses:
* the Microsoft Translator Text API, licensed under the MIT License.  
  More details: https://www.microsoft.com/translator