package com.ksr.servlet;

import com.ksr.dao.MovieDAO;
import com.ksr.model.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
    
    private MovieDAO movieDAO = new MovieDAO();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        List<Movie> movies = movieDAO.getAllMovies();
        String jsonResponse = objectMapper.writeValueAsString(movies);
        
        response.getWriter().write(jsonResponse);
    }
}