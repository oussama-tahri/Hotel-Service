package com.tahrioussama.hotelservice.service;

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

    private ReservationRepository reservationRepository;
    private RoomMapper roomMapper;
    private ReservationMapper reservationMapper;
    private ClientMapper clientMapper;
    private ClientRepository clientRepository;
    private IRoomService roomService;

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
                .toList();

        // Check if the room is available (no overlapping reservations)
        return overlappingReservationsDTO.isEmpty();
    }


    @Override
    public ReservationDTO makeReservation(ClientDTO client, RoomDTO room, LocalDate checkin, LocalDate checkout) throws RoomNotFoundException, RoomNotAvailableException {
        // Check if the room exists in the database
        RoomDTO foundRoom = roomService.getRoomByNumber(room.getRoomNumber());
        if (foundRoom == null) {
            throw new RoomNotFoundException("Room with number " + room.getRoomNumber() + " not found.");
        }

        // Check if the room is available for the provided dates
        boolean isRoomAvailable = isRoomAvailable(foundRoom, checkin, checkout);
        if (!isRoomAvailable) {
            throw new RoomNotAvailableException("Room with number " + foundRoom.getRoomNumber() + " is not available for the specified dates.");
        }

        if (checkin.isAfter(checkout)) {
            throw new IllegalArgumentException("Check-in date must be before the check-out date.");
        }

        // Convert the ClientDTO to Client entity using the ClientMapper
        Client clientEntity = clientMapper.fromClientDTO(client);

        // Create a new ReservationDTO object with the provided data
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setClientDTO(client);
        reservationDTO.setRoomDTO(foundRoom);
        reservationDTO.setCheckIN(checkin);
        reservationDTO.setCheckOUT(checkout);

        // Convert the ReservationDTO to the Reservation entity using the ReservationMapper
        Reservation reservationEntity = reservationMapper.fromReservationDTO(reservationDTO);

        // Save the reservation entity in the database using the repository
        Reservation savedReservation = reservationRepository.save(reservationEntity);

        // Convert the saved Reservation entity back to ReservationDTO using the ReservationMapper

        // Return the saved reservation DTO
        return reservationMapper.fromReservation(savedReservation);
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

        // Add debug log to check the room entity data
        log.info("Room entity: " + room);

        // Retrieve all reservations for the specific room
        List<Reservation> reservations = reservationRepository.findByRoom(room);

        // Convert List<Reservation> to List<ReservationDTO> using the reservationMapper
        return reservations.stream()
                .map(reservationMapper::fromReservation)
                .collect(Collectors.toList());
    }

}