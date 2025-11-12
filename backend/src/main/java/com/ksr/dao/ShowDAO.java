package com.ksr.dao;

import com.ksr.model.Showtime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowDAO {
    
    public List<Showtime> getShowtimesByMovieId(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT * FROM showtimes WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setId(rs.getInt("id"));
                showtime.setMovieId(rs.getInt("movie_id"));
                showtime.setTheaterName(rs.getString("theater_name"));
                showtime.setPrice(rs.getDouble("price"));
                showtime.setTotalSeats(rs.getInt("total_seats"));
                showtime.setAvailableSeats(rs.getInt("available_seats"));
                
                Timestamp timestamp = rs.getTimestamp("show_time");
                if (timestamp != null) {
                    showtime.setShowTime(timestamp.toLocalDateTime());
                }
                
                showtimes.add(showtime);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return showtimes;
    }
}