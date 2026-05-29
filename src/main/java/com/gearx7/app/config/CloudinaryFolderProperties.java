package com.gearx7.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloudinary.folders")
public class CloudinaryFolderProperties {

    private String machineDocuments;

    private String operatorPhoto;

    private String operatorLicense;

    private String categoryImage;

    private String subcategoryImage;

    private String typeImage;

    private String operatorBaseFolder;

    public String getOperatorBaseFolder() {
        return operatorBaseFolder;
    }

    public void setOperatorBaseFolder(String operatorBaseFolder) {
        this.operatorBaseFolder = operatorBaseFolder;
    }

    public String getMachineDocuments() {
        return machineDocuments;
    }

    public void setMachineDocuments(String machineDocuments) {
        this.machineDocuments = machineDocuments;
    }

    public String getOperatorPhoto() {
        return operatorPhoto;
    }

    public void setOperatorPhoto(String operatorPhoto) {
        this.operatorPhoto = operatorPhoto;
    }

    public String getOperatorLicense() {
        return operatorLicense;
    }

    public void setOperatorLicense(String operatorLicense) {
        this.operatorLicense = operatorLicense;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getSubcategoryImage() {
        return subcategoryImage;
    }

    public void setSubcategoryImage(String subcategoryImage) {
        this.subcategoryImage = subcategoryImage;
    }

    public String getTypeImage() {
        return typeImage;
    }

    public void setTypeImage(String typeImage) {
        this.typeImage = typeImage;
    }
}
