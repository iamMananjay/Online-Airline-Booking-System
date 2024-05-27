package com.airtravel.airtravel.util;

import com.airtravel.airtravel.model.Seat;

public class Aircraft {
    private Seat[][] seats;

    public Aircraft(int rows, int cols) {
        seats = new Seat[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                seats[i][j] = new Seat();
            }
        }
    }

    public Seat[][] getSeats() {
        return seats;
    }

    public void setSeats(Seat[][] seats) {
        this.seats = seats;
    }
// Getters and Setters
}

