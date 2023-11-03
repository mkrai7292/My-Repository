package com.thg.procurement.questionnaire.service.web.rest;

import com.thg.procurement.questionnaire.service.domain.AnswerAudit;
import com.thg.procurement.questionnaire.service.repository.AnswerAuditRepository;
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
 * REST controller for managing {@link com.thg.procurement.questionnaire.service.domain.AnswerAudit}.
 */
@RestController
@RequestMapping("/api")
public class AnswerAuditResource {

    private final Logger log = LoggerFactory.getLogger(AnswerAuditResource.class);

    private static final String ENTITY_NAME = "questionnaireServiceAnswerAudit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnswerAuditRepository answerAuditRepository;

    public AnswerAuditResource(AnswerAuditRepository answerAuditRepository) {
        this.answerAuditRepository = answerAuditRepository;
    }

    /**
     * {@code POST  /answer-audits} : Create a new answerAudit.
     *
     * @param answerAudit the answerAudit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new answerAudit, or with status {@code 400 (Bad Request)} if the answerAudit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/answer-audits")
    public ResponseEntity<AnswerAudit> createAnswerAudit(@Valid @RequestBody AnswerAudit answerAudit) throws URISyntaxException {
        log.debug("REST request to save AnswerAudit : {}", answerAudit);
        if (answerAudit.getId() != null) {
            throw new BadRequestAlertException("A new answerAudit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AnswerAudit result = answerAuditRepository.save(answerAudit);
        return ResponseEntity
            .created(new URI("/api/answer-audits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /answer-audits/:id} : Updates an existing answerAudit.
     *
     * @param id the id of the answerAudit to save.
     * @param answerAudit the answerAudit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated answerAudit,
     * or with status {@code 400 (Bad Request)} if the answerAudit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the answerAudit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/answer-audits/{id}")
    public ResponseEntity<AnswerAudit> updateAnswerAudit(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody AnswerAudit answerAudit
    ) throws URISyntaxException {
        log.debug("REST request to update AnswerAudit : {}, {}", id, answerAudit);
        if (answerAudit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, answerAudit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!answerAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AnswerAudit result = answerAuditRepository.save(answerAudit);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, answerAudit.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /answer-audits/:id} : Partial updates given fields of an existing answerAudit, field will ignore if it is null
     *
     * @param id the id of the answerAudit to save.
     * @param answerAudit the answerAudit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated answerAudit,
     * or with status {@code 400 (Bad Request)} if the answerAudit is not valid,
     * or with status {@code 404 (Not Found)} if the answerAudit is not found,
     * or with status {@code 500 (Internal Server Error)} if the answerAudit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/answer-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AnswerAudit> partialUpdateAnswerAudit(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody AnswerAudit answerAudit
    ) throws URISyntaxException {
        log.debug("REST request to partial update AnswerAudit partially : {}, {}", id, answerAudit);
        if (answerAudit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, answerAudit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!answerAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AnswerAudit> result = answerAuditRepository
            .findById(answerAudit.getId())
            .map(existingAnswerAudit -> {
                if (answerAudit.getAnswerId() != null) {
                    existingAnswerAudit.setAnswerId(answerAudit.getAnswerId());
                }
                if (answerAudit.getActionBy() != null) {
                    existingAnswerAudit.setActionBy(answerAudit.getActionBy());
                }
                if (answerAudit.getActionType() != null) {
                    existingAnswerAudit.setActionType(answerAudit.getActionType());
                }
                if (answerAudit.getActionAt() != null) {
                    existingAnswerAudit.setActionAt(answerAudit.getActionAt());
                }
                if (answerAudit.getClient() != null) {
                    existingAnswerAudit.setClient(answerAudit.getClient());
                }

                return existingAnswerAudit;
            })
            .map(answerAuditRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, answerAudit.getId())
        );
    }

    /**
     * {@code GET  /answer-audits} : get all the answerAudits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of answerAudits in body.
     */
    @GetMapping("/answer-audits")
    public List<AnswerAudit> getAllAnswerAudits() {
        log.debug("REST request to get all AnswerAudits");
        return answerAuditRepository.findAll();
    }

    /**
     * {@code GET  /answer-audits/:id} : get the "id" answerAudit.
     *
     * @param id the id of the answerAudit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the answerAudit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/answer-audits/{id}")
    public ResponseEntity<AnswerAudit> getAnswerAudit(@PathVariable String id) {
        log.debug("REST request to get AnswerAudit : {}", id);
        Optional<AnswerAudit> answerAudit = answerAuditRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(answerAudit);
    }

    /**
     * {@code DELETE  /answer-audits/:id} : delete the "id" answerAudit.
     *
     * @param id the id of the answerAudit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/answer-audits/{id}")
    public ResponseEntity<Void> deleteAnswerAudit(@PathVariable String id) {
        log.debug("REST request to delete AnswerAudit : {}", id);
        answerAuditRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
