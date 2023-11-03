package com.thg.procurement.questionnaire.service.domain;

import static com.thg.procurement.questionnaire.service.config.Constants.ID_DELIMITER;
import static com.thg.procurement.questionnaire.service.domain.Questionnaire.TYPE_NAME;
import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.repository.Collection;

/**
 * A Questionnaire.
 */
@Document
@TypeAlias(TYPE_NAME)
@Collection(TYPE_NAME)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Questionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_NAME = "questionnaire";

    @Id
    @GeneratedValue(strategy = UNIQUE, delimiter = ID_DELIMITER)
    private String id;

    @NotNull
    @Field
    private String name;

    @NotNull
    @Field
    private String description;

    @NotNull
    @Field
    private Boolean isDeleted;

    @NotNull
    @Field
    private UUID createdBy;

    @NotNull
    @Field
    private ZonedDateTime createdAt;

    @NotNull
    @Field
    private UUID updatedBy;

    @NotNull
    @Field
    private ZonedDateTime updatedAt;

    @NotNull
    @Field
    private String schema;

    @NotNull
    @Field
    private UUID client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Questionnaire id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Questionnaire name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Questionnaire description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Questionnaire isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public UUID getCreatedBy() {
        return this.createdBy;
    }

    public Questionnaire createdBy(UUID createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Questionnaire createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getUpdatedBy() {
        return this.updatedBy;
    }

    public Questionnaire updatedBy(UUID updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Questionnaire updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSchema() {
        return this.schema;
    }

    public Questionnaire schema(String schema) {
        this.setSchema(schema);
        return this;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public UUID getClient() {
        return this.client;
    }

    public Questionnaire client(UUID client) {
        this.setClient(client);
        return this;
    }

    public void setClient(UUID client) {
        this.client = client;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Questionnaire)) {
            return false;
        }
        return id != null && id.equals(((Questionnaire) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Questionnaire{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", schema='" + getSchema() + "'" +
            ", client='" + getClient() + "'" +
            "}";
    }
}
