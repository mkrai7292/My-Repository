package com.thg.procurement.questionnaire.service.web.rest;

import static com.thg.procurement.questionnaire.service.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thg.procurement.questionnaire.service.IntegrationTest;
import com.thg.procurement.questionnaire.service.domain.Answer;
import com.thg.procurement.questionnaire.service.repository.AnswerRepository;
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
 * Integration tests for the {@link AnswerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AnswerResourceIT {

    private static final UUID DEFAULT_QUESTIONNAIRE_ID = UUID.randomUUID();
    private static final UUID UPDATED_QUESTIONNAIRE_ID = UUID.randomUUID();

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

    private static final String DEFAULT_DATA = "AAAAAAAAAA";
    private static final String UPDATED_DATA = "BBBBBBBBBB";

    private static final UUID DEFAULT_CLIENT = UUID.randomUUID();
    private static final UUID UPDATED_CLIENT = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private MockMvc restAnswerMockMvc;

    private Answer answer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createEntity() {
        Answer answer = new Answer()
            .questionnaireId(DEFAULT_QUESTIONNAIRE_ID)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .data(DEFAULT_DATA)
            .client(DEFAULT_CLIENT);
        return answer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createUpdatedEntity() {
        Answer answer = new Answer()
            .questionnaireId(UPDATED_QUESTIONNAIRE_ID)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .data(UPDATED_DATA)
            .client(UPDATED_CLIENT);
        return answer;
    }

    @BeforeEach
    public void initTest() {
        answerRepository.deleteAll();
        answer = createEntity();
    }

    @Test
    void createAnswer() throws Exception {
        int databaseSizeBeforeCreate = answerRepository.findAll().size();
        // Create the Answer
        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isCreated());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate + 1);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getQuestionnaireId()).isEqualTo(DEFAULT_QUESTIONNAIRE_ID);
        assertThat(testAnswer.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testAnswer.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testAnswer.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testAnswer.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testAnswer.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testAnswer.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void createAnswerWithExistingId() throws Exception {
        // Create the Answer with an existing ID
        answer.setId("existing_id");

        int databaseSizeBeforeCreate = answerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuestionnaireIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setQuestionnaireId(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkIsDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setIsDeleted(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setCreatedBy(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setCreatedAt(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUpdatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setUpdatedBy(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUpdatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setUpdatedAt(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDataIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setData(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkClientIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setClient(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAnswers() throws Exception {
        // Initialize the database
        answerRepository.save(answer);

        // Get all the answerList
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId())))
            .andExpect(jsonPath("$.[*].questionnaireId").value(hasItem(DEFAULT_QUESTIONNAIRE_ID.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA)))
            .andExpect(jsonPath("$.[*].client").value(hasItem(DEFAULT_CLIENT.toString())));
    }

    @Test
    void getAnswer() throws Exception {
        // Initialize the database
        answerRepository.save(answer);

        // Get the answer
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, answer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(answer.getId()))
            .andExpect(jsonPath("$.questionnaireId").value(DEFAULT_QUESTIONNAIRE_ID.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY.toString()))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA))
            .andExpect(jsonPath("$.client").value(DEFAULT_CLIENT.toString()));
    }

    @Test
    void getNonExistingAnswer() throws Exception {
        // Get the answer
        restAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingAnswer() throws Exception {
        // Initialize the database
        answerRepository.save(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer
        Answer updatedAnswer = answerRepository.findById(answer.getId()).get();
        updatedAnswer
            .questionnaireId(UPDATED_QUESTIONNAIRE_ID)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .data(UPDATED_DATA)
            .client(UPDATED_CLIENT);

        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAnswer.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getQuestionnaireId()).isEqualTo(UPDATED_QUESTIONNAIRE_ID);
        assertThat(testAnswer.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAnswer.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testAnswer.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testAnswer.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testAnswer.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testAnswer.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void putNonExistingAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, answer.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        answerRepository.save(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer
            .questionnaireId(UPDATED_QUESTIONNAIRE_ID)
            .isDeleted(UPDATED_IS_DELETED)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getQuestionnaireId()).isEqualTo(UPDATED_QUESTIONNAIRE_ID);
        assertThat(testAnswer.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testAnswer.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testAnswer.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testAnswer.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testAnswer.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testAnswer.getClient()).isEqualTo(DEFAULT_CLIENT);
    }

    @Test
    void fullUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        answerRepository.save(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer
            .questionnaireId(UPDATED_QUESTIONNAIRE_ID)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .data(UPDATED_DATA)
            .client(UPDATED_CLIENT);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getQuestionnaireId()).isEqualTo(UPDATED_QUESTIONNAIRE_ID);
        assertThat(testAnswer.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAnswer.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testAnswer.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testAnswer.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testAnswer.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testAnswer.getClient()).isEqualTo(UPDATED_CLIENT);
    }

    @Test
    void patchNonExistingAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, answer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAnswer() throws Exception {
        // Initialize the database
        answerRepository.save(answer);

        int databaseSizeBeforeDelete = answerRepository.findAll().size();

        // Delete the answer
        restAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, answer.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
