package com.thg.procurement.questionnaire.service.web.rest;

import static com.thg.procurement.questionnaire.service.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thg.procurement.questionnaire.service.IntegrationTest;
import com.thg.procurement.questionnaire.service.domain.ClientAudit;
import com.thg.procurement.questionnaire.service.domain.enumeration.ActionType;
import com.thg.procurement.questionnaire.service.repository.ClientAuditRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link ClientAuditResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientAuditResourceIT {

    private static final UUID DEFAULT_CLIENT_ID = UUID.randomUUID();
    private static final UUID UPDATED_CLIENT_ID = UUID.randomUUID();

    private static final UUID DEFAULT_ACTION_BY = UUID.randomUUID();
    private static final UUID UPDATED_ACTION_BY = UUID.randomUUID();

    private static final ActionType DEFAULT_ACTION_TYPE = ActionType.CREATED;
    private static final ActionType UPDATED_ACTION_TYPE = ActionType.UPDATED;

    private static final ZonedDateTime DEFAULT_ACTION_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ACTION_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/client-audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ClientAuditRepository clientAuditRepository;

    @Autowired
    private MockMvc restClientAuditMockMvc;

    private ClientAudit clientAudit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientAudit createEntity() {
        ClientAudit clientAudit = new ClientAudit()
            .clientId(DEFAULT_CLIENT_ID)
            .actionBy(DEFAULT_ACTION_BY)
            .actionType(DEFAULT_ACTION_TYPE)
            .actionAt(DEFAULT_ACTION_AT);
        return clientAudit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientAudit createUpdatedEntity() {
        ClientAudit clientAudit = new ClientAudit()
            .clientId(UPDATED_CLIENT_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT);
        return clientAudit;
    }

    @BeforeEach
    public void initTest() {
        clientAuditRepository.deleteAll();
        clientAudit = createEntity();
    }

    @Test
    void createClientAudit() throws Exception {
        int databaseSizeBeforeCreate = clientAuditRepository.findAll().size();
        // Create the ClientAudit
        restClientAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isCreated());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeCreate + 1);
        ClientAudit testClientAudit = clientAuditList.get(clientAuditList.size() - 1);
        assertThat(testClientAudit.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testClientAudit.getActionBy()).isEqualTo(DEFAULT_ACTION_BY);
        assertThat(testClientAudit.getActionType()).isEqualTo(DEFAULT_ACTION_TYPE);
        assertThat(testClientAudit.getActionAt()).isEqualTo(DEFAULT_ACTION_AT);
    }

    @Test
    void createClientAuditWithExistingId() throws Exception {
        // Create the ClientAudit with an existing ID
        clientAudit.setId("existing_id");

        int databaseSizeBeforeCreate = clientAuditRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkClientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientAuditRepository.findAll().size();
        // set the field null
        clientAudit.setClientId(null);

        // Create the ClientAudit, which fails.

        restClientAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionByIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientAuditRepository.findAll().size();
        // set the field null
        clientAudit.setActionBy(null);

        // Create the ClientAudit, which fails.

        restClientAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientAuditRepository.findAll().size();
        // set the field null
        clientAudit.setActionType(null);

        // Create the ClientAudit, which fails.

        restClientAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientAuditRepository.findAll().size();
        // set the field null
        clientAudit.setActionAt(null);

        // Create the ClientAudit, which fails.

        restClientAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllClientAudits() throws Exception {
        // Initialize the database
        clientAuditRepository.save(clientAudit);

        // Get all the clientAuditList
        restClientAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientAudit.getId())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.toString())))
            .andExpect(jsonPath("$.[*].actionBy").value(hasItem(DEFAULT_ACTION_BY.toString())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].actionAt").value(hasItem(sameInstant(DEFAULT_ACTION_AT))));
    }

    @Test
    void getClientAudit() throws Exception {
        // Initialize the database
        clientAuditRepository.save(clientAudit);

        // Get the clientAudit
        restClientAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, clientAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientAudit.getId()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.toString()))
            .andExpect(jsonPath("$.actionBy").value(DEFAULT_ACTION_BY.toString()))
            .andExpect(jsonPath("$.actionType").value(DEFAULT_ACTION_TYPE.toString()))
            .andExpect(jsonPath("$.actionAt").value(sameInstant(DEFAULT_ACTION_AT)));
    }

    @Test
    void getNonExistingClientAudit() throws Exception {
        // Get the clientAudit
        restClientAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingClientAudit() throws Exception {
        // Initialize the database
        clientAuditRepository.save(clientAudit);

        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();

        // Update the clientAudit
        ClientAudit updatedClientAudit = clientAuditRepository.findById(clientAudit.getId()).get();
        updatedClientAudit
            .clientId(UPDATED_CLIENT_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT);

        restClientAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientAudit.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClientAudit))
            )
            .andExpect(status().isOk());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
        ClientAudit testClientAudit = clientAuditList.get(clientAuditList.size() - 1);
        assertThat(testClientAudit.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testClientAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testClientAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testClientAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
    }

    @Test
    void putNonExistingClientAudit() throws Exception {
        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();
        clientAudit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientAudit.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClientAudit() throws Exception {
        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();
        clientAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClientAudit() throws Exception {
        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();
        clientAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAuditMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClientAuditWithPatch() throws Exception {
        // Initialize the database
        clientAuditRepository.save(clientAudit);

        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();

        // Update the clientAudit using partial update
        ClientAudit partialUpdatedClientAudit = new ClientAudit();
        partialUpdatedClientAudit.setId(clientAudit.getId());

        partialUpdatedClientAudit
            .clientId(UPDATED_CLIENT_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT);

        restClientAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientAudit))
            )
            .andExpect(status().isOk());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
        ClientAudit testClientAudit = clientAuditList.get(clientAuditList.size() - 1);
        assertThat(testClientAudit.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testClientAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testClientAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testClientAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
    }

    @Test
    void fullUpdateClientAuditWithPatch() throws Exception {
        // Initialize the database
        clientAuditRepository.save(clientAudit);

        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();

        // Update the clientAudit using partial update
        ClientAudit partialUpdatedClientAudit = new ClientAudit();
        partialUpdatedClientAudit.setId(clientAudit.getId());

        partialUpdatedClientAudit
            .clientId(UPDATED_CLIENT_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT);

        restClientAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientAudit))
            )
            .andExpect(status().isOk());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
        ClientAudit testClientAudit = clientAuditList.get(clientAuditList.size() - 1);
        assertThat(testClientAudit.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testClientAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testClientAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testClientAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
    }

    @Test
    void patchNonExistingClientAudit() throws Exception {
        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();
        clientAudit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClientAudit() throws Exception {
        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();
        clientAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClientAudit() throws Exception {
        int databaseSizeBeforeUpdate = clientAuditRepository.findAll().size();
        clientAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAuditMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientAudit))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClientAudit() throws Exception {
        // Initialize the database
        clientAuditRepository.save(clientAudit);

        int databaseSizeBeforeDelete = clientAuditRepository.findAll().size();

        // Delete the clientAudit
        restClientAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientAudit.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<ClientAudit> clientAuditList = clientAuditRepository.findAll();
        assertThat(clientAuditList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
