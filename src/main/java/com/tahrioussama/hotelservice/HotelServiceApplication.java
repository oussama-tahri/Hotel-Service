package com.tahrioussama.hotelservice;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.dto.ReservationDTO;
import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.enums.RoomType;
import com.tahrioussama.hotelservice.exceptions.DuplicateRoomException;
import com.tahrioussama.hotelservice.exceptions.EmailAlreadyExistsException;
import com.tahrioussama.hotelservice.exceptions.RoomNotFoundException;
import com.tahrioussama.hotelservice.service.IClientService;
import com.tahrioussama.hotelservice.service.IReservationService;
import com.tahrioussama.hotelservice.service.IRoomService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class HotelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(IClientService clientService,
                                        IRoomService roomService,
                                        IReservationService reservationService) {
        return args -> {
            // Test cases for ClientService
            ClientDTO client1 = new ClientDTO();
            client1.setId(1L);
            client1.setFirstName("Oussama");
            client1.setLastName("Tahri");
            client1.setEmail("oussama@tahri.com");
            client1.setPassword("password1");
            try {
                clientService.registerClient(client1);
            } catch (EmailAlreadyExistsException ex) {
                System.out.println("Error while registering client 1: " + ex.getMessage());
            }

            ClientDTO client2 = new ClientDTO();
            client2.setId(2L);
            client2.setFirstName("Marouane");
            client2.setLastName("Bouchtaoui");
            client2.setEmail("marouane@bouchtaoui.com");
            client2.setPassword("password2");
            try {
                clientService.registerClient(client2);
            } catch (EmailAlreadyExistsException ex) {
                System.out.println("Error while registering client 1: " + ex.getMessage());
            }

            ClientDTO authenticatedClient = clientService.authenticateClient("oussama@tahri.com", "password1");
            System.out.println("Authenticated Client: " + authenticatedClient);

            // Test cases for RoomService
            RoomDTO room1 = new RoomDTO();
            room1.setId(1L);
            room1.setRoomNumber("777");
            room1.setRoomType(RoomType.SUITE);
            try {
                roomService.addRoom(room1);
            } catch (DuplicateRoomException ex) {
                System.out.println("Error while adding room 1: " + ex.getMessage());
            }

            RoomDTO room2 = new RoomDTO();
            room2.setId(2L);
            room2.setRoomNumber("2222");
            room2.setRoomType(RoomType.SINGLE);
            try {
                roomService.addRoom(room2);
            } catch (DuplicateRoomException ex) {
                System.out.println("Error while adding room 1: " + ex.getMessage());
            }

            List<RoomDTO> allRooms = roomService.getAllRooms();
            System.out.println("All Rooms: " + allRooms);

            RoomDTO roomByNumber = roomService.getRoomByNumber("777");
            System.out.println("Room by Number: " + roomByNumber);

            // Test cases for ReservationService
            // Generate a random number between 1 and 30 (for check-in duration)
            Random random = new Random();
            int randomCheckinDuration1 = random.nextInt(30) + 1;
            int randomCheckinDuration2 = random.nextInt(30) + 1;

            // Generate a random number between 1 and 60 (for stay duration)
            int randomStayDuration1 = random.nextInt(60) + 1;
            int randomStayDuration2 = random.nextInt(60) + 1;

            // Calculate the check-in and check-out dates using the random durations
            LocalDate checkinDate1 = LocalDate.now().plusDays(randomCheckinDuration1);
            LocalDate checkoutDate1 = checkinDate1.plusDays(randomStayDuration1);

            LocalDate checkinDate2 = LocalDate.now().plusDays(randomCheckinDuration2);
            LocalDate checkoutDate2 = checkinDate2.plusDays(randomStayDuration2);

            try {
                ReservationDTO reservation1 = reservationService.makeReservation(
                        client1, room1, checkinDate1, checkoutDate1);
                System.out.println("Reservation 1: " + reservation1);
            } catch (RoomNotFoundException ex) {
                System.out.println("Error while making Reservation 1: " + ex.getMessage());
            }

            try {
                ReservationDTO reservation2 = reservationService.makeReservation(
                        client2, room2, checkinDate2, checkoutDate2);
                System.out.println("Reservation 2: " + reservation2);
            } catch (RoomNotFoundException ex) {
                System.out.println("Error while making Reservation 2: " + ex.getMessage());
            }

            boolean isRoomAvailable = reservationService.isRoomAvailable(room1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(4));
            System.out.println("Is Room 1 Available: " + isRoomAvailable);

            List<ReservationDTO> reservationsByClient = reservationService.getReservationsByClient(client1);
            System.out.println("Reservations by Client 1: " + reservationsByClient);

            List<ReservationDTO> reservationsByRoom = reservationService.getReservationsByRoom(room2);
            System.out.println("Reservations by room 2: " + reservationsByRoom);
        };
    }

}