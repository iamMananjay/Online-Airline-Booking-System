package com.airtravel.airtravel.service;



import com.airtravel.airtravel.model.Booking;

import java.util.List;

public interface BookingService {
    List<Booking> getAllBookings();
    void saveBooking(Booking booking);
    void deleteBooking(Long id);
}

