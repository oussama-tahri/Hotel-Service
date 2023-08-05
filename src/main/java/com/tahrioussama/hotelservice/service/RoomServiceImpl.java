package com.tahrioussama.hotelservice.service;

import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.entities.Room;
import com.tahrioussama.hotelservice.exceptions.DuplicateRoomException;
import com.tahrioussama.hotelservice.exceptions.RoomNotFoundException;
import com.tahrioussama.hotelservice.mappers.RoomMapper;
import com.tahrioussama.hotelservice.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class RoomServiceImpl implements IRoomService {

    private RoomRepository roomRepository;
    private RoomMapper roomMapper;

    @Override
    public RoomDTO addRoom(RoomDTO roomDTO) throws DuplicateRoomException {
        log.info("Saving new Room");

        // Check if a room with the same room number already exists
        if (roomRepository.existsByRoomNumber(roomDTO.getRoomNumber())) {
            throw new DuplicateRoomException("Room with number " + roomDTO.getRoomNumber() + " already exists.");
        }
        // Convert RoomDTO to Room entity using the roomMapper
        Room room = roomMapper.fromRoomDTO(roomDTO);
        // Save new user in database
        Room savedRoom = roomRepository.save(room);
        // Convert Room entity to RoomDTO using the roomMapper
        return roomMapper.fromRoom(savedRoom);
    }

    @Override
    public RoomDTO updateRoom(RoomDTO roomDTO) throws RoomNotFoundException {
        log.info("Updating the Room");
        // Check if the room to be updated exists in the database
        if (!roomRepository.existsById(roomDTO.getId())) {
            throw new RoomNotFoundException("Room with ID " + roomDTO.getId() + " not found.");
        }
        // Convert RoomDTO to Room entity using the roomMapper
        Room room = roomMapper.fromRoomDTO(roomDTO);
        // Save new user in database
        Room savedRoom = roomRepository.save(room);
        // Convert Room entity to RoomDTO using the roomMapper
        return roomMapper.fromRoom(savedRoom);
    }

    @Override
    public List<RoomDTO> getAllRooms() {
        List<Room> allRooms = roomRepository.findAll();
        List<RoomDTO> roomDTOList = allRooms.stream()
                .map(roomMapper::fromRoom)
                .collect(Collectors.toList());
        return roomDTOList;
    }

    @Override
    public RoomDTO getRoomByNumber(String roomNumber) throws RoomNotFoundException {
        // Retrieve the Room entity from the database by room number
        Room room = roomRepository.findByRoomNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Room with number " + roomNumber + " not found.");
        }

        // Now, we need to check if the retrieved room actually has the same room number as requested
        if (!room.getRoomNumber().equals(roomNumber)) {
            throw new RoomNotFoundException("Room with number " + roomNumber + " not found.");
        }

        // Convert Room entity to RoomDTO using the RoomMapper
        return roomMapper.fromRoom(room);
    }

    @Override
    public void deleteRoomById(Long id) throws RoomNotFoundException {
        // Check if the room with the given ID exists in the database
        if(!roomRepository.existsById(id)) {
            throw new RoomNotFoundException("Room with ID: "+id+" Not Found!");
        }

        // Retrieve the room from the database by reservation ID
        Optional<Room> OptionalRoom = roomRepository.findById(id);

        // Check if the reservation exists
        if (OptionalRoom.isEmpty()) {
            throw new RoomNotFoundException("Room with ID: "+id+" Not Found!");
        }

        // Convert the optional room to Room entity
        Room room = OptionalRoom.get();

        // Convert the Room entity to RoomDTO using the roomMapper
        RoomDTO roomDTO = roomMapper.fromRoom(room);

        roomRepository.delete(room);
    }

    @Override
    public RoomDTO findById(Long id) throws RoomNotFoundException {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room with ID: "+id+" Not Found!"));
        return roomMapper.fromRoom(room);
    }
}
