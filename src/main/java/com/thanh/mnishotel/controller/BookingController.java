package com.thanh.mnishotel.controller;

import com.thanh.mnishotel.dto.BookingDto;
import com.thanh.mnishotel.dto.RoomDto;
import com.thanh.mnishotel.exception.InvalidBookingRequestException;
import com.thanh.mnishotel.exception.ResourceNotFoundException;
import com.thanh.mnishotel.model.BookedRoom;
import com.thanh.mnishotel.model.Room;
import com.thanh.mnishotel.service.BookingService;
import com.thanh.mnishotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    private final RoomService roomService;

    @Autowired
    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping("all-bookings")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            BookingDto bookingDto = getBookingDto(booking);
            bookingDtoList.add(bookingDto);
        }
        return ResponseEntity.ok(bookingDtoList);
    }

    @GetMapping("confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable("confirmationCode") String confirmationCode) {
        try {
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingDto bookingDto = getBookingDto(booking);
            return ResponseEntity.ok(bookingDto);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }


    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully Your booking confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    private BookingDto getBookingDto(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomDto roomDto = new RoomDto(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice());
        return new BookingDto(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),
                roomDto);
    }
}
