# Bug Tracking App
BugTrackerApp - A Java Bug Tracking Application

# Features
- **User Management**
  - User authentication
  - Authorization based on user roles
    
- **Report Management**
  - Create, update, and close bug reports
  - Prioritize and assign bugs to specific team members
    
- **Project Management**
  - Create, update, and close projects
  - Create, update, and close sprints

- **Bug Relationships**
  - Establish and manage relationships between bugs for better tracking

- **Collaboration**
  - Comment on bug reports for effective communication
  - View revision history to track changes made to bug reports

## Under the Hood
This bug tracking app is written in Java using the Spring framework, incorporating Spring Security for user authentication, Spring Data JPA for database interaction and Envers for revision history tracking. The app exposes REST endpoints for requests with JSON content.

### Project Structure
- **`src/main/java`:** Main Java source code.
- **`src/main/resources`:** Yaml configuration files.
- **`src/test`:** Unit and integration tests.
- **`com.example.bugtracker.config`:** Configuration for the app, including security configuration.
- **`com.example.bugtracker.authentication`:** Custom components, including custom filters, to override default Spring Security classes for JWT and refresh token authentication.
- **`com.example.bugtracker.domain`:** Domain entities containing business logic and JPA mapping details.
- **`com.example.bugtracker.controller`:** Controllers routing incoming requests after user authorization and validation of inputs. 
- **`com.example.bugtracker.service`:** Service layer for orchestrating interactions between layers, handling transactions, and implementing application-specific rules.  While primarily delegating tasks to domain objects and repositories, it also encapsulates specific business logic.
- **`com.example.bugtracker.repository`:** Data access and interaction with the database.

### API Documentation
- The API is documented using Swagger UI. Explore the documentation at 'http://localhost:8080/swagger-ui/index.html'.
- (Note: Do not add '/' at the end of the URL).

### Testing
- The app contains mostly unit tests and few integration tests. Unit tests are not dependent on any mock library.
  Fake objects, which simulate specific behaviors, are used for isolated testing environments in unit tests.
  More tests need to be added to ensure the correct operation of the app.
  
# Getting Started

To run this program, follow these steps:

## Prerequisites
1. **Java Development Kit (JDK 17):**
   - Download JDK 17 (Java SE Development Kit 17) from Oracle's official website.

2. **H2 Database (Version 2.2.224):**
   - Download H2 Database from the official website.

## Database Configuration
**H2 Database Setup:**
- After H2 Database installation, create a database with the following parameters:
  - **URL:** `jdbc:h2:tcp://localhost//todo3`
  - **Username:** `sa` (no password required)
  - To create a database with a different URL, use `jdbc:h2:/[databasenameyouwant]`.

## Running the Program
- **Navigate to Program Directory:**
  - Open a command prompt window and navigate to the directory where the program is saved.

- **Execute the JAR File:**
  - Run the program by executing the JAR file in 'security2pro/out/artifacts/security2pro_jar/'.
  - If the JAR file is named 'firstapp', use the following command:
    ```
    java -jar firstapp.jar
    ```

## Initial User Setup
 **Default Users:** 
 The app includes three default users with different roles.

- **Admin user:**
  - Username: 'admin', Password: 'adminPassword'

- **Team Lead user:**
  - Username: 'teamLead', Password: 'teamLeadPassword'

- **Team Member user:**
  - Username: 'teamMember', Password: 'teamMemberPassword'

