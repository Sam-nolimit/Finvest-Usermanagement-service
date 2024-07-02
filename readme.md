# 2. User Authentication and Authorization System

## Overview
This project implements a user authentication and authorization system using Spring Boot and Spring Security. It is utilizes a relational database(postgres) for data storage.
## Table of Contents
- [Setup Instructions](#setup-instructions)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Documentation](#documentation)
- [Error Handling](#error-handling)

## Setup Instructions
To run the application locally, follow these steps:

1. **Clone the Repository:**
   ```bash
   https://github.com/Sam-nolimit/assessment-prunny-java.git
Save to grepper
Navigate to the Project Directory:

bash
Copy code
cd auth
Build and Run the Application:

bash
Copy code
./mvnw spring-boot:run
The application will be accessible at http://localhost:8080.

Access postgres Database Console:
The postgres database console is available at http://localhost:8080/PrunnySecurityDB.
Use the JDBC URL jdbc:postgresql://localhost:5432/PrunnySecurityDB to connect. Update the values in application.properties accordingly.

API Endpoints

Admin Registration
Endpoint: POST /api/v1/auth/register-admin
Description: Registers the Admin.

User Registration
Endpoint: POST /api/v1/auth/register
Description: Register as a user.

Login
Endpoint: POST api/v1/auth/authenticate
Description: Login either the admin or user.

Database
The application uses a Postgres database for data storage. JPA (Java Persistence API) is employed for database interactions. The provided application.properties file includes database configuration.


Documentation
API documentation is crucial for developers and users to understand how to interact with the application. Documentation for this project can be found in the docs directory. Link to API Documentation

Error Handling
Proper error handling is implemented throughout the API. The application returns appropriate HTTP status codes and error messages to indicate the result of each request.


SPRING BOOT DEVELOPER PROJECT ASSESSMENT

2. User Authentication and Authorization System
Project Description: Develop a user authentication and authorization system using Spring
Boot and Spring Security.
Requirements:
 Implement user registration and login functionalities.
 Secure endpoints using JWT (JSON Web Token).
 Define roles (e.g., USER, ADMIN) and restrict access to certain endpoints based on
roles.
 Use a relational database to store user credentials and roles.
 Implement password hashing using BCrypt.
 Create a comprehensive README with setup instructions and API documentation.
