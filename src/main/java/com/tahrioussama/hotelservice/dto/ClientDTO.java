package com.tahrioussama.hotelservice.dto;


import lombok.Data;


@Data
public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
