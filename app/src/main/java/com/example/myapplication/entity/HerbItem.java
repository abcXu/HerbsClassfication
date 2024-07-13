package com.example.myapplication.entity;

public class HerbItem {

    private String name;
    private String image;
    private String description;
    private String source;

    public HerbItem() {
    }

    public HerbItem(String name, String image, String description, String source, String effect) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.source = source;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    private String effect;

}
