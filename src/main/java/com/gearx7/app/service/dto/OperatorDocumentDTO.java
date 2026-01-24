package com.gearx7.app.service.dto;

import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;

public class OperatorDocumentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long documentId; // response
    private String docType; // request + response
    private String url; // response (Cloudinary)

    //private MultipartFile file;  // request

    // getters & setters

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /* public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }*/

    @Override
    public String toString() {
        return (
            "OperatorDocumentDTO{" +
            "documentId=" +
            documentId +
            ", docType='" +
            docType +
            '\'' +
            ", url='" +
            url +
            '\'' +
            // ", file=" + file +
            '}'
        );
    }
}
