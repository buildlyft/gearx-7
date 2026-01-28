package com.gearx7.app.service.dto;

import java.time.Instant;

public class VehicleDocumentDTO {

    // =====These all are Machine Document Details=====

    private Long id;
    private String docType;
    private String fileName;
    private String fileUrl;
    private String contentType;
    private Long size;
    private Instant uploadedAt;
    private String uploadedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    @Override
    public String toString() {
        return (
            "VehicleDocumentDTO{" +
            "id=" +
            id +
            ", docType='" +
            docType +
            '\'' +
            ", fileName='" +
            fileName +
            '\'' +
            ", fileUrl='" +
            fileUrl +
            '\'' +
            ", contentType='" +
            contentType +
            '\'' +
            ", size=" +
            size +
            ", uploadedAt=" +
            uploadedAt +
            ", uploadedBy='" +
            uploadedBy +
            '\'' +
            '}'
        );
    }
}
