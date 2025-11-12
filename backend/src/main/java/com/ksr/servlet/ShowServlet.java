package com.ksr.servlet;

import com.ksr.dao.ShowDAO;
import com.ksr.model.Showtime;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/showtimes")
public class ShowServlet extends HttpServlet {
    
    private ShowDAO showDAO = new ShowDAO();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String movieIdParam = request.getParameter("movieId");
        if (movieIdParam != null) {
            int movieId = Integer.parseInt(movieIdParam);
            List<Showtime> showtimes = showDAO.getShowtimesByMovieId(movieId);
            String jsonResponse = objectMapper.writeValueAsString(showtimes);
            response.getWriter().write(jsonResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Movie ID is required\"}");
        }
    }
}