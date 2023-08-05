package com.tahrioussama.hotelservice.service;

import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.exceptions.DuplicateRoomException;
import com.tahrioussama.hotelservice.exceptions.RoomNotFoundException;

import java.util.List;

public interface IRoomService {
    RoomDTO addRoom(RoomDTO room) throws DuplicateRoomException;
    RoomDTO updateRoom(RoomDTO room) throws RoomNotFoundException;
    List<RoomDTO> getAllRooms();
    RoomDTO getRoomByNumber(String roomNumber) throws RoomNotFoundException;
    void deleteRoomById(Long id) throws RoomNotFoundException;

    RoomDTO findById(Long id) throws RoomNotFoundException;
}
