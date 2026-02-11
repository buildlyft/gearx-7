package com.gearx7.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "vehicle_document")
public class VehicleDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "vehicle_document_seq", sequenceName = "vehicle_document_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_document_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    @JsonIgnoreProperties(value = { "documents", "operator" }, allowSetters = true)
    private Machine machine;

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "operator_id")
    //    @JsonIgnoreProperties(value = { "vehicleDocument", "machine" }, allowSetters = true)
    //    private MachineOperator operator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "doc_type", length = 100)
    private String docType;

    @Column(name = "file_name", length = 512)
    private String fileName;

    /**
     * storage key (S3/FS) - service is responsible to upload and store the key here
     */

    @Column(name = "file_key", length = 1024)
    private String fileKey;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    private Long size;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    //    public MachineOperator getOperator() {
    //        return operator;
    //    }
    //
    //    public void setOperator(MachineOperator operator) {
    //        this.operator = operator;
    //    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public Machine getMachine() {
        return machine;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public String getDocType() {
        return docType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleDocument)) return false;
        return id != null && id.equals(((VehicleDocument) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "VehicleDocument{" +
            "id=" +
            id +
            ", machine=" +
            machine +
            //            ", operator=" +
            //            operator +
            ", uploadedBy=" +
            uploadedBy +
            ", docType='" +
            docType +
            '\'' +
            ", fileName='" +
            fileName +
            '\'' +
            ", fileKey='" +
            fileKey +
            '\'' +
            ", contentType='" +
            contentType +
            '\'' +
            ", size=" +
            size +
            ", uploadedAt=" +
            uploadedAt +
            ", expiresAt=" +
            expiresAt +
            '}'
        );
    }
}
