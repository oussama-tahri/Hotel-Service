package com.tahrioussama.hotelservice.exceptions;

import org.springframework.dao.DataAccessException;

public class ClientSaveException extends Exception {
    public ClientSaveException(String message, DataAccessException ex) {
        super(message);}
}
