package com.boardcampapi.api.exceptions;

public class RentalNotFoundException extends RuntimeException{
    public RentalNotFoundException(String message) {
        super(message);
    }
}
