package com.ksr.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {
    
    public List<String> getAvailableSeats(int showtimeId) {
        List<String> availableSeats = new ArrayList<>();
        String sql = "SELECT seat_number FROM seats WHERE showtime_id = ? AND is_booked = false";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, showtimeId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                availableSeats.add(rs.getString("seat_number"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return availableSeats;
    }
    
    public boolean bookSeats(int showtimeId, List<String> seatNumbers) {
        String sql = "UPDATE seats SET is_booked = true WHERE showtime_id = ? AND seat_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (String seatNumber : seatNumbers) {
                pstmt.setInt(1, showtimeId);
                pstmt.setString(2, seatNumber);
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            return results.length == seatNumbers.size();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}