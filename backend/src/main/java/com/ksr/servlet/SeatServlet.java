package com.ksr.servlet;

import com.ksr.dao.SeatDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/seats")
public class SeatServlet extends HttpServlet {
    
    private SeatDAO seatDAO = new SeatDAO();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String showtimeIdParam = request.getParameter("showtimeId");
        if (showtimeIdParam != null) {
            int showtimeId = Integer.parseInt(showtimeIdParam);
            List<String> availableSeats = seatDAO.getAvailableSeats(showtimeId);
            String jsonResponse = objectMapper.writeValueAsString(availableSeats);
            response.getWriter().write(jsonResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Showtime ID is required\"}");
        }
    }
}