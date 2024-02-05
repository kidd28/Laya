package com.capstone.laya.Model;

public class AudioModel {
    String Name,FilePath,FileName,FileLink, ImageLink,Category,Id,UserUID;

    public AudioModel(){}

    public AudioModel(String Name, String FilePath, String FileName, String FileLink,String ImageLink,String Category,String Id,String UserUID){
        this.Name = Name;
        this.FilePath = FilePath;
        this.FileName = FileName;
        this.FileLink = FileLink;
        this.ImageLink = ImageLink;
        this.Category = Category;
        this.Id = Id;
        this.UserUID = UserUID;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileLink() {
        return FileLink;
    }

    public void setFileLink(String fileLink) {
        FileLink = fileLink;
    }
}
