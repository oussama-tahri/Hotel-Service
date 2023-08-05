package com.tahrioussama.hotelservice.repositories;

import com.tahrioussama.hotelservice.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room,Long> {

    // Custom query to check if a room with a given room number exists
    @Query("SELECT COUNT(r) > 0 FROM Room r WHERE r.roomNumber = ?1")
    boolean existsByRoomNumber(String roomNumber);
    Room findByRoomNumber(String roomNumber);
}
