# Spring Boot JWT Authentication with Access & Refresh Tokens

## Overview

This repository contains a Spring Boot project implementing JWT-based authentication with both **Access** and **Refresh Tokens**. The project demonstrates secure authentication, user registration, and token management using JWT tokens, cookies, and Spring Security.

### Key Features:
- User login and registration with JWT authentication
- Access token sent in the `Authorization` header
- Refresh token stored in HttpOnly cookies
- Stateless session management
- Role-based access control (e.g., `USER` role)
- Spring Security configuration for JWT filtering

## Prerequisites

Before running the project, ensure that the following are installed:

- **JDK 17+**: Make sure you have a compatible JDK installed.
- **Maven**: Used for project dependency management and build.
- **Postman or similar API client** (optional): To test the API endpoints.

# Getting Started

## Clone the repository

```
git clone https://github.com/SivvalaChandu/Spring-Boot-JWT-Access-Refresh-Auth.git
cd Spring-Boot-JWT-Access-Refresh-Auth
```

## Install dependencies
Run the following command to install the necessary dependencies using Maven:
```
mvn clean install
```

## Running the Application
To run the application, use the following Maven command:
```
mvn spring-boot:run
```
The application will run on `http://localhost:8080` by default.

# Endpoints
## 1. User Registration
URL:  `/api/auth/register`
    Method: `POST`
    Body:
```
    {
      "username": "exampleUser",
      "password": "password123",
      "email": "user@example.com"
    }
  ```
**Description**: Registers a new user and returns an access token in the Authorization header and a refresh token in an HttpOnly cookie.

## 2. User Login
URL: `/api/auth/login`
    Method: `POST`
    Body:
    
```
    {
        "username": "exampleUser",
        "password": "password123"
    }
```
**Description**: Logs in an existing user and returns an access token in the Authorization header and a refresh token in an HttpOnly cookie.

## 3. Get All Users (Protected)
URL: `/api/users/`
    Method: `GET`
    Authorization: Requires valid access token in the Authorization header (Bearer token).
    Description: Returns a list of all users.

## 4. Get User By ID (Protected)
URL: `/api/users/{id}`
    Method: `GET`
    Authorization: Requires valid access token in the Authorization header (Bearer token).
    Description: Fetches a user by ID.

## 5. Update User (Protected)
URL: `/api/users/{id}`
    Method: `PUT`
    Authorization: Requires valid access token in the Authorization header (Bearer token).
    Body:
```
    {
      "username": "newUsername",
      "email": "newEmail@example.com",
      "password": "newPassword123"
    }
```
**Description**: Updates the user with the given ID.

## 6. Delete User (Protected)
URL: `/api/users/{id}`
    Method: `DELETE`
    Authorization: Requires valid access token in the Authorization header (Bearer token).
    Description: Deletes a user by ID.
    
    
# Security Configuration
## JWT Authentication Filter

This project uses a custom `JwtAuthenticationFilter` that extends `OncePerRequestFilter`. It intercepts all HTTP requests to check for a valid access token in the `Authorization` header. If the access token is expired, it attempts to authenticate the user using the refresh token stored in an HttpOnly cookie.

## Spring Security Configuration
In the `SecurityConfig` class, we configure Spring Security to:

Disable CSRF (not needed for stateless authentication)
    Use stateless sessions (no HTTP session used)
    Add the `JwtAuthenticationFilter` to the filter chain before the `UsernamePasswordAuthenticationFilter`
    Permit unauthenticated access to the authentication endpoints (/api/auth/**)
    Protect all other endpoints with authentication and roles.

## Token Generation
**Access Token**: A short-lived JWT token used for accessing protected resources.
    **Refresh Token**: A long-lived JWT token used to get a new access token once the current one expires. It is stored securely in an HttpOnly cookie to prevent client-side JavaScript access.

## Example of Refresh Token Handling

If the access token expires, the `JwtAuthenticationFilter` checks for a valid refresh token in the request cookies. If valid, it issues a new access token and sends it in the `Authorization` header.

# Configuration
The JWT secret key and token expiration times are configured in the `JwtUtils` service. Change time as much as your requirements access token less time compare to refresh token

**Access Token Expiration Time**: 2 minutes
    **Refresh Token Expiration Time**: 8 minutes (configured in seconds)

## JWT Secret Key

The JWT secret key is hardcoded for simplicity, but it should be stored securely (e.g., in environment variables or a secrets manager) in a production environment.
```
private static final String SECURE_KEY = "your-secure-secret-key";
```
# Common Issues
`403` Forbidden or `401` Unauthorized

Ensure that you are passing a valid access token in the Authorization header for protected routes.
    Ensure that the refresh token is stored as an HttpOnly cookie for automatic handling.

## Invalid Credentials
Double-check the username and password during login. The system uses **BCryptPasswordEncoder** for password encoding.
