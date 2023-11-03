package com.thg.procurement.questionnaire.service.domain;

import static com.thg.procurement.questionnaire.service.config.Constants.ID_DELIMITER;
import static com.thg.procurement.questionnaire.service.domain.QuestionnaireAudit.TYPE_NAME;
import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

import com.thg.procurement.questionnaire.service.domain.enumeration.ActionType;
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
 * A QuestionnaireAudit.
 */
@Document
@TypeAlias(TYPE_NAME)
@Collection(TYPE_NAME)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionnaireAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_NAME = "questionnaireAudit";

    @Id
    @GeneratedValue(strategy = UNIQUE, delimiter = ID_DELIMITER)
    private String id;

    @NotNull
    @Field
    private UUID questionnaireId;

    @NotNull
    @Field
    private UUID actionBy;

    @NotNull
    @Field
    private ActionType actionType;

    @NotNull
    @Field
    private ZonedDateTime actionAt;

    @NotNull
    @Field
    private UUID client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public QuestionnaireAudit id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getQuestionnaireId() {
        return this.questionnaireId;
    }

    public QuestionnaireAudit questionnaireId(UUID questionnaireId) {
        this.setQuestionnaireId(questionnaireId);
        return this;
    }

    public void setQuestionnaireId(UUID questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public UUID getActionBy() {
        return this.actionBy;
    }

    public QuestionnaireAudit actionBy(UUID actionBy) {
        this.setActionBy(actionBy);
        return this;
    }

    public void setActionBy(UUID actionBy) {
        this.actionBy = actionBy;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public QuestionnaireAudit actionType(ActionType actionType) {
        this.setActionType(actionType);
        return this;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ZonedDateTime getActionAt() {
        return this.actionAt;
    }

    public QuestionnaireAudit actionAt(ZonedDateTime actionAt) {
        this.setActionAt(actionAt);
        return this;
    }

    public void setActionAt(ZonedDateTime actionAt) {
        this.actionAt = actionAt;
    }

    public UUID getClient() {
        return this.client;
    }

    public QuestionnaireAudit client(UUID client) {
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
        if (!(o instanceof QuestionnaireAudit)) {
            return false;
        }
        return id != null && id.equals(((QuestionnaireAudit) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionnaireAudit{" +
            "id=" + getId() +
            ", questionnaireId='" + getQuestionnaireId() + "'" +
            ", actionBy='" + getActionBy() + "'" +
            ", actionType='" + getActionType() + "'" +
            ", actionAt='" + getActionAt() + "'" +
            ", client='" + getClient() + "'" +
            "}";
    }
}
