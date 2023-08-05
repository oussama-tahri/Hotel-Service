package com.tahrioussama.hotelservice.dto;

import com.tahrioussama.hotelservice.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private RoomType roomType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomDTO)) return false;
        RoomDTO roomDTO = (RoomDTO) o;
        return Objects.equals(id, roomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
