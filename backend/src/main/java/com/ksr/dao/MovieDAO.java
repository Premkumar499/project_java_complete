package com.ksr.dao;

import com.ksr.model.Movie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setLanguage(rs.getString("language"));
                movie.setRating(rs.getString("rating"));
                // Handle date conversion
                Date releaseDate = rs.getDate("release_date");
                if (releaseDate != null) {
                    movie.setReleaseDate(releaseDate.toLocalDate());
                }
                movies.add(movie);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return movies;
    }
    
    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setLanguage(rs.getString("language"));
                movie.setRating(rs.getString("rating"));
                Date releaseDate = rs.getDate("release_date");
                if (releaseDate != null) {
                    movie.setReleaseDate(releaseDate.toLocalDate());
                }
                return movie;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}