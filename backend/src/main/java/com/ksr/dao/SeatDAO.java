package com.ksr.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public Map<String, Object> getSeatStatus(int showtimeId) {
        Map<String, Object> seatStatus = new HashMap<>();
        List<String> availableSeats = new ArrayList<>();
        List<String> occupiedSeats = new ArrayList<>();
        
        String sql = "SELECT seat_number, is_booked FROM seats WHERE showtime_id = ? ORDER BY seat_number";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, showtimeId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String seatNumber = rs.getString("seat_number");
                boolean isBooked = rs.getBoolean("is_booked");
                
                if (isBooked) {
                    occupiedSeats.add(seatNumber);
                } else {
                    availableSeats.add(seatNumber);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        seatStatus.put("available", availableSeats);
        seatStatus.put("occupied", occupiedSeats);
        return seatStatus;
    }
    
    public boolean bookSeats(int showtimeId, List<String> seatNumbers) {
        String checkSql = "SELECT COUNT(*) FROM seats WHERE showtime_id = ? AND seat_number = ? AND is_booked = false";
        String updateSql = "UPDATE seats SET is_booked = true WHERE showtime_id = ? AND seat_number = ? AND is_booked = false";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            // First, check if all seats are available
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                for (String seatNumber : seatNumbers) {
                    checkStmt.setInt(1, showtimeId);
                    checkStmt.setString(2, seatNumber);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    if (count == 0) {
                        // Seat not available
                        conn.rollback();
                        conn.setAutoCommit(true);
                        return false;
                    }
                }
            }
            
            // All seats are available, now book them
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                int successfulUpdates = 0;
                for (String seatNumber : seatNumbers) {
                    updateStmt.setInt(1, showtimeId);
                    updateStmt.setString(2, seatNumber);
                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        successfulUpdates++;
                    }
                }
                
                // Check if all seats were successfully booked
                if (successfulUpdates == seatNumbers.size()) {
                    conn.commit();
                    conn.setAutoCommit(true);
                    return true;
                } else {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return false;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean releaseSeats(int showtimeId, List<String> seatNumbers) {
        String sql = "UPDATE seats SET is_booked = false WHERE showtime_id = ? AND seat_number = ?";
        
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