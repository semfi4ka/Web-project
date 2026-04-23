package com.filippovich.webtask.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cocktail implements Serializable {
    private long id;
    private String name;
    private String description;
    private CocktailStatus status;
    private User author;
    private LocalDateTime createdAt;
    private String imagePath;

    private List<CocktailIngredient> ingredients = new ArrayList<>();

    public Cocktail() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CocktailStatus getStatus() {
        return status;
    }

    public void setStatus(CocktailStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CocktailIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<CocktailIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
