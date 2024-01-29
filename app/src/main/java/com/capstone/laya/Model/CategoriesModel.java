package com.capstone.laya.Model;

public class CategoriesModel {
    String Category,ImageLink, Color;
    public CategoriesModel(){}
    public CategoriesModel(String Category,String ImageLink, String Color){
        this.Category = Category;
        this.ImageLink = ImageLink;
        this.Color = Color;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
