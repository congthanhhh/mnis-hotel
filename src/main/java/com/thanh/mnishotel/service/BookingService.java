package com.thanh.mnishotel.service;

import com.thanh.mnishotel.model.BookedRoom;

import java.util.List;

public interface BookingService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    void cancelBooking(Long bookingId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> getAllBookings();
}
