package com.thg.procurement.questionnaire.service.web.rest;

import static com.thg.procurement.questionnaire.service.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thg.procurement.questionnaire.service.IntegrationTest;
import com.thg.procurement.questionnaire.service.domain.Questionnaire;
import com.thg.procurement.questionnaire.service.repository.QuestionnaireRepository;
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
 * Integration tests for the {@link QuestionnaireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuestionnaireResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final UUID DEFAULT_CREATED_BY = UUID.randomUUID();
    private static final UUID UPDATED_CREATED_BY = UUID.randomUUID();

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final UUID DEFAULT_UPDATED_BY = UUID.randomUUID();
    private static final UUID UPDATED_UPDATED_BY = UUID.randomUUID();

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_SCHEMA = "AAAAAAAAAA";
    private static final String UPDATED_SCHEMA = "BBBBBBBBBB";

    private static final UUID DEFAULT_CLIENT = UUID.randomUUID();
    private static final UUID UPDATED_CLIENT = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/questionnaires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private MockMvc restQuestionnaireMockMvc;

    private Questionnaire questionnaire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Questionnaire createEntity() {
        Questionnaire questionnaire = new Questionnaire()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .schema(DEFAULT_SCHEMA)
            .client(DEFAULT_CLIENT);
        return questionnaire;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Questionnaire createUpdatedEntity() {
        Questionnaire questionnaire = new Questionnaire()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .schema(UPDATED_SCHEMA)
            .client(UPDATED_CLIENT);
        return questionnaire;
    }

    @BeforeEach
    public void initTest() {
        questionnaireRepository.deleteAll();
        questionnaire = createEntity();
    }

    @Test
    void createQuestionnaire() throws Exception {
        int databaseSizeBeforeCreate = questionnaireRepository.findAll().size();
        // Create the Questionnaire
        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isCreated());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeCreate + 1);
        Questionnaire testQuestionnaire = questionnaireList.get(questionnaireList.size() - 1);
        assertThat(testQuestionnaire.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQuestionnaire.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testQuestionnaire.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testQuestionnaire.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testQuestionnaire.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testQuestionnaire.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testQuestionnaire.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testQuestionnaire.getSchema()).isEqualTo(DEFAULT_SCHEMA);
        assertThat(testQuestionnaire.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void createQuestionnaireWithExistingId() throws Exception {
        // Create the Questionnaire with an existing ID
        questionnaire.setId("existing_id");

        int databaseSizeBeforeCreate = questionnaireRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setName(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setDescription(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkIsDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setIsDeleted(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setCreatedBy(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setCreatedAt(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUpdatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setUpdatedBy(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUpdatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setUpdatedAt(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSchemaIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setSchema(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkClientIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionnaireRepository.findAll().size();
        // set the field null
        questionnaire.setClient(null);

        // Create the Questionnaire, which fails.

        restQuestionnaireMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllQuestionnaires() throws Exception {
        // Initialize the database
        questionnaireRepository.save(questionnaire);

        // Get all the questionnaireList
        restQuestionnaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionnaire.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].schema").value(hasItem(DEFAULT_SCHEMA)))
            .andExpect(jsonPath("$.[*].client").value(hasItem(DEFAULT_CLIENT.toString())));
    }

    @Test
    void getQuestionnaire() throws Exception {
        // Initialize the database
        questionnaireRepository.save(questionnaire);

        // Get the questionnaire
        restQuestionnaireMockMvc
            .perform(get(ENTITY_API_URL_ID, questionnaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(questionnaire.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY.toString()))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)))
            .andExpect(jsonPath("$.schema").value(DEFAULT_SCHEMA))
            .andExpect(jsonPath("$.client").value(DEFAULT_CLIENT.toString()));
    }

    @Test
    void getNonExistingQuestionnaire() throws Exception {
        // Get the questionnaire
        restQuestionnaireMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingQuestionnaire() throws Exception {
        // Initialize the database
        questionnaireRepository.save(questionnaire);

        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();

        // Update the questionnaire
        Questionnaire updatedQuestionnaire = questionnaireRepository.findById(questionnaire.getId()).get();
        updatedQuestionnaire
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .schema(UPDATED_SCHEMA)
            .client(UPDATED_CLIENT);

        restQuestionnaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuestionnaire.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuestionnaire))
            )
            .andExpect(status().isOk());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
        Questionnaire testQuestionnaire = questionnaireList.get(questionnaireList.size() - 1);
        assertThat(testQuestionnaire.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQuestionnaire.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testQuestionnaire.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testQuestionnaire.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testQuestionnaire.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testQuestionnaire.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testQuestionnaire.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testQuestionnaire.getSchema()).isEqualTo(UPDATED_SCHEMA);
        assertThat(testQuestionnaire.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void putNonExistingQuestionnaire() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();
        questionnaire.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionnaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionnaire.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchQuestionnaire() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();
        questionnaire.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamQuestionnaire() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();
        questionnaire.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateQuestionnaireWithPatch() throws Exception {
        // Initialize the database
        questionnaireRepository.save(questionnaire);

        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();

        // Update the questionnaire using partial update
        Questionnaire partialUpdatedQuestionnaire = new Questionnaire();
        partialUpdatedQuestionnaire.setId(questionnaire.getId());

        partialUpdatedQuestionnaire
            .description(UPDATED_DESCRIPTION)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);

        restQuestionnaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionnaire.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestionnaire))
            )
            .andExpect(status().isOk());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
        Questionnaire testQuestionnaire = questionnaireList.get(questionnaireList.size() - 1);
        assertThat(testQuestionnaire.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQuestionnaire.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testQuestionnaire.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testQuestionnaire.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testQuestionnaire.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testQuestionnaire.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testQuestionnaire.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testQuestionnaire.getSchema()).isEqualTo(DEFAULT_SCHEMA);
        assertThat(testQuestionnaire.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void fullUpdateQuestionnaireWithPatch() throws Exception {
        // Initialize the database
        questionnaireRepository.save(questionnaire);

        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();

        // Update the questionnaire using partial update
        Questionnaire partialUpdatedQuestionnaire = new Questionnaire();
        partialUpdatedQuestionnaire.setId(questionnaire.getId());

        partialUpdatedQuestionnaire
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .schema(UPDATED_SCHEMA)
            .client(UPDATED_CLIENT);

        restQuestionnaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionnaire.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestionnaire))
            )
            .andExpect(status().isOk());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
        Questionnaire testQuestionnaire = questionnaireList.get(questionnaireList.size() - 1);
        assertThat(testQuestionnaire.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQuestionnaire.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testQuestionnaire.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testQuestionnaire.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testQuestionnaire.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testQuestionnaire.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testQuestionnaire.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testQuestionnaire.getSchema()).isEqualTo(UPDATED_SCHEMA);
        assertThat(testQuestionnaire.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void patchNonExistingQuestionnaire() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();
        questionnaire.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionnaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionnaire.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchQuestionnaire() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();
        questionnaire.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamQuestionnaire() throws Exception {
        int databaseSizeBeforeUpdate = questionnaireRepository.findAll().size();
        questionnaire.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionnaireMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(questionnaire))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Questionnaire in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteQuestionnaire() throws Exception {
        // Initialize the database
        questionnaireRepository.save(questionnaire);

        int databaseSizeBeforeDelete = questionnaireRepository.findAll().size();

        // Delete the questionnaire
        restQuestionnaireMockMvc
            .perform(delete(ENTITY_API_URL_ID, questionnaire.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAll();
        assertThat(questionnaireList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
