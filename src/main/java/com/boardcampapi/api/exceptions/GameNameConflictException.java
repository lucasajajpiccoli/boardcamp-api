package com.boardcampapi.api.exceptions;

public class GameNameConflictException extends RuntimeException {
    public GameNameConflictException(String message) {
        super(message);
    }
}
