package com.ksr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import com.ksr.dao.*;
import com.ksr.model.*;
import java.sql.SQLException;
import java.util.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:8082")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "KSR Cinemas Backend API is running!";
    }

    @PostMapping("/api/register")
    public Map<String, Object> register(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDAO userDAO = new UserDAO();
            User user = new User();
            user.setUsername(userData.get("fullName"));
            user.setEmail(userData.get("email"));
            user.setPassword(userData.get("password"));
            user.setPhoneNumber(userData.get("phone"));
            
            boolean success = userDAO.createUser(user);
            if (success) {
                response.put("status", "success");
                response.put("message", "User registered successfully!");
            } else {
                response.put("status", "error");
                response.put("message", "Registration failed");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Registration failed: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmailAndPassword(
                credentials.get("email"), 
                credentials.get("password")
            );
            
            if (user != null) {
                response.put("status", "success");
                response.put("message", "Login successful!");
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail()
                ));
            } else {
                response.put("status", "error");
                response.put("message", "Invalid email or password");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Login failed: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/movies")
    public Map<String, Object> getMovies() {
        Map<String, Object> response = new HashMap<>();
        try {
            MovieDAO movieDAO = new MovieDAO();
            List<Movie> movies = movieDAO.getAllMovies();
            response.put("status", "success");
            response.put("movies", movies);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch movies: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/showtimes/{movieId}")
    public Map<String, Object> getShowtimes(@PathVariable int movieId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ShowDAO showDAO = new ShowDAO();
            List<Showtime> showtimes = showDAO.getShowtimesByMovieId(movieId);
            response.put("status", "success");
            response.put("showtimes", showtimes);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch showtimes: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/api/book")
    public Map<String, Object> bookSeats(@RequestBody Map<String, Object> bookingData) {
        Map<String, Object> response = new HashMap<>();
        try {
            BookingDAO bookingDAO = new BookingDAO();
            
            int userId = (Integer) bookingData.get("userId");
            int showtimeId = (Integer) bookingData.get("showtimeId");
            @SuppressWarnings("unchecked")
            List<String> seatNumbersList = (List<String>) bookingData.get("seatNumbers");
            String seatNumbers = String.join(",", seatNumbersList); // Convert List to comma-separated String
            double totalAmount = ((Number) bookingData.get("totalAmount")).doubleValue();
            
            boolean success = bookingDAO.createBooking(userId, showtimeId, seatNumbers, totalAmount);
            
            if (success) {
                response.put("status", "success");
                response.put("message", "Booking confirmed!");
                response.put("bookingId", "KSR" + System.currentTimeMillis());
            } else {
                response.put("status", "error");
                response.put("message", "Booking failed");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Booking failed: " + e.getMessage());
        }
        return response;
    }
}