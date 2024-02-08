package com.boardcampapi.api.exceptions;

public class UnprocessableRentalException extends RuntimeException{
    public UnprocessableRentalException(String message) {
        super(message);
    }
}
