package com.filippovich.webtask.model;

import java.time.LocalDateTime;

public class ProfileComment {
    private long cocktailId;
    private String cocktailName;
    private String text;
    private Integer rating; // can be null
    private LocalDateTime createdAt;

    public long getCocktailId() { return cocktailId; }
    public void setCocktailId(long cocktailId) { this.cocktailId = cocktailId; }

    public String getCocktailName() { return cocktailName; }
    public void setCocktailName(String cocktailName) { this.cocktailName = cocktailName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}