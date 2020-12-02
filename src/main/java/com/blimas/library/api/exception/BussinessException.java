package com.blimas.library.api.exception;

public class BussinessException extends RuntimeException {
    public BussinessException(String error) {
        super(error);
    }
}
