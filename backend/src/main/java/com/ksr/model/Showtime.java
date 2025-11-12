package com.ksr.model;

import java.time.LocalDateTime;

public class Showtime {
    private int id;
    private int movieId;
    private LocalDateTime showTime;
    private String theaterName;
    private double price;
    private int totalSeats;
    private int availableSeats;
    
    public Showtime() {}
    
    public Showtime(int movieId, LocalDateTime showTime, String theaterName, 
                   double price, int totalSeats, int availableSeats) {
        this.movieId = movieId;
        this.showTime = showTime;
        this.theaterName = theaterName;
        this.price = price;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getMovieId() {
        return movieId;
    }
    
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    
    public LocalDateTime getShowTime() {
        return showTime;
    }
    
    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }
    
    public String getTheaterName() {
        return theaterName;
    }
    
    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getTotalSeats() {
        return totalSeats;
    }
    
    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
    
    public int getAvailableSeats() {
        return availableSeats;
    }
    
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}