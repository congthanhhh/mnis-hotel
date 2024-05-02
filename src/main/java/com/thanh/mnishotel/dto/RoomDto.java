package com.thanh.mnishotel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked;
    private String photo;
    private List<BookingDto> bookings;

    public RoomDto(Long id, String roomType, BigDecimal roomPrice) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    public RoomDto(Long id, String roomType, BigDecimal roomPrice, boolean isBooked, byte[] photoBytes, List<BookingDto> bookings) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
        this.bookings = bookings;
    }
}
