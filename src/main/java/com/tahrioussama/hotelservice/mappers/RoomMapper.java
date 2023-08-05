package com.tahrioussama.hotelservice.mappers;

import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.entities.Room;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class RoomMapper {
    public RoomDTO fromRoom(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        BeanUtils.copyProperties(room, roomDTO);
        return roomDTO;
    }

    public Room fromRoomDTO(RoomDTO roomDTO) {
        Room room = new Room();
        BeanUtils.copyProperties(roomDTO, room);
        return room;
    }
}