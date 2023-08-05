package com.tahrioussama.hotelservice.service;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.dto.ReservationDTO;
import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.exceptions.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IReservationService {
    boolean isRoomAvailable(RoomDTO roomDTO, LocalDate checkINDate, LocalDate checkOUTDate);
    ReservationDTO makeReservation(ClientDTO client, RoomDTO roomDTO, LocalDate CheckINDate, LocalDate CheckOUTDate) throws RoomNotFoundException, RoomNotAvailableException, ClientNotFoundException;


   // ReservationDTO makeReservation(ReservationDTO reservationDTO) throws ClientNotFoundException, RoomNotFoundException, RoomNotAvailableException;

   // ReservationDTO makeReservation(Long clientId, Long roomId, LocalDate checkIN, LocalDate checkOUT)
          //  throws RoomNotFoundException, RoomNotAvailableException, ClientNotFoundException;

    void cancelReservation(Long reservationId) throws ReservationNotFoundException;

    List<ReservationDTO> getReservationsByClient(ClientDTO client) throws ClientNotFoundException, ClientSaveException;
    List<ReservationDTO> getReservationsByRoom(RoomDTO room);
}
