package com.tahrioussama.hotelservice.web;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.dto.ReservationDTO;
import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.exceptions.*;
import com.tahrioussama.hotelservice.service.ReservationServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
@CrossOrigin("*")
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping("/check-availability")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public boolean isRoomAvailable(@RequestBody RoomDTO roomDTO,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkINDate,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOUTDate) {
        return reservationService.isRoomAvailable(roomDTO, checkINDate, checkOUTDate);
    }

    @PostMapping("/make-reservation")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ReservationDTO makeReservation(@RequestBody ReservationDTO reservationRequestDTO) throws RoomNotFoundException, RoomNotAvailableException {
        return reservationService.makeReservation(
                reservationRequestDTO.getClientDTO(),
                reservationRequestDTO.getRoomDTO(),
                reservationRequestDTO.getCheckIN(),
                reservationRequestDTO.getCheckOUT()
        );
    }

    @DeleteMapping("/cancel-reservation/{reservationId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void cancelReservation(@PathVariable Long reservationId) throws ReservationNotFoundException {
        reservationService.cancelReservation(reservationId);
    }

    @GetMapping("/client/{email}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<ReservationDTO> getReservationsByClient(@PathVariable String email) throws ClientNotFoundException, ClientSaveException {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail(email);
        return reservationService.getReservationsByClient(clientDTO);
    }

    @GetMapping("/room/{roomNumber}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<ReservationDTO> getReservationsByRoom(@PathVariable String roomNumber) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setRoomNumber(roomNumber);
        return reservationService.getReservationsByRoom(roomDTO);
    }
}