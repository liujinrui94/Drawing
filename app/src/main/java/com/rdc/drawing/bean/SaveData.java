package com.rdc.drawing.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Edianzu on 2018/4/12.
 */
@Entity
public class SaveData {

    @Id
    private Long id;
    private String imageName;
    private String picturePath;

    private String filePath;


    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Generated(hash = 1180756066)
    public SaveData(Long id, String imageName, String picturePath,
            String filePath) {
        this.id = id;
        this.imageName = imageName;
        this.picturePath = picturePath;
        this.filePath = filePath;
    }

    @Generated(hash = 2052259273)
    public SaveData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "SaveData{" +
                "id=" + id +
                ", imageName='" + imageName + '\'' +
                ", picturePath='" + picturePath + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
