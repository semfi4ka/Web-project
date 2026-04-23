package com.filippovich.webtask.model;

import java.time.LocalDateTime;

public class Comment {
    private long id;
    private long cocktailId;
    private User author;
    private String text;
    private LocalDateTime createdAt;
    private Integer rating;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCocktailId() {
        return cocktailId;
    }

    public void setCocktailId(long cocktailId) {
        this.cocktailId = cocktailId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    // getters/setters
}