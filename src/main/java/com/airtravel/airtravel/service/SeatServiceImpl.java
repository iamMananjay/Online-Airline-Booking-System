package com.airtravel.airtravel.service;





import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.repository.FlightRepository;
import com.airtravel.airtravel.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private FlightRepository flightRepository;


    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

//    @Override
//    public Seat updateSeatAvailability(String flightNumber, String seatNumber, boolean available) {
//        Optional<Seat> optionalSeat = seatRepository.findByFlight_FlightNumberAndSeatNumber(flightNumber, seatNumber);
//        if (optionalSeat.isPresent()) {
//            Seat seat = optionalSeat.get();
//            seat.setAvailable(available);
//            return seatRepository.save(seat);
//        } else {
//            // Handle the case where the seat with the given flight number and seat number is not found
//            // You can throw an exception or return null, depending on your requirement
//            return null;
//        }
//    }




    @Override
    public Seat lockSeat(String seatNumber) {
        Optional<Seat> optionalSeat = seatRepository.findBySeatNumber(seatNumber);
        if (optionalSeat.isPresent()) {
            Seat seat = optionalSeat.get();
            seat.setLocked(true);
            seat.setLockedAt(LocalDateTime.now());
            return seatRepository.save(seat);
        }
        return null;
    }

    @Override
    public Seat unlockSeat(String seatNumber) {
        Optional<Seat> optionalSeat = seatRepository.findBySeatNumber(seatNumber);
        if (optionalSeat.isPresent()) {
            Seat seat = optionalSeat.get();
            seat.setLocked(false);
            seat.setLockedAt(null);
            return seatRepository.save(seat);
        }
        return null;
    }

    @Override
    public Map<String, Boolean> convertSeatsToMap(List<Seat> seats) {
        Map<String, Boolean> seatMap = new HashMap<>();
        for (Seat seat : seats) {
            seatMap.put(seat.getSeatNumber(), seat.isAvailable());
        }
        return seatMap;
    }

    @Override
    public List<Seat> getSeatsByFlightId(Long flightId) {
        return seatRepository.findByFlightFlightId(flightId);
    }

    @Override
    public Seat getSeatByNumberAndFlight(String seatNumber, Flight flight) {
        return seatRepository.findBySeatNumberAndFlight(seatNumber, flight);
    }

    @Override
    public void updateSeatAvailability(String flightNumber, String seatNumber, boolean availability) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        Seat seat = seatRepository.findBySeatNumberAndFlight(seatNumber, flight);
        seat.setAvailable(availability);
        seatRepository.save(seat);
    }

}
