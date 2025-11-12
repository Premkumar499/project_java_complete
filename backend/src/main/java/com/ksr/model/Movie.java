package com.ksr.model;

import java.time.LocalDate;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String genre;
    private int duration; // in minutes
    private LocalDate releaseDate;
    private String language;
    private String rating;
    
    public Movie() {}
    
    public Movie(String title, String description, String genre, int duration, 
                 LocalDate releaseDate, String language, String rating) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.language = language;
        this.rating = rating;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getRating() {
        return rating;
    }
    
    public void setRating(String rating) {
        this.rating = rating;
    }
}