package com.thg.procurement.questionnaire.service.web.rest;

import static com.thg.procurement.questionnaire.service.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thg.procurement.questionnaire.service.IntegrationTest;
import com.thg.procurement.questionnaire.service.domain.AnswerAudit;
import com.thg.procurement.questionnaire.service.domain.enumeration.ActionType;
import com.thg.procurement.questionnaire.service.repository.AnswerAuditRepository;
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
 * Integration tests for the {@link AnswerAuditResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AnswerAuditResourceIT {

    private static final UUID DEFAULT_ANSWER_ID = UUID.randomUUID();
    private static final UUID UPDATED_ANSWER_ID = UUID.randomUUID();

    private static final UUID DEFAULT_ACTION_BY = UUID.randomUUID();
    private static final UUID UPDATED_ACTION_BY = UUID.randomUUID();

    private static final ActionType DEFAULT_ACTION_TYPE = ActionType.CREATED;
    private static final ActionType UPDATED_ACTION_TYPE = ActionType.UPDATED;

    private static final ZonedDateTime DEFAULT_ACTION_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ACTION_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final UUID DEFAULT_CLIENT = UUID.randomUUID();
    private static final UUID UPDATED_CLIENT = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/answer-audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private AnswerAuditRepository answerAuditRepository;

    @Autowired
    private MockMvc restAnswerAuditMockMvc;

    private AnswerAudit answerAudit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AnswerAudit createEntity() {
        AnswerAudit answerAudit = new AnswerAudit()
            .answerId(DEFAULT_ANSWER_ID)
            .actionBy(DEFAULT_ACTION_BY)
            .actionType(DEFAULT_ACTION_TYPE)
            .actionAt(DEFAULT_ACTION_AT)
            .client(DEFAULT_CLIENT);
        return answerAudit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AnswerAudit createUpdatedEntity() {
        AnswerAudit answerAudit = new AnswerAudit()
            .answerId(UPDATED_ANSWER_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT)
            .client(UPDATED_CLIENT);
        return answerAudit;
    }

    @BeforeEach
    public void initTest() {
        answerAuditRepository.deleteAll();
        answerAudit = createEntity();
    }

    @Test
    void createAnswerAudit() throws Exception {
        int databaseSizeBeforeCreate = answerAuditRepository.findAll().size();
        // Create the AnswerAudit
        restAnswerAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isCreated());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeCreate + 1);
        AnswerAudit testAnswerAudit = answerAuditList.get(answerAuditList.size() - 1);
        assertThat(testAnswerAudit.getAnswerId()).isEqualTo(DEFAULT_ANSWER_ID);
        assertThat(testAnswerAudit.getActionBy()).isEqualTo(DEFAULT_ACTION_BY);
        assertThat(testAnswerAudit.getActionType()).isEqualTo(DEFAULT_ACTION_TYPE);
        assertThat(testAnswerAudit.getActionAt()).isEqualTo(DEFAULT_ACTION_AT);
        assertThat(testAnswerAudit.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void createAnswerAuditWithExistingId() throws Exception {
        // Create the AnswerAudit with an existing ID
        answerAudit.setId("existing_id");

        int databaseSizeBeforeCreate = answerAuditRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnswerAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkAnswerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerAuditRepository.findAll().size();
        // set the field null
        answerAudit.setAnswerId(null);

        // Create the AnswerAudit, which fails.

        restAnswerAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionByIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerAuditRepository.findAll().size();
        // set the field null
        answerAudit.setActionBy(null);

        // Create the AnswerAudit, which fails.

        restAnswerAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerAuditRepository.findAll().size();
        // set the field null
        answerAudit.setActionType(null);

        // Create the AnswerAudit, which fails.

        restAnswerAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerAuditRepository.findAll().size();
        // set the field null
        answerAudit.setActionAt(null);

        // Create the AnswerAudit, which fails.

        restAnswerAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkClientIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerAuditRepository.findAll().size();
        // set the field null
        answerAudit.setClient(null);

        // Create the AnswerAudit, which fails.

        restAnswerAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAnswerAudits() throws Exception {
        // Initialize the database
        answerAuditRepository.save(answerAudit);

        // Get all the answerAuditList
        restAnswerAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answerAudit.getId())))
            .andExpect(jsonPath("$.[*].answerId").value(hasItem(DEFAULT_ANSWER_ID.toString())))
            .andExpect(jsonPath("$.[*].actionBy").value(hasItem(DEFAULT_ACTION_BY.toString())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].actionAt").value(hasItem(sameInstant(DEFAULT_ACTION_AT))))
            .andExpect(jsonPath("$.[*].client").value(hasItem(DEFAULT_CLIENT.toString())));
    }

    @Test
    void getAnswerAudit() throws Exception {
        // Initialize the database
        answerAuditRepository.save(answerAudit);

        // Get the answerAudit
        restAnswerAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, answerAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(answerAudit.getId()))
            .andExpect(jsonPath("$.answerId").value(DEFAULT_ANSWER_ID.toString()))
            .andExpect(jsonPath("$.actionBy").value(DEFAULT_ACTION_BY.toString()))
            .andExpect(jsonPath("$.actionType").value(DEFAULT_ACTION_TYPE.toString()))
            .andExpect(jsonPath("$.actionAt").value(sameInstant(DEFAULT_ACTION_AT)))
            .andExpect(jsonPath("$.client").value(DEFAULT_CLIENT.toString()));
    }

    @Test
    void getNonExistingAnswerAudit() throws Exception {
        // Get the answerAudit
        restAnswerAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingAnswerAudit() throws Exception {
        // Initialize the database
        answerAuditRepository.save(answerAudit);

        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();

        // Update the answerAudit
        AnswerAudit updatedAnswerAudit = answerAuditRepository.findById(answerAudit.getId()).get();
        updatedAnswerAudit
            .answerId(UPDATED_ANSWER_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT)
            .client(UPDATED_CLIENT);

        restAnswerAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAnswerAudit.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAnswerAudit))
            )
            .andExpect(status().isOk());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
        AnswerAudit testAnswerAudit = answerAuditList.get(answerAuditList.size() - 1);
        assertThat(testAnswerAudit.getAnswerId()).isEqualTo(UPDATED_ANSWER_ID);
        assertThat(testAnswerAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testAnswerAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testAnswerAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
        assertThat(testAnswerAudit.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void putNonExistingAnswerAudit() throws Exception {
        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();
        answerAudit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, answerAudit.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAnswerAudit() throws Exception {
        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();
        answerAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAnswerAudit() throws Exception {
        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();
        answerAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerAuditMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAnswerAuditWithPatch() throws Exception {
        // Initialize the database
        answerAuditRepository.save(answerAudit);

        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();

        // Update the answerAudit using partial update
        AnswerAudit partialUpdatedAnswerAudit = new AnswerAudit();
        partialUpdatedAnswerAudit.setId(answerAudit.getId());

        partialUpdatedAnswerAudit.answerId(UPDATED_ANSWER_ID).actionType(UPDATED_ACTION_TYPE).actionAt(UPDATED_ACTION_AT);

        restAnswerAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswerAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswerAudit))
            )
            .andExpect(status().isOk());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
        AnswerAudit testAnswerAudit = answerAuditList.get(answerAuditList.size() - 1);
        assertThat(testAnswerAudit.getAnswerId()).isEqualTo(UPDATED_ANSWER_ID);
        assertThat(testAnswerAudit.getActionBy()).isEqualTo(DEFAULT_ACTION_BY);
        assertThat(testAnswerAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testAnswerAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
        assertThat(testAnswerAudit.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void fullUpdateAnswerAuditWithPatch() throws Exception {
        // Initialize the database
        answerAuditRepository.save(answerAudit);

        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();

        // Update the answerAudit using partial update
        AnswerAudit partialUpdatedAnswerAudit = new AnswerAudit();
        partialUpdatedAnswerAudit.setId(answerAudit.getId());

        partialUpdatedAnswerAudit
            .answerId(UPDATED_ANSWER_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT)
            .client(UPDATED_CLIENT);

        restAnswerAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswerAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswerAudit))
            )
            .andExpect(status().isOk());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
        AnswerAudit testAnswerAudit = answerAuditList.get(answerAuditList.size() - 1);
        assertThat(testAnswerAudit.getAnswerId()).isEqualTo(UPDATED_ANSWER_ID);
        assertThat(testAnswerAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testAnswerAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testAnswerAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
        assertThat(testAnswerAudit.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void patchNonExistingAnswerAudit() throws Exception {
        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();
        answerAudit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, answerAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAnswerAudit() throws Exception {
        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();
        answerAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAnswerAudit() throws Exception {
        int databaseSizeBeforeUpdate = answerAuditRepository.findAll().size();
        answerAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerAuditMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answerAudit))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AnswerAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAnswerAudit() throws Exception {
        // Initialize the database
        answerAuditRepository.save(answerAudit);

        int databaseSizeBeforeDelete = answerAuditRepository.findAll().size();

        // Delete the answerAudit
        restAnswerAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, answerAudit.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<AnswerAudit> answerAuditList = answerAuditRepository.findAll();
        assertThat(answerAuditList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
