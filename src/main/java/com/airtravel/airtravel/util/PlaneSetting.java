package com.airtravel.airtravel.util;

import java.util.HashSet;
import java.util.Set;

public class PlaneSetting {

    public static void main(String[] args) {
        boolean[][] seating = new boolean[30][6]; // Example 30 rows and 6 columns seating arrangement

        // Populate the seating array as per the current booking status
        // For demonstration, let's assume some seats are already booked
        seating[2][1] = true;
        seating[2][2] = true;
        seating[2][4] = true;

        Set<String> seatsToBook = new HashSet<>();
        seatsToBook.add("3A");
        seatsToBook.add("3B");
        seatsToBook.add("3C");

        boolean canBook = canBookSeats(seating, seatsToBook);
        System.out.println("Can book seats: " + canBook);
    }

    public static boolean canBookSeats(boolean[][] seating, Set<String> seatsToBook) {
        // Convert seat names to indices
        Set<int[]> seatIndices = new HashSet<>();
        for (String seat : seatsToBook) {
            int row = Integer.parseInt(seat.substring(0, seat.length() - 1)) - 1;
            int col = seat.charAt(seat.length() - 1) - 'A';
            seatIndices.add(new int[]{row, col});
        }

        for (int[] seat : seatIndices) {
            int row = seat[0];
            int col = seat[1];

            if (!isValidBooking(seating, row, col, seatIndices)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidBooking(boolean[][] seating, int row, int col, Set<int[]> seatIndices) {
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];

            if (isInBounds(seating, newRow, newCol) && !seating[newRow][newCol] && !contains(seatIndices, newRow, newCol)) {
                // Check for isolated seat
                if (isIsolated(seating, newRow, newCol, seatIndices)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isIsolated(boolean[][] seating, int row, int col, Set<int[]> seatIndices) {
        int[] dx = {0, 0, -1, 1, -1, -1, 1, 1};
        int[] dy = {-1, 1, 0, 0, -1, 1, -1, 1};

        int count = 0;
        for (int i = 0; i < 8; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];

            if (isInBounds(seating, newRow, newCol) && (seating[newRow][newCol] || contains(seatIndices, newRow, newCol))) {
                count++;
            }
        }

        return count == 0;
    }

    private static boolean isInBounds(boolean[][] seating, int row, int col) {
        return row >= 0 && row < seating.length && col >= 0 && col < seating[0].length;
    }

    private static boolean contains(Set<int[]> seatIndices, int row, int col) {
        for (int[] seat : seatIndices) {
            if (seat[0] == row && seat[1] == col) {
                return true;
            }
        }
        return false;
    }
}