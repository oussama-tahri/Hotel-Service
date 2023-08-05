package com.tahrioussama.hotelservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReservationHistoryDTO {
    private String reservationId;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<ReservationDTO> reservationDTO;
}
