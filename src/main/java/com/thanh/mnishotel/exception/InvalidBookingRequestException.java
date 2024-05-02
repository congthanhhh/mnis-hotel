package com.thanh.mnishotel.exception;

public class InvalidBookingRequestException extends RuntimeException{
    public InvalidBookingRequestException(String message) {
        super(message);
    }
}
