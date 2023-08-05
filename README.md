# Hotel Service Project

## Introduction

This project is a Hotel Service Application that provides functionalities to manage clients, rooms, and reservations. It allows clients to register, view available rooms, and make reservations for specific dates.

## Technologies Used

- Java: The core programming language used for backend development. 
- Spring Boot: The framework used to build the application, providing dependency injection, MVC architecture, and more.
- Spring Data JPA: Simplifies working with databases by providing JPA implementation for data access.
- Hibernate: The ORM (Object-Relational Mapping) tool used to map Java objects to database entities.
- PostgreSQL: The relational database management system used for data storage.
- JSON Web Token (JWT): For secure authentication and authorization of users.
- Maven: The build tool used for managing project dependencies and packaging.
- Git: Version control system for collaborative development.
- Docker: Containerization platform to create, deploy, and run applications in containers.
- Docker Compose: A tool for defining and running multi-container Docker applications.

## Features

1. **Client Management:**
   - Clients can register with the application using their email and password.
   - Existing clients can authenticate using their registered credentials.

2. **Room Management:**
   - Admin users can add new rooms to the system, specifying room number and type (e.g., Suite, Single, etc.).
   - All users can view the list of available rooms.

3. **Reservation Management:**
   - Authenticated clients can make reservations for available rooms by specifying check-in and check-out dates.
   - The application ensures that the selected room is available during the specified dates.
   - Admin users can view reservations by client and reservations by room.

4. **Security using JWT:**
   - The application uses JSON Web Tokens (JWT) for secure authentication and authorization.
   - Clients are issued a JWT upon successful authentication, which they use for subsequent API calls.
   - JWTs are validated to ensure only authorized users can access specific resources.

## Setup and Installation

1. **Clone the Repository:**
   ```
   git clone https://github.com/oussama-tahri/Hotel-Service.git
   cd hotel-service
   ```

2. **Build and Run the Application:**
   ```
   mvn spring-boot:run
   ```

3. **Access the Application:**
   The application will be accessible at `http://localhost:8085`.

## Running with Docker Compose

To run the application using Docker Compose, follow these steps:

1. **Install Docker and Docker Compose:**
   Make sure you have Docker and Docker Compose installed on your machine. For instructions, visit the official Docker website: [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/).

2. **Build and Run Docker Containers:**
   From the project root directory, execute the following command:
   ```
   docker-compose up
   ```
   This will build and run the Docker containers defined in the `compose.yml` file. It includes the Spring Boot application and a PostgreSQL database container.

3. **Access the Application:**
   The application will be accessible at `http://localhost:8085` just like before, but now it's running inside a Docker container.

## API Endpoints

The application exposes the following API endpoints:

- **Client Endpoints:**
  - `POST /clients/register`: Register a new client.
  - `POST /clients/authenticate`: Authenticate a client and get a JWT.
  - ...

- **Room Endpoints:**
  - `POST /rooms/addRoom`: Add a new room (Admin only).
  - `GET /rooms/all`: Get a list of all available rooms.
  - ...

- **Reservation Endpoints:**
  - `POST /reservations/make-reservation`: Make a new reservation.
  - `GET /reservations/client/{email}`: Get reservations made by a specific client.
  - `GET /reservations/by-room/{roomNumber}`: Get reservations for a specific room.
  - ...

## Security and JWT

The application uses JWT for secure authentication and authorization. When a client successfully authenticates, a JWT token is issued containing the client's information and access permissions. This token is included in the request headers for subsequent API calls.

To ensure secure access to certain endpoints (e.g., reservation-related endpoints), the application validates the JWT to check if the client has the required permissions.

## Contribution

Contributions to the project are welcome! Feel free to create pull requests for bug fixes, improvements, or additional features.
