package com.tardin.appioca.entity;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Recipe implements Serializable {
    private String id;
    private String name;
    private String author;
    private String authorId;
    private String description;
    private String photo;
    private Integer yield;
    private Integer preparationTime;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> preparationSteps;

    public String getPreparationTimeInString() {
        return preparationTime.toString() + " min";
    }

    public String getYieldInString() {
        return yield.toString() + ((yield > 1 || yield == 0) ? " porções" : " porção");
    }

    public String getShortName(int maxNameChars) {
        if (this.name.length() > maxNameChars)
            return this.name.substring(0, maxNameChars - 3) + "...";
        return this.name;
    }

    public boolean isValid() {
        return checkIfFieldsAreNotNull()
                && checkIfFieldsAreNotEmpty()
                && checkIfNumberFieldsAreGreaterThanZero();
    }

    private boolean checkIfFieldsAreNotNull() {
        return this.name != null
                && this.author != null
                && this.authorId != null
                && this.description != null
                && this.photo != null
                && this.yield != null
                && this.preparationTime != null
                && this.ingredients != null
                && this.preparationSteps != null;
    }

    private boolean checkIfFieldsAreNotEmpty() {
        return !this.name.isEmpty()
                && !this.author.isEmpty()
                && !this.authorId.isEmpty()
                && !this.description.isEmpty()
                && !this.photo.isEmpty()
                && !this.ingredients.isEmpty()
                && !this.preparationSteps.isEmpty();
    }

    private boolean checkIfNumberFieldsAreGreaterThanZero() {
        return this.yield > 0
                && this.preparationTime > 0;
    }
}
