# User Management Service
_The User Management Service provides secure authentication and user management functionalities through a RESTful API._

## Features

1. **User Registration:** Endpoint to register new users with validation for unique email addresses and strong password policies.

2. **User Profile Management:** APIs for fetching user details and updating profiles.

3. **JWT Authentication:** Secure user authentication mechanism utilizing JSON Web Tokens to authenticate and authorize users.

## Technologies

- **Spring Boot:** Framework for building Java applications, providing essential tools for web development.

- **Spring Security:** Provides authentication and access control features for securing Spring-based applications.

- **Jakarta Validation API:** Implements validation constraints for domain models.

- **JUnit and Mockito:** Testing frameworks for unit and integration testing to ensure reliability and functionality.

## Endpoints

**AuthController**
+ `POST: /api/auth/registration` - Endpoint for user registration.
+ `POST: /api/auth/login` - Endpoint for user login and JWT token generation.

**UserController:**
+ `GET: /api/users/{userId}` - Endpoint for fetching user details.
+ `PUT: /api/users` - Endpoint for updating the details of the currently authenticated user.
+ `PATCH: /api/users` - Endpoint for patching the details of the currently authenticated user.
+ `DELETE: /api/users` - Endpoint for deleting the details of the currently authenticated user.
+ `GET: /api/users/search-by-birthdate-range` - Endpoint for searching for users based on their birth date within a specified range.
