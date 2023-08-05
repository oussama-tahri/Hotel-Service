package com.tahrioussama.hotelservice.mappers;

import com.tahrioussama.hotelservice.dto.ReservationDTO;
import com.tahrioussama.hotelservice.entities.Reservation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ReservationMapper {

    private final ClientMapper clientMapper;
    private final RoomMapper roomMapper;

    public ReservationMapper(ClientMapper clientMapper, RoomMapper roomMapper) {
        this.clientMapper = clientMapper;
        this.roomMapper = roomMapper;
    }

    public ReservationDTO fromReservation(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        BeanUtils.copyProperties(reservation, reservationDTO);
        reservationDTO.setClientDTO(clientMapper.fromClient(reservation.getClient()));
        reservationDTO.setRoomDTO(roomMapper.fromRoom(reservation.getRoom()));
        return reservationDTO;
    }

    public Reservation fromReservationDTO(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationDTO, reservation);
        reservation.setClient(clientMapper.fromClientDTO(reservationDTO.getClientDTO()));
        reservation.setRoom(roomMapper.fromRoomDTO(reservationDTO.getRoomDTO()));
        return reservation;
    }
}
