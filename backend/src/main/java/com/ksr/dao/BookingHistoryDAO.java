package com.ksr.dao;

import com.ksr.model.BookingHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingHistoryDAO {
    
    public List<BookingHistory> getBookingHistoryByUserId(int userId) {
        List<BookingHistory> bookingHistory = new ArrayList<>();
        String sql = "SELECT b.id, b.user_id, b.showtime_id, b.seat_numbers, b.total_amount, " +
                    "b.booking_time, b.status, " +
                    "m.title as movie_title, s.theater_name, s.show_time, " +
                    "u.username, u.email " +
                    "FROM bookings b " +
                    "JOIN showtimes s ON b.showtime_id = s.id " +
                    "JOIN movies m ON s.movie_id = m.id " +
                    "JOIN users u ON b.user_id = u.id " +
                    "WHERE b.user_id = ? " +
                    "ORDER BY b.booking_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BookingHistory booking = new BookingHistory();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setShowtimeId(rs.getInt("showtime_id"));
                booking.setSeatNumbers(rs.getString("seat_numbers"));
                booking.setTotalAmount(rs.getBigDecimal("total_amount"));
                booking.setBookingTime(rs.getTimestamp("booking_time"));
                booking.setStatus(rs.getString("status"));
                booking.setMovieTitle(rs.getString("movie_title"));
                booking.setTheaterName(rs.getString("theater_name"));
                booking.setShowTime(rs.getTimestamp("show_time"));
                booking.setUserName(rs.getString("username"));
                booking.setUserEmail(rs.getString("email"));
                
                bookingHistory.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching booking history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bookingHistory;
    }
    
    public BookingHistory getBookingById(int bookingId) {
        String sql = "SELECT b.id, b.user_id, b.showtime_id, b.seat_numbers, b.total_amount, " +
                    "b.booking_time, b.status, " +
                    "m.title as movie_title, s.theater_name, s.show_time, " +
                    "u.username, u.email " +
                    "FROM bookings b " +
                    "JOIN showtimes s ON b.showtime_id = s.id " +
                    "JOIN movies m ON s.movie_id = m.id " +
                    "JOIN users u ON b.user_id = u.id " +
                    "WHERE b.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                BookingHistory booking = new BookingHistory();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setShowtimeId(rs.getInt("showtime_id"));
                booking.setSeatNumbers(rs.getString("seat_numbers"));
                booking.setTotalAmount(rs.getBigDecimal("total_amount"));
                booking.setBookingTime(rs.getTimestamp("booking_time"));
                booking.setStatus(rs.getString("status"));
                booking.setMovieTitle(rs.getString("movie_title"));
                booking.setTheaterName(rs.getString("theater_name"));
                booking.setShowTime(rs.getTimestamp("show_time"));
                booking.setUserName(rs.getString("username"));
                booking.setUserEmail(rs.getString("email"));
                
                return booking;
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching booking: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean cancelBooking(int bookingId, int userId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}