package com.thg.procurement.questionnaire.service.web.rest;

import com.thg.procurement.questionnaire.service.domain.ClientAudit;
import com.thg.procurement.questionnaire.service.repository.ClientAuditRepository;
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
 * REST controller for managing {@link com.thg.procurement.questionnaire.service.domain.ClientAudit}.
 */
@RestController
@RequestMapping("/api")
public class ClientAuditResource {

    private final Logger log = LoggerFactory.getLogger(ClientAuditResource.class);

    private static final String ENTITY_NAME = "questionnaireServiceClientAudit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientAuditRepository clientAuditRepository;

    public ClientAuditResource(ClientAuditRepository clientAuditRepository) {
        this.clientAuditRepository = clientAuditRepository;
    }

    /**
     * {@code POST  /client-audits} : Create a new clientAudit.
     *
     * @param clientAudit the clientAudit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientAudit, or with status {@code 400 (Bad Request)} if the clientAudit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/client-audits")
    public ResponseEntity<ClientAudit> createClientAudit(@Valid @RequestBody ClientAudit clientAudit) throws URISyntaxException {
        log.debug("REST request to save ClientAudit : {}", clientAudit);
        if (clientAudit.getId() != null) {
            throw new BadRequestAlertException("A new clientAudit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientAudit result = clientAuditRepository.save(clientAudit);
        return ResponseEntity
            .created(new URI("/api/client-audits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /client-audits/:id} : Updates an existing clientAudit.
     *
     * @param id the id of the clientAudit to save.
     * @param clientAudit the clientAudit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientAudit,
     * or with status {@code 400 (Bad Request)} if the clientAudit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientAudit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/client-audits/{id}")
    public ResponseEntity<ClientAudit> updateClientAudit(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ClientAudit clientAudit
    ) throws URISyntaxException {
        log.debug("REST request to update ClientAudit : {}, {}", id, clientAudit);
        if (clientAudit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientAudit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientAudit result = clientAuditRepository.save(clientAudit);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientAudit.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /client-audits/:id} : Partial updates given fields of an existing clientAudit, field will ignore if it is null
     *
     * @param id the id of the clientAudit to save.
     * @param clientAudit the clientAudit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientAudit,
     * or with status {@code 400 (Bad Request)} if the clientAudit is not valid,
     * or with status {@code 404 (Not Found)} if the clientAudit is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientAudit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/client-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientAudit> partialUpdateClientAudit(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ClientAudit clientAudit
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientAudit partially : {}, {}", id, clientAudit);
        if (clientAudit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientAudit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientAudit> result = clientAuditRepository
            .findById(clientAudit.getId())
            .map(existingClientAudit -> {
                if (clientAudit.getClientId() != null) {
                    existingClientAudit.setClientId(clientAudit.getClientId());
                }
                if (clientAudit.getActionBy() != null) {
                    existingClientAudit.setActionBy(clientAudit.getActionBy());
                }
                if (clientAudit.getActionType() != null) {
                    existingClientAudit.setActionType(clientAudit.getActionType());
                }
                if (clientAudit.getActionAt() != null) {
                    existingClientAudit.setActionAt(clientAudit.getActionAt());
                }

                return existingClientAudit;
            })
            .map(clientAuditRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientAudit.getId())
        );
    }

    /**
     * {@code GET  /client-audits} : get all the clientAudits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientAudits in body.
     */
    @GetMapping("/client-audits")
    public List<ClientAudit> getAllClientAudits() {
        log.debug("REST request to get all ClientAudits");
        return clientAuditRepository.findAll();
    }

    /**
     * {@code GET  /client-audits/:id} : get the "id" clientAudit.
     *
     * @param id the id of the clientAudit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientAudit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/client-audits/{id}")
    public ResponseEntity<ClientAudit> getClientAudit(@PathVariable String id) {
        log.debug("REST request to get ClientAudit : {}", id);
        Optional<ClientAudit> clientAudit = clientAuditRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clientAudit);
    }

    /**
     * {@code DELETE  /client-audits/:id} : delete the "id" clientAudit.
     *
     * @param id the id of the clientAudit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/client-audits/{id}")
    public ResponseEntity<Void> deleteClientAudit(@PathVariable String id) {
        log.debug("REST request to delete ClientAudit : {}", id);
        clientAuditRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
