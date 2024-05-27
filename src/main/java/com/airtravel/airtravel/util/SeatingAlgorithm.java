package com.airtravel.airtravel.util;

import com.airtravel.airtravel.model.Seat;

public class SeatingAlgorithm {

    public void preventSingleSeatScatter(Aircraft aircraft) {
        Seat[][] seats = aircraft.getSeats();
        int rows = seats.length;
        int cols = seats[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!seats[i][j].isAvailable() && !seats[i][j].isLocked()) {
                    if (leavesSingleSeat(seats, i, j, rows, cols)) {
                        seats[i][j].setLocked(true);
                    }
                }
            }
        }
    }

    private boolean leavesSingleSeat(Seat[][] seats, int row, int col, int rows, int cols) {
        // Check adjacent seats
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        for (int k = 0; k < 4; k++) {
            int newRow = row + dRow[k];
            int newCol = col + dCol[k];

            if (isInBounds(newRow, newCol, rows, cols) && !seats[newRow][newCol].isAvailable() && !seats[newRow][newCol].isLocked()) {
                if (countAdjacentAvailableSeats(seats, newRow, newCol, rows, cols) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countAdjacentAvailableSeats(Seat[][] seats, int row, int col, int rows, int cols) {
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};
        int count = 0;

        for (int k = 0; k < 4; k++) {
            int newRow = row + dRow[k];
            int newCol = col + dCol[k];

            if (isInBounds(newRow, newCol, rows, cols) && !seats[newRow][newCol].isAvailable() && !seats[newRow][newCol].isLocked()) {
                count++;
            }
        }
        return count;
    }

    private boolean isInBounds(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}

