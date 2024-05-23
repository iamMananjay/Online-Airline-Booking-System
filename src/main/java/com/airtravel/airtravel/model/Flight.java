package com.airtravel.airtravel.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    @Column(unique = true)
    private String flightNumber;

    private String departureAirport;
    private String destinationAirport;

    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;

    private double ticketPrice;
    private int totalSeats;


    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Booking> bookings;


    // Constructors, getters, and setters

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }



    public LocalDateTime getArrivalDatetime() {
        return arrivalDatetime;
    }

    public void setArrivalDatetime(LocalDateTime arrivalDatetime) {
        this.arrivalDatetime = arrivalDatetime;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<Booking> getBookings() {
        return bookings;
    }


    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Transient
    private String formattedDepartureDate; // This field will hold the formatted departure date

    // Getters and setters...

    public LocalDateTime getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(LocalDateTime departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public String getFormattedDepartureDate() {
        return formattedDepartureDate;
    }

    public void setFormattedDepartureDate(String formattedDepartureDate) {
        this.formattedDepartureDate = formattedDepartureDate;
    }
}
