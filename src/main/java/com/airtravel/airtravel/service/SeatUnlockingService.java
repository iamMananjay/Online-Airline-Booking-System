package com.airtravel.airtravel.service;

import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeatUnlockingService {
    @Autowired
    private SeatRepository seatRepository;

    @Scheduled(fixedRate = 60000) // Run every minute
    public void unlockExpiredSeats() {
        List<Seat> seats = seatRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Seat seat : seats) {
            if (seat.isLocked() && seat.getLockedAt().isBefore(now.minusMinutes(10))) {
                seat.setLocked(false);
                seat.setLockedAt(null);
                seatRepository.save(seat);
            }
        }
    }
}

