package com.ksr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import com.ksr.dao.*;
import com.ksr.model.*;
import java.sql.SQLException;
import java.util.*;
import java.util.Arrays;

@SpringBootApplication
@RestController
@CrossOrigin(origins = {"http://localhost:8082", "http://localhost:8085", "*"})
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
                    "email", user.getEmail(),
                    "phoneNumber", user.getPhoneNumber()
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

    @PostMapping("/api/signup")
    public Map<String, Object> signup(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDAO userDAO = new UserDAO();
            
            // Check if email already exists
            if (userDAO.emailExists(userData.get("email"))) {
                response.put("status", "error");
                response.put("message", "Email already exists");
                return response;
            }
            
            // Create new user
            User user = new User(
                userData.get("fullName"),
                userData.get("email"),
                userData.get("password"),
                userData.get("phone")
            );
            
            boolean created = userDAO.createUser(user);
            
            if (created) {
                response.put("status", "success");
                response.put("message", "Account created successfully!");
            } else {
                response.put("status", "error");
                response.put("message", "Failed to create account");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Signup failed: " + e.getMessage());
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
            SeatDAO seatDAO = new SeatDAO();
            BookingDAO bookingDAO = new BookingDAO();
            
            int userId = (Integer) bookingData.get("userId");
            int showtimeId = (Integer) bookingData.get("showtimeId");
            @SuppressWarnings("unchecked")
            List<String> seatNumbersList = (List<String>) bookingData.get("seatNumbers");
            String seatNumbers = String.join(",", seatNumbersList); // Convert List to comma-separated String
            double totalAmount = ((Number) bookingData.get("totalAmount")).doubleValue();
            
            // Check if any seats are already booked
            Map<String, Object> currentSeatStatus = seatDAO.getSeatStatus(showtimeId);
            @SuppressWarnings("unchecked")
            List<String> occupiedSeats = (List<String>) currentSeatStatus.get("occupied");
            
            for (String seat : seatNumbersList) {
                if (occupiedSeats.contains(seat)) {
                    response.put("status", "error");
                    response.put("message", "Seat " + seat + " is already booked");
                    return response;
                }
            }
            
            // First, try to book the seats
            boolean seatsBooked = seatDAO.bookSeats(showtimeId, seatNumbersList);
            
            if (seatsBooked) {
                // Create booking record
                boolean bookingCreated = bookingDAO.createBooking(userId, showtimeId, seatNumbers, totalAmount);
                
                if (bookingCreated) {
                    response.put("status", "success");
                    response.put("message", "Booking confirmed!");
                    response.put("bookingId", "KSR" + System.currentTimeMillis());
                } else {
                    // If booking creation failed, release the seats
                    seatDAO.releaseSeats(showtimeId, seatNumbersList);
                    response.put("status", "error");
                    response.put("message", "Booking failed - could not create booking record");
                }
            } else {
                response.put("status", "error");
                response.put("message", "Booking failed - seats are unavailable");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Booking failed: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/seats")
    public Map<String, Object> getSeatStatus(@RequestParam int showtimeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SeatDAO seatDAO = new SeatDAO();
            Map<String, Object> seatStatus = seatDAO.getSeatStatus(showtimeId);
            
            response.put("available", seatStatus.get("available"));
            response.put("occupied", seatStatus.get("occupied"));
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch seats: " + e.getMessage());
            response.put("available", new ArrayList<>());
            response.put("occupied", new ArrayList<>());
        }
        return response;
    }

    @PostMapping("/api/booking")
    public Map<String, Object> processBooking(@RequestBody Map<String, Object> bookingRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Extract parameters from JSON request
            int showtimeId = ((Number) bookingRequest.get("showtimeId")).intValue();
            String seatNumbers = String.join(",", (List<String>) bookingRequest.get("seatNumbers"));
            double totalAmount = ((Number) bookingRequest.get("totalPrice")).doubleValue();
            int userId = ((Number) bookingRequest.get("userId")).intValue();
            
            SeatDAO seatDAO = new SeatDAO();
            BookingDAO bookingDAO = new BookingDAO();
            UserDAO userDAO = new UserDAO();
            
            // Convert comma-separated string to list
            List<String> seatList = Arrays.asList(seatNumbers.split(","));
            
            // Check if any seats are already booked
            Map<String, Object> currentSeatStatus = seatDAO.getSeatStatus(showtimeId);
            @SuppressWarnings("unchecked")
            List<String> occupiedSeats = (List<String>) currentSeatStatus.get("occupied");
            
            for (String seat : seatList) {
                if (occupiedSeats.contains(seat)) {
                    response.put("success", false);
                    response.put("error", "seat_already_booked");
                    response.put("message", "Seat " + seat + " is already booked");
                    return response;
                }
            }
            
            // Get user details from database
            User user = null;
            // For now, we'll use a simple approach to get user by ID
            // In a real application, you'd have user session management
            
            // First, try to book the seats
            boolean seatsBooked = seatDAO.bookSeats(showtimeId, seatList);
            
            if (seatsBooked) {
                // Create booking record
                boolean bookingCreated = bookingDAO.createBooking(userId, showtimeId, seatNumbers, totalAmount);
                
                if (bookingCreated) {
                    response.put("success", true);
                    response.put("message", "Booking successful");
                    response.put("bookingDetails", Map.of(
                        "seats", seatNumbers,
                        "totalAmount", totalAmount,
                        "bookingTime", new java.util.Date().toString()
                    ));
                } else {
                    // If booking creation failed, release the seats
                    seatDAO.releaseSeats(showtimeId, seatList);
                    response.put("success", false);
                    response.put("error", "booking_failed");
                }
            } else {
                response.put("success", false);
                response.put("error", "seats_unavailable");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "booking_error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/api/booking/{bookingId}")
    public Map<String, Object> cancelBooking(@PathVariable int bookingId) {
        Map<String, Object> response = new HashMap<>();
        try {
            BookingHistoryDAO bookingHistoryDAO = new BookingHistoryDAO();
            SeatDAO seatDAO = new SeatDAO();
            
            // First get booking details to release seats
            BookingHistory booking = bookingHistoryDAO.getBookingById(bookingId);
            if (booking == null) {
                response.put("success", false);
                response.put("message", "Booking not found");
                return response;
            }
            
            // Check if booking is already cancelled
            if ("CANCELLED".equals(booking.getStatus())) {
                response.put("success", false);
                response.put("message", "Booking is already cancelled");
                return response;
            }
            
            // Cancel the booking (update status to CANCELLED)
            boolean cancelled = bookingHistoryDAO.cancelBooking(bookingId, booking.getUserId());
            
            if (cancelled) {
                // Release the seats
                List<String> seatNumbers = Arrays.asList(booking.getSeatNumbers().split(","));
                boolean seatsReleased = seatDAO.releaseSeats(booking.getShowtimeId(), seatNumbers);
                
                if (seatsReleased) {
                    response.put("success", true);
                    response.put("message", "Booking cancelled successfully");
                } else {
                    response.put("success", true);
                    response.put("message", "Booking cancelled but there was an issue releasing seats");
                }
            } else {
                response.put("success", false);
                response.put("message", "Failed to cancel booking");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping(value = "/api/booking", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> bookingForm(
            @RequestParam("userId") int userId,
            @RequestParam("showtimeId") int showtimeId,
            @RequestParam("seatNumbers") String seatNumbers,
            @RequestParam("totalAmount") double totalAmount,
            @RequestParam(value = "movieName", required = false) String movieName,
            @RequestParam(value = "showTime", required = false) String showTime) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            SeatDAO seatDAO = new SeatDAO();
            BookingDAO bookingDAO = new BookingDAO();
            
            // Convert comma-separated string to list
            List<String> seatList = Arrays.asList(seatNumbers.split(","));
            
            // Check if any seats are already booked
            Map<String, Object> currentSeatStatus = seatDAO.getSeatStatus(showtimeId);
            @SuppressWarnings("unchecked")
            List<String> occupiedSeats = (List<String>) currentSeatStatus.get("occupied");
            
            for (String seat : seatList) {
                if (occupiedSeats.contains(seat.trim())) {
                    response.put("success", false);
                    response.put("error", "seat_already_booked");
                    response.put("message", "Seat " + seat + " is already booked");
                    return response;
                }
            }
            
            // First, try to book the seats
            boolean seatsBooked = seatDAO.bookSeats(showtimeId, seatList);
            
            if (seatsBooked) {
                // Create booking record
                boolean bookingCreated = bookingDAO.createBooking(userId, showtimeId, seatNumbers, totalAmount);
                
                if (bookingCreated) {
                    response.put("success", true);
                    response.put("message", "Booking successful");
                    response.put("bookingDetails", Map.of(
                        "seats", seatNumbers,
                        "totalAmount", totalAmount,
                        "bookingTime", new java.util.Date().toString()
                    ));
                } else {
                    // If booking creation failed, release the seats
                    seatDAO.releaseSeats(showtimeId, seatList);
                    response.put("success", false);
                    response.put("error", "booking_failed");
                    response.put("message", "Failed to create booking record");
                }
            } else {
                response.put("success", false);
                response.put("error", "seats_unavailable");
                response.put("message", "Selected seats are no longer available");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "booking_error");
            response.put("message", "Error processing booking: " + e.getMessage());
        }
        return response;
    }
}