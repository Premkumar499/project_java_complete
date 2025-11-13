package com.ksr.controller;

import com.ksr.dao.UserDAO;
import com.ksr.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {
    
    private UserDAO userDAO = new UserDAO();
    
    @PostMapping("/user-login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = userDAO.getUserByEmailAndPassword(request.getEmail(), request.getPassword());
            
            if (user != null) {
                response.put("status", "success");
                response.put("message", "Login successful");
                
                // Create user data map
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("phoneNumber", user.getPhoneNumber());
                
                response.put("user", userData);
            } else {
                response.put("status", "error");
                response.put("message", "Invalid email or password");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Login failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    // Inner class for request body
    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}