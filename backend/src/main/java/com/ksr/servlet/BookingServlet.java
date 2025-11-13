package com.ksr.servlet;

import com.ksr.dao.BookingDAO;
import com.ksr.dao.BookingHistoryDAO;
import com.ksr.dao.SeatDAO;
import com.ksr.model.BookingHistory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/booking")
public class BookingServlet extends HttpServlet {
    
    private BookingDAO bookingDAO = new BookingDAO();
    private SeatDAO seatDAO = new SeatDAO();
    private BookingHistoryDAO bookingHistoryDAO = new BookingHistoryDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        
        if ("history".equals(action)) {
            // Handle booking history request
            try {
                String userIdParam = request.getParameter("userId");
                
                if (userIdParam == null || userIdParam.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"User ID is required\"}");
                    return;
                }
                
                int userId = Integer.parseInt(userIdParam);
                List<BookingHistory> bookingHistory = bookingHistoryDAO.getBookingHistoryByUserId(userId);
                
                // Build JSON response manually
                StringBuilder json = new StringBuilder();
                json.append("{\"success\": true, \"data\": [");
                
                for (int i = 0; i < bookingHistory.size(); i++) {
                    BookingHistory booking = bookingHistory.get(i);
                    if (i > 0) json.append(",");
                    
                    String movieTitle = booking.getMovieTitle() != null ? booking.getMovieTitle() : "";
                    String theaterName = booking.getTheaterName() != null ? booking.getTheaterName() : "";
                    String seatNumbers = booking.getSeatNumbers() != null ? booking.getSeatNumbers() : "";
                    String status = booking.getStatus() != null ? booking.getStatus() : "";
                    String userName = booking.getUserName() != null ? booking.getUserName() : "";
                    String userEmail = booking.getUserEmail() != null ? booking.getUserEmail() : "";
                    
                    json.append("{")
                        .append("\"id\": ").append(booking.getId()).append(",")
                        .append("\"userId\": ").append(booking.getUserId()).append(",")
                        .append("\"showtimeId\": ").append(booking.getShowtimeId()).append(",")
                        .append("\"seatNumbers\": \"").append(seatNumbers).append("\",")
                        .append("\"totalAmount\": ").append(booking.getTotalAmount()).append(",")
                        .append("\"bookingTime\": \"").append(booking.getBookingTime()).append("\",")
                        .append("\"status\": \"").append(status).append("\",")
                        .append("\"movieTitle\": \"").append(movieTitle).append("\",")
                        .append("\"theaterName\": \"").append(theaterName).append("\",")
                        .append("\"showTime\": \"").append(booking.getShowTime()).append("\",")
                        .append("\"userName\": \"").append(userName).append("\",")
                        .append("\"userEmail\": \"").append(userEmail).append("\"")
                        .append("}");
                }
                
                json.append("]}");
                out.print(json.toString());
                
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Invalid user ID format\"}");
                
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error fetching booking history\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid action\"}");
        }
        
        out.close();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Get userId from request parameter (from frontend)
            String userIdParam = request.getParameter("userId");
            int userId = userIdParam != null ? Integer.parseInt(userIdParam) : 1; // Default to 1 if not provided
            
            String showtimeIdParam = request.getParameter("showtimeId");
            String seatNumbersParam = request.getParameter("seatNumbers");
            String totalAmountParam = request.getParameter("totalAmount");
            String movieNameParam = request.getParameter("movieName");
            String showTimeParam = request.getParameter("showTime");
            
            System.out.println("Received booking request:");
            System.out.println("UserId: " + userId);
            System.out.println("ShowtimeId: " + showtimeIdParam);
            System.out.println("SeatNumbers: " + seatNumbersParam);
            System.out.println("TotalAmount: " + totalAmountParam);
            System.out.println("MovieName: " + movieNameParam);
            System.out.println("ShowTime: " + showTimeParam);
            
            if (showtimeIdParam != null && seatNumbersParam != null && totalAmountParam != null) {
                int showtimeId = Integer.parseInt(showtimeIdParam);
                double totalAmount = Double.parseDouble(totalAmountParam);
                List<String> seatNumbers = Arrays.asList(seatNumbersParam.split(","));
                
                // Book seats
                if (seatDAO.bookSeats(showtimeId, seatNumbers)) {
                    // Create booking record
                    if (bookingDAO.createBooking(userId, showtimeId, seatNumbersParam, totalAmount)) {
                        out.write("{\"success\": true, \"message\": \"Booking successful\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.write("{\"success\": false, \"error\": \"booking_failed\", \"message\": \"Failed to create booking record\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.write("{\"success\": false, \"error\": \"seats_unavailable\", \"message\": \"Selected seats are no longer available\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"error\": \"invalid_data\", \"message\": \"Missing required booking parameters\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"success\": false, \"error\": \"invalid_format\", \"message\": \"Invalid number format in request\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\": false, \"error\": \"server_error\", \"message\": \"Internal server error occurred\"}");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}