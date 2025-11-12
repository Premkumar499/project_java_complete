package com.ksr.servlet;

import com.ksr.dao.BookingDAO;
import com.ksr.dao.SeatDAO;
import com.ksr.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/booking")
public class BookingServlet extends HttpServlet {
    
    private BookingDAO bookingDAO = new BookingDAO();
    private SeatDAO seatDAO = new SeatDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.html");
            return;
        }
        
        String showtimeIdParam = request.getParameter("showtimeId");
        String seatNumbersParam = request.getParameter("seatNumbers");
        String totalAmountParam = request.getParameter("totalAmount");
        
        if (showtimeIdParam != null && seatNumbersParam != null && totalAmountParam != null) {
            int showtimeId = Integer.parseInt(showtimeIdParam);
            double totalAmount = Double.parseDouble(totalAmountParam);
            List<String> seatNumbers = Arrays.asList(seatNumbersParam.split(","));
            
            // Book seats
            if (seatDAO.bookSeats(showtimeId, seatNumbers)) {
                // Create booking record
                if (bookingDAO.createBooking(user.getId(), showtimeId, seatNumbersParam, totalAmount)) {
                    response.sendRedirect("confirmation.html?success=true");
                } else {
                    response.sendRedirect("seats.html?error=booking_failed");
                }
            } else {
                response.sendRedirect("seats.html?error=seats_unavailable");
            }
        } else {
            response.sendRedirect("seats.html?error=invalid_data");
        }
    }
}