package com.thg.procurement.questionnaire.service.web.rest;

import com.thg.procurement.questionnaire.service.domain.QuestionnaireAudit;
import com.thg.procurement.questionnaire.service.repository.QuestionnaireAuditRepository;
import com.thg.procurement.questionnaire.service.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.thg.procurement.questionnaire.service.domain.QuestionnaireAudit}.
 */
@RestController
@RequestMapping("/api")
public class QuestionnaireAuditResource {

    private final Logger log = LoggerFactory.getLogger(QuestionnaireAuditResource.class);

    private static final String ENTITY_NAME = "questionnaireServiceQuestionnaireAudit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuestionnaireAuditRepository questionnaireAuditRepository;

    public QuestionnaireAuditResource(QuestionnaireAuditRepository questionnaireAuditRepository) {
        this.questionnaireAuditRepository = questionnaireAuditRepository;
    }

    /**
     * {@code POST  /questionnaire-audits} : Create a new questionnaireAudit.
     *
     * @param questionnaireAudit the questionnaireAudit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new questionnaireAudit, or with status {@code 400 (Bad Request)} if the questionnaireAudit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/questionnaire-audits")
    public ResponseEntity<QuestionnaireAudit> createQuestionnaireAudit(@Valid @RequestBody QuestionnaireAudit questionnaireAudit)
        throws URISyntaxException {
        log.debug("REST request to save QuestionnaireAudit : {}", questionnaireAudit);
        if (questionnaireAudit.getId() != null) {
            throw new BadRequestAlertException("A new questionnaireAudit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        QuestionnaireAudit result = questionnaireAuditRepository.save(questionnaireAudit);
        return ResponseEntity
            .created(new URI("/api/questionnaire-audits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /questionnaire-audits/:id} : Updates an existing questionnaireAudit.
     *
     * @param id the id of the questionnaireAudit to save.
     * @param questionnaireAudit the questionnaireAudit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionnaireAudit,
     * or with status {@code 400 (Bad Request)} if the questionnaireAudit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the questionnaireAudit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/questionnaire-audits/{id}")
    public ResponseEntity<QuestionnaireAudit> updateQuestionnaireAudit(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody QuestionnaireAudit questionnaireAudit
    ) throws URISyntaxException {
        log.debug("REST request to update QuestionnaireAudit : {}, {}", id, questionnaireAudit);
        if (questionnaireAudit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionnaireAudit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionnaireAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        QuestionnaireAudit result = questionnaireAuditRepository.save(questionnaireAudit);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, questionnaireAudit.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /questionnaire-audits/:id} : Partial updates given fields of an existing questionnaireAudit, field will ignore if it is null
     *
     * @param id the id of the questionnaireAudit to save.
     * @param questionnaireAudit the questionnaireAudit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionnaireAudit,
     * or with status {@code 400 (Bad Request)} if the questionnaireAudit is not valid,
     * or with status {@code 404 (Not Found)} if the questionnaireAudit is not found,
     * or with status {@code 500 (Internal Server Error)} if the questionnaireAudit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/questionnaire-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuestionnaireAudit> partialUpdateQuestionnaireAudit(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody QuestionnaireAudit questionnaireAudit
    ) throws URISyntaxException {
        log.debug("REST request to partial update QuestionnaireAudit partially : {}, {}", id, questionnaireAudit);
        if (questionnaireAudit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionnaireAudit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionnaireAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuestionnaireAudit> result = questionnaireAuditRepository
            .findById(questionnaireAudit.getId())
            .map(existingQuestionnaireAudit -> {
                if (questionnaireAudit.getQuestionnaireId() != null) {
                    existingQuestionnaireAudit.setQuestionnaireId(questionnaireAudit.getQuestionnaireId());
                }
                if (questionnaireAudit.getActionBy() != null) {
                    existingQuestionnaireAudit.setActionBy(questionnaireAudit.getActionBy());
                }
                if (questionnaireAudit.getActionType() != null) {
                    existingQuestionnaireAudit.setActionType(questionnaireAudit.getActionType());
                }
                if (questionnaireAudit.getActionAt() != null) {
                    existingQuestionnaireAudit.setActionAt(questionnaireAudit.getActionAt());
                }
                if (questionnaireAudit.getClient() != null) {
                    existingQuestionnaireAudit.setClient(questionnaireAudit.getClient());
                }

                return existingQuestionnaireAudit;
            })
            .map(questionnaireAuditRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, questionnaireAudit.getId())
        );
    }

    /**
     * {@code GET  /questionnaire-audits} : get all the questionnaireAudits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of questionnaireAudits in body.
     */
    @GetMapping("/questionnaire-audits")
    public List<QuestionnaireAudit> getAllQuestionnaireAudits() {
        log.debug("REST request to get all QuestionnaireAudits");
        return questionnaireAuditRepository.findAll();
    }

    /**
     * {@code GET  /questionnaire-audits/:id} : get the "id" questionnaireAudit.
     *
     * @param id the id of the questionnaireAudit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the questionnaireAudit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/questionnaire-audits/{id}")
    public ResponseEntity<QuestionnaireAudit> getQuestionnaireAudit(@PathVariable String id) {
        log.debug("REST request to get QuestionnaireAudit : {}", id);
        Optional<QuestionnaireAudit> questionnaireAudit = questionnaireAuditRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(questionnaireAudit);
    }

    /**
     * {@code DELETE  /questionnaire-audits/:id} : delete the "id" questionnaireAudit.
     *
     * @param id the id of the questionnaireAudit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/questionnaire-audits/{id}")
    public ResponseEntity<Void> deleteQuestionnaireAudit(@PathVariable String id) {
        log.debug("REST request to delete QuestionnaireAudit : {}", id);
        questionnaireAuditRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
