package com.ksr.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BookingHistory {
    private int id;
    private int userId;
    private int showtimeId;
    private String seatNumbers;
    private BigDecimal totalAmount;
    private Timestamp bookingTime;
    private String status;
    
    // Additional fields from joins
    private String movieTitle;
    private String theaterName;
    private Timestamp showTime;
    private String userName;
    private String userEmail;
    
    public BookingHistory() {}
    
    public BookingHistory(int id, int userId, int showtimeId, String seatNumbers, 
                         BigDecimal totalAmount, Timestamp bookingTime, String status) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatNumbers = seatNumbers;
        this.totalAmount = totalAmount;
        this.bookingTime = bookingTime;
        this.status = status;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getShowtimeId() {
        return showtimeId;
    }
    
    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }
    
    public String getSeatNumbers() {
        return seatNumbers;
    }
    
    public void setSeatNumbers(String seatNumbers) {
        this.seatNumbers = seatNumbers;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Timestamp getBookingTime() {
        return bookingTime;
    }
    
    public void setBookingTime(Timestamp bookingTime) {
        this.bookingTime = bookingTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMovieTitle() {
        return movieTitle;
    }
    
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    
    public String getTheaterName() {
        return theaterName;
    }
    
    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }
    
    public Timestamp getShowTime() {
        return showTime;
    }
    
    public void setShowTime(Timestamp showTime) {
        this.showTime = showTime;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}