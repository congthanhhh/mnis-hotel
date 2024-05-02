package com.thanh.mnishotel.controller;

import com.thanh.mnishotel.dto.BookingDto;
import com.thanh.mnishotel.dto.RoomDto;
import com.thanh.mnishotel.exception.PhotoRetrievalException;
import com.thanh.mnishotel.exception.ResourceNotFoundException;
import com.thanh.mnishotel.model.BookedRoom;
import com.thanh.mnishotel.model.Room;
import com.thanh.mnishotel.service.BookingService;
import com.thanh.mnishotel.service.RoomService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin("*") // se xoa sau khi config cors
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;


    @Autowired
    public RoomController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomDto> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomDto dto = new RoomDto(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomDto>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomDto> listRoomDto = new ArrayList<>();
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomDto roomDto = getRoomDto(room);
                roomDto.setPhoto(base64Photo);
                listRoomDto.add(roomDto);
            }
        }
        return ResponseEntity.ok(listRoomDto);
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDto>> getAvailableRooms(@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                           @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                                           @RequestParam("roomType") String roomType) throws SQLException {
        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomDto> roomDtoList = new ArrayList<>();
        for (Room room : availableRooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String photoBase64 = Base64.encodeBase64String(photoBytes);
                RoomDto roomDto = getRoomDto(room);
                roomDto.setPhoto(photoBase64);
                roomDtoList.add(roomDto);
            }
        }
        if (roomDtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(roomDtoList);
        }

    }

    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable("roomId") Long roomId,
                                              @RequestParam(required = false) String roomType,
                                              @RequestParam(required = false) BigDecimal roomPrice,
                                              @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {

        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;
        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);
        RoomDto roomDto = getRoomDto(theRoom);
        return ResponseEntity.ok(roomDto);

    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomDto>> getRoomById(@PathVariable("roomId") Long roomId) {
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            //.map is a function of Optional
            RoomDto roomDto = getRoomDto(room);
            return ResponseEntity.ok(Optional.of(roomDto));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    private RoomDto getRoomDto(Room room) {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingDto> bookingInfo = bookings.stream()
                .map(booking -> new BookingDto(
                        booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getBookingConfirmationCode()
                )).toList();

        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new RoomDto(
                room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.isBooked(),
                photoBytes, bookingInfo);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

}
