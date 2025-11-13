package com.ksr.servlet;

import com.ksr.dao.BookingHistoryDAO;
import com.ksr.model.BookingHistory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/user/booking-history")
public class UserBookingHistoryServlet extends HttpServlet {
    
    private BookingHistoryDAO bookingHistoryDAO;
    
    @Override
    public void init() throws ServletException {
        bookingHistoryDAO = new BookingHistoryDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        
        PrintWriter out = response.getWriter();
        
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
                
                json.append("{")
                    .append("\"id\": ").append(booking.getId()).append(",")
                    .append("\"userId\": ").append(booking.getUserId()).append(",")
                    .append("\"showtimeId\": ").append(booking.getShowtimeId()).append(",")
                    .append("\"seatNumbers\": \"").append(booking.getSeatNumbers() != null ? booking.getSeatNumbers() : "").append("\",")
                    .append("\"totalAmount\": ").append(booking.getTotalAmount()).append(",")
                    .append("\"bookingTime\": \"").append(booking.getBookingTime()).append("\",")
                    .append("\"status\": \"").append(booking.getStatus() != null ? booking.getStatus() : "").append("\",")
                    .append("\"movieTitle\": \"").append(booking.getMovieTitle() != null ? booking.getMovieTitle() : "").append("\",")
                    .append("\"theaterName\": \"").append(booking.getTheaterName() != null ? booking.getTheaterName() : "").append("\",")
                    .append("\"showTime\": \"").append(booking.getShowTime()).append("\",")
                    .append("\"userName\": \"").append(booking.getUserName() != null ? booking.getUserName() : "").append("\",")
                    .append("\"userEmail\": \"").append(booking.getUserEmail() != null ? booking.getUserEmail() : "").append("\"")
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
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}