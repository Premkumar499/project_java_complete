package com.ksr.controller;

import com.ksr.dao.BookingHistoryDAO;
import com.ksr.model.BookingHistory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookingHistoryController {
    
    private BookingHistoryDAO bookingHistoryDAO = new BookingHistoryDAO();
    
    @GetMapping("/booking-history")
    public Map<String, Object> getBookingHistory(@RequestParam int userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<BookingHistory> bookingHistory = bookingHistoryDAO.getBookingHistoryByUserId(userId);
            response.put("success", true);
            response.put("data", bookingHistory);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching booking history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
}