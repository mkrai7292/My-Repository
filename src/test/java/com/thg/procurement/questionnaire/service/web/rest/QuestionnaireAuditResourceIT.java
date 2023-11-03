package com.thg.procurement.questionnaire.service.web.rest;

import static com.thg.procurement.questionnaire.service.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thg.procurement.questionnaire.service.IntegrationTest;
import com.thg.procurement.questionnaire.service.domain.QuestionnaireAudit;
import com.thg.procurement.questionnaire.service.domain.enumeration.ActionType;
import com.thg.procurement.questionnaire.service.repository.QuestionnaireAuditRepository;
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
 * Integration tests for the {@link QuestionnaireAuditResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuestionnaireAuditResourceIT {

    private static final UUID DEFAULT_QUESTIONNAIRE_ID = UUID.randomUUID();
    private static final UUID UPDATED_QUESTIONNAIRE_ID = UUID.randomUUID();

    private static final UUID DEFAULT_ACTION_BY = UUID.randomUUID();
    private static final UUID UPDATED_ACTION_BY = UUID.randomUUID();

    private static final ActionType DEFAULT_ACTION_TYPE = ActionType.CREATED;
    private static final ActionType UPDATED_ACTION_TYPE = ActionType.UPDATED;

    private static final ZonedDateTime DEFAULT_ACTION_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ACTION_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final UUID DEFAULT_CLIENT = UUID.randomUUID();
    private static final UUID UPDATED_CLIENT = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/questionnaire-audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private QuestionnaireAuditRepository questionnaireAuditRepository;

    @Autowired
    private MockMvc restQuestionnaireAuditMockMvc;

    private QuestionnaireAudit questionnaireAudit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionnaireAudit createEntity() {
        QuestionnaireAudit questionnaireAudit = new QuestionnaireAudit()
            .questionnaireId(DEFAULT_QUESTIONNAIRE_ID)
            .actionBy(DEFAULT_ACTION_BY)
            .actionType(DEFAULT_ACTION_TYPE)
            .actionAt(DEFAULT_ACTION_AT)
            .client(DEFAULT_CLIENT);
        return questionnaireAudit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionnaireAudit createUpdatedEntity() {
        QuestionnaireAudit questionnaireAudit = new QuestionnaireAudit()
            .questionnaireId(UPDATED_QUESTIONNAIRE_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT)
            .client(UPDATED_CLIENT);
        return questionnaireAudit;
    }

    @BeforeEach
    public void initTest() {
        questionnaireAuditRepository.deleteAll();
        questionnaireAudit = createEntity();
    }

    @Test
    void createQuestionnaireAudit() throws Exception {
        int databaseSizeBeforeCreate = questionnaireAuditRepository.findAll().size();
        // Create the QuestionnaireAudit
        restQuestionnaireAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isCreated());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeCreate + 1);
        QuestionnaireAudit testQuestionnaireAudit = questionnaireAuditList.get(questionnaireAuditList.size() - 1);
        assertThat(testQuestionnaireAudit.getQuestionnaireId()).isEqualTo(DEFAULT_QUESTIONNAIRE_ID);
        assertThat(testQuestionnaireAudit.getActionBy()).isEqualTo(DEFAULT_ACTION_BY);
        assertThat(testQuestionnaireAudit.getActionType()).isEqualTo(DEFAULT_ACTION_TYPE);
        assertThat(testQuestionnaireAudit.getActionAt()).isEqualTo(DEFAULT_ACTION_AT);
        assertThat(testQuestionnaireAudit.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void createQuestionnaireAuditWithExistingId() throws Exception {
        // Create the QuestionnaireAudit with an existing ID
        questionnaireAudit.setId("existing_id");

        int databaseSizeBeforeCreate = questionnaireAuditRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionnaireAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuestionnaireIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireAuditRepository.findAll().size();
        // set the field null
        questionnaireAudit.setQuestionnaireId(null);

        // Create the QuestionnaireAudit, which fails.

        restQuestionnaireAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionByIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireAuditRepository.findAll().size();
        // set the field null
        questionnaireAudit.setActionBy(null);

        // Create the QuestionnaireAudit, which fails.

        restQuestionnaireAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireAuditRepository.findAll().size();
        // set the field null
        questionnaireAudit.setActionType(null);

        // Create the QuestionnaireAudit, which fails.

        restQuestionnaireAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActionAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireAuditRepository.findAll().size();
        // set the field null
        questionnaireAudit.setActionAt(null);

        // Create the QuestionnaireAudit, which fails.

        restQuestionnaireAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkClientIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireAuditRepository.findAll().size();
        // set the field null
        questionnaireAudit.setClient(null);

        // Create the QuestionnaireAudit, which fails.

        restQuestionnaireAuditMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllQuestionnaireAudits() throws Exception {
        // Initialize the database
        questionnaireAuditRepository.save(questionnaireAudit);

        // Get all the questionnaireAuditList
        restQuestionnaireAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionnaireAudit.getId())))
            .andExpect(jsonPath("$.[*].questionnaireId").value(hasItem(DEFAULT_QUESTIONNAIRE_ID.toString())))
            .andExpect(jsonPath("$.[*].actionBy").value(hasItem(DEFAULT_ACTION_BY.toString())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].actionAt").value(hasItem(sameInstant(DEFAULT_ACTION_AT))))
            .andExpect(jsonPath("$.[*].client").value(hasItem(DEFAULT_CLIENT.toString())));
    }

    @Test
    void getQuestionnaireAudit() throws Exception {
        // Initialize the database
        questionnaireAuditRepository.save(questionnaireAudit);

        // Get the questionnaireAudit
        restQuestionnaireAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, questionnaireAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(questionnaireAudit.getId()))
            .andExpect(jsonPath("$.questionnaireId").value(DEFAULT_QUESTIONNAIRE_ID.toString()))
            .andExpect(jsonPath("$.actionBy").value(DEFAULT_ACTION_BY.toString()))
            .andExpect(jsonPath("$.actionType").value(DEFAULT_ACTION_TYPE.toString()))
            .andExpect(jsonPath("$.actionAt").value(sameInstant(DEFAULT_ACTION_AT)))
            .andExpect(jsonPath("$.client").value(DEFAULT_CLIENT.toString()));
    }

    @Test
    void getNonExistingQuestionnaireAudit() throws Exception {
        // Get the questionnaireAudit
        restQuestionnaireAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingQuestionnaireAudit() throws Exception {
        // Initialize the database
        questionnaireAuditRepository.save(questionnaireAudit);

        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();

        // Update the questionnaireAudit
        QuestionnaireAudit updatedQuestionnaireAudit = questionnaireAuditRepository.findById(questionnaireAudit.getId()).get();
        updatedQuestionnaireAudit
            .questionnaireId(UPDATED_QUESTIONNAIRE_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT)
            .client(UPDATED_CLIENT);

        restQuestionnaireAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuestionnaireAudit.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuestionnaireAudit))
            )
            .andExpect(status().isOk());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
        QuestionnaireAudit testQuestionnaireAudit = questionnaireAuditList.get(questionnaireAuditList.size() - 1);
        assertThat(testQuestionnaireAudit.getQuestionnaireId()).isEqualTo(UPDATED_QUESTIONNAIRE_ID);
        assertThat(testQuestionnaireAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testQuestionnaireAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testQuestionnaireAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
        assertThat(testQuestionnaireAudit.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void putNonExistingQuestionnaireAudit() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();
        questionnaireAudit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionnaireAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionnaireAudit.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchQuestionnaireAudit() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();
        questionnaireAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamQuestionnaireAudit() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();
        questionnaireAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireAuditMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateQuestionnaireAuditWithPatch() throws Exception {
        // Initialize the database
        questionnaireAuditRepository.save(questionnaireAudit);

        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();

        // Update the questionnaireAudit using partial update
        QuestionnaireAudit partialUpdatedQuestionnaireAudit = new QuestionnaireAudit();
        partialUpdatedQuestionnaireAudit.setId(questionnaireAudit.getId());

        partialUpdatedQuestionnaireAudit.questionnaireId(UPDATED_QUESTIONNAIRE_ID).actionBy(UPDATED_ACTION_BY);

        restQuestionnaireAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionnaireAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestionnaireAudit))
            )
            .andExpect(status().isOk());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
        QuestionnaireAudit testQuestionnaireAudit = questionnaireAuditList.get(questionnaireAuditList.size() - 1);
        assertThat(testQuestionnaireAudit.getQuestionnaireId()).isEqualTo(UPDATED_QUESTIONNAIRE_ID);
        assertThat(testQuestionnaireAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testQuestionnaireAudit.getActionType()).isEqualTo(DEFAULT_ACTION_TYPE);
        assertThat(testQuestionnaireAudit.getActionAt()).isEqualTo(DEFAULT_ACTION_AT);
        assertThat(testQuestionnaireAudit.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void fullUpdateQuestionnaireAuditWithPatch() throws Exception {
        // Initialize the database
        questionnaireAuditRepository.save(questionnaireAudit);

        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();

        // Update the questionnaireAudit using partial update
        QuestionnaireAudit partialUpdatedQuestionnaireAudit = new QuestionnaireAudit();
        partialUpdatedQuestionnaireAudit.setId(questionnaireAudit.getId());

        partialUpdatedQuestionnaireAudit
            .questionnaireId(UPDATED_QUESTIONNAIRE_ID)
            .actionBy(UPDATED_ACTION_BY)
            .actionType(UPDATED_ACTION_TYPE)
            .actionAt(UPDATED_ACTION_AT)
            .client(UPDATED_CLIENT);

        restQuestionnaireAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionnaireAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestionnaireAudit))
            )
            .andExpect(status().isOk());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
        QuestionnaireAudit testQuestionnaireAudit = questionnaireAuditList.get(questionnaireAuditList.size() - 1);
        assertThat(testQuestionnaireAudit.getQuestionnaireId()).isEqualTo(UPDATED_QUESTIONNAIRE_ID);
        assertThat(testQuestionnaireAudit.getActionBy()).isEqualTo(UPDATED_ACTION_BY);
        assertThat(testQuestionnaireAudit.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
        assertThat(testQuestionnaireAudit.getActionAt()).isEqualTo(UPDATED_ACTION_AT);
        assertThat(testQuestionnaireAudit.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void patchNonExistingQuestionnaireAudit() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();
        questionnaireAudit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionnaireAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionnaireAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchQuestionnaireAudit() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();
        questionnaireAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamQuestionnaireAudit() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireAuditRepository.findAll().size();
        questionnaireAudit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireAuditMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(questionnaireAudit))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionnaireAudit in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteQuestionnaireAudit() throws Exception {
        // Initialize the database
        questionnaireAuditRepository.save(questionnaireAudit);

        int databaseSizeBeforeDelete = questionnaireAuditRepository.findAll().size();

        // Delete the questionnaireAudit
        restQuestionnaireAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, questionnaireAudit.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<QuestionnaireAudit> questionnaireAuditList = questionnaireAuditRepository.findAll();
        assertThat(questionnaireAuditList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
