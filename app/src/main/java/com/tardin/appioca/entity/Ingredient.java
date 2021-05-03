package com.tardin.appioca.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
public class Ingredient implements Serializable {
    private String name;
    private String measurement;
}
