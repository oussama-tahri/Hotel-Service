package com.tahrioussama.hotelservice.entities;

import com.tahrioussama.hotelservice.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Check-in date must not be null")
    private LocalDate checkIN;
    @NotNull(message = "Check-out date must not be null")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOUT;

//    @Column(nullable = false)
//    private double totalCost;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

}
