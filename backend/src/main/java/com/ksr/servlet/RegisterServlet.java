package com.ksr.servlet;

import com.ksr.dao.UserDAO;
import com.ksr.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phoneNumber = request.getParameter("phoneNumber");
        
        if (userDAO.emailExists(email)) {
            response.sendRedirect("signup.html?error=exists");
            return;
        }
        
        User user = new User(username, email, password, phoneNumber);
        
        if (userDAO.createUser(user)) {
            response.sendRedirect("login.html?success=registered");
        } else {
            response.sendRedirect("signup.html?error=failed");
        }
    }
}