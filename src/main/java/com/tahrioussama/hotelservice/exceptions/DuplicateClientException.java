package com.tahrioussama.hotelservice.exceptions;

public class DuplicateClientException extends Exception {
    public DuplicateClientException(String message) {
        super(message);
    }
}