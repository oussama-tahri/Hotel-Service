let's have a look back to reservations service and controller:
when i make a reservation it appears 200 message but only take checkIN and checkOUT but roomDTO and clientDTO are null, and when i do checks with email or roomNumber i receive http message 500
let's have a look at our dtos:
for RservationDTO:
"package com.tahrioussama.hotelservice.dto;

import com.tahrioussama.hotelservice.enums.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class ReservationDTO {
    private Long id;
    private LocalDate checkIN;
    private LocalDate checkOUT;
    //    private double totalCost;
    private ReservationStatus status;
    private ClientDTO clientDTO;
    private RoomDTO roomDTO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationDTO)) return false;
        ReservationDTO that = (ReservationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
"
for roomDTO:
"package com.tahrioussama.hotelservice.dto;

import com.tahrioussama.hotelservice.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private RoomType roomType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomDTO)) return false;
        RoomDTO roomDTO = (RoomDTO) o;
        return Objects.equals(id, roomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
"
for ClientDTO:
"package com.tahrioussama.hotelservice.dto;


import lombok.Data;


@Data
public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
"
let's have a look at mappers :
roomMapper:
"package com.tahrioussama.hotelservice.mappers;

import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.entities.Room;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class RoomMapper {
    public RoomDTO fromRoom(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        BeanUtils.copyProperties(room,roomDTO);
        return roomDTO;
    }

    public Room fromRoomDTO(RoomDTO roomDTO) {
        Room room = new Room();
        BeanUtils.copyProperties(roomDTO,room);
        return room;
    }
}"
for reservationMapper:
"package com.tahrioussama.hotelservice.mappers;

import com.tahrioussama.hotelservice.dto.ReservationDTO;
import com.tahrioussama.hotelservice.entities.Reservation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ReservationMapper {
    public ReservationDTO fromReservation(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        BeanUtils.copyProperties(reservation,reservationDTO);
        return reservationDTO;
    }

    public Reservation fromReservationDTO(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationDTO,reservation);
        return reservation;
    }
}
"
for clientMapper:
"package com.tahrioussama.hotelservice.mappers;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.entities.Client;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ClientMapper {
    public ClientDTO fromClient(Client client) {
        ClientDTO clientDTO = new ClientDTO();
        BeanUtils.copyProperties(client,clientDTO);
        return clientDTO;
    }

    public Client fromClientDTO(ClientDTO clientDTO) {
        Client client = new Client();
        BeanUtils.copyProperties(clientDTO,client);
        return client;
    }
}
"
let's have a look to our ReservationService:
"package com.tahrioussama.hotelservice.service;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.dto.ReservationDTO;
import com.tahrioussama.hotelservice.dto.RoomDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tahrioussama.hotelservice.entities.Client;
import com.tahrioussama.hotelservice.entities.Reservation;
import com.tahrioussama.hotelservice.entities.Room;
import com.tahrioussama.hotelservice.exceptions.*;
import com.tahrioussama.hotelservice.mappers.ClientMapper;
import com.tahrioussama.hotelservice.mappers.ReservationMapper;
import com.tahrioussama.hotelservice.mappers.RoomMapper;
import com.tahrioussama.hotelservice.repositories.ClientRepository;
import com.tahrioussama.hotelservice.repositories.ReservationRepository;
import com.tahrioussama.hotelservice.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class ReservationServiceImpl implements IReservationService {

    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;
    private RoomMapper roomMapper;
    private ReservationMapper reservationMapper;
    private ClientMapper clientMapper;
    private ClientRepository clientRepository;

    @Override
    public boolean isRoomAvailable(RoomDTO roomDTO, LocalDate checkINDate, LocalDate checkOUTDate) {
        log.info("Checking for Reservation");
        // Convert RoomDTO to Room entity
        Room room = roomMapper.fromRoomDTO(roomDTO);

        // Ensure that room is not null before accessing its ID
        if (room == null) {
            return false; // Room not found, so it is not available
        }

        // Retrieve overlapping reservations
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                room.getId(),
                checkINDate,
                checkOUTDate
        );

        // Convert List<Reservation> to List<ReservationDTO>
        List<ReservationDTO> overlappingReservationsDTO = overlappingReservations.stream()
                .map(reservationMapper::fromReservation)
                .collect(Collectors.toList());

        // Check if the room is available (no overlapping reservations)
        return overlappingReservationsDTO.isEmpty();
    }


    @Override
    public ReservationDTO makeReservation(ClientDTO client, RoomDTO room, LocalDate checkin, LocalDate checkout) throws RoomNotFoundException, RoomNotAvailableException {
        // Check if the room exists in the database
        Room foundRoom = roomRepository.findById(room.getId()).orElseThrow(() -> new RoomNotFoundException("Room with ID " + room.getId() + " not found."));

        // Check if the room is available for the provided dates
        boolean isRoomAvailable = isRoomAvailable(room, checkin, checkout);
        if (!isRoomAvailable) {
            throw new RoomNotAvailableException("Room with ID " + room.getId() + " is not available for the specified dates.");
        }

        if (checkin.isAfter(checkout)) {
            throw new IllegalArgumentException("Check-in date must be before the check-out date.");
        }

        // Create a new ReservationDTO object with the provided data
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setClientDTO(client);
        reservationDTO.setRoomDTO(room);
        reservationDTO.setCheckIN(checkin);
        reservationDTO.setCheckOUT(checkout);

        // Calculate the total price for the reservation
//        Double totalPrice = reservationDTO.calculateTotalPrice(room);

        // Convert the ReservationDTO to the Reservation entity using the ReservationMapper
        Reservation reservationEntity = reservationMapper.fromReservationDTO(reservationDTO);

        // Save the reservation entity in the database using the repository
        Reservation savedReservation = reservationRepository.save(reservationEntity);

        // Convert the saved Reservation entity back to ReservationDTO using the ReservationMapper
        ReservationDTO savedReservationDTO = reservationMapper.fromReservation(savedReservation);

        // Return the saved reservation DTO
        return savedReservationDTO;
    }



    @Override
    public void cancelReservation(Long reservationId) throws ReservationNotFoundException {
        log.info("Canceling Reservation");

        // Check if the reservation with the given ID exists in the database
        if (!reservationRepository.existsById(reservationId)) {
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " not found.");
        }

        // Retrieve the reservation from the database by reservation ID
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        // Check if the reservation exists
        if (optionalReservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " not found.");
        }

        // Convert the optional reservation to Reservation entity
        Reservation reservation = optionalReservation.get();

        // Convert the Reservation entity to ReservationDTO using the reservationMapper
        ReservationDTO reservationDTO = reservationMapper.fromReservation(reservation);

        // Remove the reservation from the database
        reservationRepository.delete(reservation);
    }


    @Override
    public List<ReservationDTO> getReservationsByClient(ClientDTO clientDTO) throws ClientNotFoundException, ClientSaveException {
        // Convert ClientDTO to Client entity using the ClientMapper
        Client client = clientMapper.fromClientDTO(clientDTO);

        // Check if the client exists in the database
        Client existingClient = clientRepository.findByEmail(client.getEmail());

        if (existingClient != null) {
            // If the client exists, use the existing client to fetch reservations
            client = existingClient;
        } else {
            try {
                // If the client does not exist, save it in the database
                client = clientRepository.save(client);
            } catch (DataAccessException ex) {
                // If there's an issue while saving the client, throw a ClientSaveException
                throw new ClientSaveException("Failed to save the client in the database.", ex);
            }
        }

        // If the client is still null at this point, it means the client was not found in the database
        if (client == null) {
            throw new ClientNotFoundException("Client not found in the database.");
        }

        // Retrieve all reservations for the specific client
        List<Reservation> reservations = reservationRepository.findByClient(client);

        // Convert List<Reservation> to List<ReservationDTO> using the reservationMapper
        return reservations.stream()
                .map(reservationMapper::fromReservation)
                .collect(Collectors.toList());
    }



    @Override
    public List<ReservationDTO> getReservationsByRoom(RoomDTO roomDTO) {
        // Convert RoomDTO to Room entity using the roomMapper
        Room room = roomMapper.fromRoomDTO(roomDTO);

        // Retrieve all reservations for the specific room
        List<Reservation> reservations = reservationRepository.findByRoom(room);

        // Convert List<Reservation> to List<ReservationDTO> using the reservationMapper
        return reservations.stream()
                .map(reservationMapper::fromReservation)
                .collect(Collectors.toList());
    }
}
"
now let's look at reservationController:
"package com.tahrioussama.hotelservice.web;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.dto.ReservationDTO;
import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.exceptions.*;
import com.tahrioussama.hotelservice.service.ReservationServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping("/check-availability")
    public boolean isRoomAvailable(@RequestBody RoomDTO roomDTO,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkINDate,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOUTDate) {
        return reservationService.isRoomAvailable(roomDTO, checkINDate, checkOUTDate);
    }

    @PostMapping("/make-reservation")
    public ReservationDTO makeReservation(@RequestBody ReservationDTO reservationRequestDTO) throws RoomNotFoundException, RoomNotAvailableException {
        return reservationService.makeReservation(
                reservationRequestDTO.getClientDTO(),
                reservationRequestDTO.getRoomDTO(),
                reservationRequestDTO.getCheckIN(),
                reservationRequestDTO.getCheckOUT()
        );
    }

    @DeleteMapping("/cancel-reservation/{reservationId}")
    public void cancelReservation(@PathVariable Long reservationId) throws ReservationNotFoundException {
        reservationService.cancelReservation(reservationId);
    }

    @GetMapping("/client/{email}")
    public List<ReservationDTO> getReservationsByClient(@PathVariable String email) throws ClientNotFoundException, ClientSaveException {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail(email);
        return reservationService.getReservationsByClient(clientDTO);
    }

    @GetMapping("/room/{roomNumber}")
    public List<ReservationDTO> getReservationsByRoom(@PathVariable String roomNumber) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setRoomNumber(roomNumber);
        return reservationService.getReservationsByRoom(roomDTO);
    }
}
"
and finally let's look at our CommandLineRunner in main:
"# Hotel Service Project

## Introduction

This project is a Hotel Service Application that provides functionalities to manage clients, rooms, and reservations. It allows clients to register, view available rooms, and make reservations for specific dates.

## Technologies Used

- Java: The core programming language used for backend development.
- Spring Boot: The framework used to build the application, providing dependency injection, MVC architecture, and more.
- Spring Data JPA: Simplifies working with databases by providing JPA implementation for data access.
- Hibernate: The ORM (Object-Relational Mapping) tool used to map Java objects to database entities.
- H2 Database: An in-memory database used for testing and development purposes.
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

---"