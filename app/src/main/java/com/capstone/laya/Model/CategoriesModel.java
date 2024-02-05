package com.capstone.laya.Model;

public class CategoriesModel {
    String Category,ImageLink, Color,UserUID;
    public CategoriesModel(){}
    public CategoriesModel(String Category,String ImageLink, String Color,String UserUID){
        this.Category = Category;
        this.ImageLink = ImageLink;
        this.Color = Color;
        this.UserUID = UserUID;
    }

    public String getColor() {
        return Color;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
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
