package com.ksr.dao;

import java.sql.*;
import java.time.LocalDateTime;

public class BookingDAO {
    
    public boolean createBooking(int userId, int showtimeId, String seatNumbers, double totalAmount) {
        String sql = "INSERT INTO bookings (user_id, showtime_id, seat_numbers, total_amount, booking_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, showtimeId);
            pstmt.setString(3, seatNumbers);
            pstmt.setDouble(4, totalAmount);
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(6, "CONFIRMED");
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}