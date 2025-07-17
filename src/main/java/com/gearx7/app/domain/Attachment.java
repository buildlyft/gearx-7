package com.gearx7.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Attachment.
 */
@Entity
@Table(name = "attachment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "uploaded_date")
    private Instant uploadedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "machines" }, allowSetters = true)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "attachments", "partner" }, allowSetters = true)
    private Machine machine;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Attachment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Attachment fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public Attachment fileType(String fileType) {
        this.setFileType(fileType);
        return this;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public Attachment fileUrl(String fileUrl) {
        this.setFileUrl(fileUrl);
        return this;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Instant getUploadedDate() {
        return this.uploadedDate;
    }

    public Attachment uploadedDate(Instant uploadedDate) {
        this.setUploadedDate(uploadedDate);
        return this;
    }

    public void setUploadedDate(Instant uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public Partner getPartner() {
        return this.partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Attachment partner(Partner partner) {
        this.setPartner(partner);
        return this;
    }

    public Machine getMachine() {
        return this.machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Attachment machine(Machine machine) {
        this.setMachine(machine);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        return getId() != null && getId().equals(((Attachment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Attachment{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", uploadedDate='" + getUploadedDate() + "'" +
            "}";
    }
}
