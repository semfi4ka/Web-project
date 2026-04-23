package com.filippovich.webtask.model;

import java.time.LocalDateTime;

public class ProfileRating {
    private long cocktailId;
    private String cocktailName;
    private int rating;
    private LocalDateTime updatedAt;

    public long getCocktailId() { return cocktailId; }
    public void setCocktailId(long cocktailId) { this.cocktailId = cocktailId; }

    public String getCocktailName() { return cocktailName; }
    public void setCocktailName(String cocktailName) { this.cocktailName = cocktailName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}